
package iqcarecompanion.core.domain;

import static iqcarecompanion.core.domain.VisitFactory.getVisit;
import iqcarecompanion.core.entities.LabResult;
import iqcarecompanion.core.entities.LabTest;
import iqcarecompanion.core.entities.Visit;
import iqcarecompanion.core.utils.ConstantProperties;
import static iqcarecompanion.core.utils.ConstantProperties.DB_NAME;
import static iqcarecompanion.core.utils.ConstantProperties.LOG_PREFIX;
import iqcarecompanion.core.utils.DBConnector;
import static iqcarecompanion.core.utils.ResourceManager.updateLastId;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Teddy Odhiambo
 */
public class LabResultFactory {

    final static Logger logger = Logger.getLogger(LabResultFactory.class.getName());

    public static List<LabResult> getLabResults(int limit, String labResultId) {
        LabResult labResult;
        List<LabResult> labResults = new ArrayList<>();
        Connection dbConnection;
        PreparedStatement preparedStatement = null;
        ResultSet rs;
        ResultSet orderRs;
        int visitId = 0;
        final String LAST_LAB_ID_RECORDED = "labResultId";

        String[] splitLabtest = StringUtils.split(ConstantProperties.LAB_TESTS, ",");
        int totalLabTests = splitLabtest.length;
       
        StringBuilder sbSqlLabTestId = new StringBuilder();
        sbSqlLabTestId.append("(");
        for (int i = 0; i < totalLabTests; i++) {
            
           if(totalLabTests - i == 1){
                sbSqlLabTestId.append("LabTestID = ")
                        .append(splitLabtest[i]);
            } else {
                 sbSqlLabTestId.append("LabTestID = ")
                         .append(splitLabtest[i]).append(" or ");
            }
        }
        
        sbSqlLabTestId.append(")");
        StringBuilder sbSqlLabResults = new StringBuilder();
        sbSqlLabResults.append("SELECT TOP ")
                .append(limit)
                .append(" LabID,LabTestID,TestResults,CreateDate\n");
        
        sbSqlLabResults.append("  FROM ")
                .append(DB_NAME)
                .append(".dbo.dtl_PatientLabResults where LabID > ")
                .append(labResultId)
                .append(" and \n");
        sbSqlLabResults.append(sbSqlLabTestId);
        String sql = sbSqlLabResults.toString();
        int final_lab_id = 0;

        try {
            dbConnection = DBConnector.connectionInstance();
            preparedStatement = dbConnection.prepareStatement(sql);
            rs = preparedStatement.executeQuery();

            while (rs.next()) {
                if (rs.getBigDecimal("TestResults") != null) {
                    labResult = new LabResult();

                    int labTestId = rs.getInt("LabTestID");
                    switch (labTestId) {
                        case 1:
                            labResult.setLabTest(LabTest.CD4_COUNT);
                            break;
                        case 2:
                            labResult.setLabTest(LabTest.CD4_PERCENT);
                            break;
                        case 3:
                            labResult.setLabTest(LabTest.VIRAL_LOAD);
                            break;
                    }

                    labResult.setResult(rs.getBigDecimal("TestResults").toString());
                    labResult.setResultDate(rs.getTimestamp("CreateDate"));
                    int labID = rs.getInt("LabID");
                    StringBuilder sbSqlLabOrder = new StringBuilder();
                    sbSqlLabOrder.append("SELECT Ptn_pk,VisitId FROM ")
                            .append(ConstantProperties.DB_NAME)
                            .append(".dbo.ord_PatientLabOrder where [LabID] = ")
                            .append(labID);
                    
                    preparedStatement = dbConnection.prepareStatement(sbSqlLabOrder.toString());
                    orderRs = preparedStatement.executeQuery();
                    while (orderRs.next()) {
                        visitId = orderRs.getInt("VisitId");
                    }
                    Visit visit = getVisit(visitId);
                    labResult.setVisit(visit);
                    labResults.add(labResult);

                    if (rs.isLast()) {
                        final_lab_id = labID;
                    }
                }
            }
        } catch (SQLException e) {
            StringBuilder sb = new StringBuilder();
            sb.append(LOG_PREFIX)
                    .append(" An error occurred during the execution of the following query:\n")
                    .append(sql);
            logger.log(Level.SEVERE,sb.toString(),e);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ex) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(LOG_PREFIX)
                            .append("The following issue is preventing the preparedStatement from closing:\n");
                    logger.log(Level.SEVERE, sb.toString() , ex);
                }
            }
        }
        updateLastId(final_lab_id, LAST_LAB_ID_RECORDED);

        return labResults;
    }

}
