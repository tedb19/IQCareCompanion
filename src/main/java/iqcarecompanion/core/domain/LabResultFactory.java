
package iqcarecompanion.core.domain;

import static iqcarecompanion.core.domain.VisitFactory.getVisit;
import iqcarecompanion.core.entities.LabResult;
import iqcarecompanion.core.entities.LabTest;
import iqcarecompanion.core.entities.Visit;
import iqcarecompanion.core.utils.DBConnector;
import iqcarecompanion.core.utils.ResourceManager;
import static iqcarecompanion.core.utils.ResourceManager.updateLastId;
import java.io.IOException;
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

    public static List<LabResult> getLabResults(int limit, String dbName, String labResultId, String labTests) throws SQLException {
        LabResult labResult;
        List<LabResult> labResults = new ArrayList<>();
        Connection dbConnection;
        PreparedStatement preparedStatement = null;
        ResultSet rs;
        ResultSet orderRs;
        int visitId = 0;
        final String LAST_LAB_ID_RECORDED = "labResultId";

        String[] splitLabtest = StringUtils.split(labTests, ",");
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
                .append(dbName)
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
                            .append(dbName)
                            .append(".dbo.ord_PatientLabOrder where [LabID] = ")
                            .append(labID);
                    
                    preparedStatement = dbConnection.prepareStatement(sbSqlLabOrder.toString());
                    orderRs = preparedStatement.executeQuery();
                    while (orderRs.next()) {
                        visitId = orderRs.getInt("VisitId");
                    }
                    Visit visit = getVisit(visitId, dbName);
                    labResult.setVisit(visit);
                    labResults.add(labResult);

                    if (rs.isLast()) {
                        final_lab_id = labID;
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.toString(), e);
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }

        }

        updateLastId(final_lab_id, LAST_LAB_ID_RECORDED);

        return labResults;
    }

}