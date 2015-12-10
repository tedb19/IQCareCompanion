
package iqcarecompanion.core.dao;

import iqcarecompanion.core.entities.LabResult;
import iqcarecompanion.core.entities.LabTest;
import iqcarecompanion.core.entities.Visit;
import iqcarecompanion.core.utils.ConstantProperties;
import static iqcarecompanion.core.utils.ConstantProperties.DB_NAME;
import static iqcarecompanion.core.utils.ConstantProperties.LOG_PREFIX;
import static iqcarecompanion.core.utils.DBConnector.connectionInstance;
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
public class LabResultDao {

    private static final Logger LOGGER = Logger.getLogger(LabResultDao.class.getName());
    private static final String LAST_LAB_ID_RECORDED = "labResultId";
    private final Connection connection;
    
    public LabResultDao(Connection connection){
        this.connection = connection;
    }

    public List<LabResult> getLabResults(int limit, String labResultId) {
        VisitDao dao = new VisitDao(connectionInstance());
        LabResult labResult;
        List<LabResult> labResults = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet rs;
        ResultSet orderRs;
        int visitId = 0;
        

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
        int finalLabId = 0;

        try {
            preparedStatement = this.connection.prepareStatement(sql);
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
                        default:
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
                    
                    preparedStatement = this.connection.prepareStatement(sbSqlLabOrder.toString());
                    orderRs = preparedStatement.executeQuery();
                    while (orderRs.next()) {
                        visitId = orderRs.getInt("VisitId");
                    }
                    Visit visit = dao.getVisit(visitId);
                    labResult.setVisit(visit);
                    labResults.add(labResult);

                    if (!rs.next()) {
                        finalLabId = labID;
                    }
                }
            }
        } catch (SQLException e) {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append(LOG_PREFIX)
                        .append(" An error occurred during the execution of the following query:\n")
                        .append(sql).append("Connection is Closed: ").append(this.connection.isClosed());
                LOGGER.log(Level.SEVERE,sb.toString(),e);
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ex) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(LOG_PREFIX)
                            .append("The following issue is preventing the preparedStatement from closing:\n");
                    LOGGER.log(Level.SEVERE, sb.toString() , ex);
                }
            }
        }
        updateLastId(finalLabId, LAST_LAB_ID_RECORDED);

        return labResults;
    }

}
