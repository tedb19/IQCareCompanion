
package iqcarecompanion.core.dao;

import iqcarecompanion.core.entities.LabResult;
import iqcarecompanion.core.entities.LabTest;
import iqcarecompanion.core.entities.Visit;
import static iqcarecompanion.core.utils.ConstantProperties.LAB_TESTS;
import static iqcarecompanion.core.utils.ResourceManager.updateLastId;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Teddy Odhiambo
 */
public class LabResultDao {

    private final Connection connection;
    
    public LabResultDao(Connection connection){
        this.connection = connection;
    }

    public List<LabResult> getLabResults(int limit, String labResultId, String propertyKey, String dbName) throws SQLException {
        List<LabResult> labResults = new ArrayList<>();
        StringBuilder sbSqlLabResults = new StringBuilder();
        int finalLabId = 0;
        
        if(StringUtils.isNotEmpty(dbName)){
            sbSqlLabResults.append("USE ").append(dbName).append("\n");
        }
        sbSqlLabResults.append("SELECT TOP ")
                .append(limit)
                .append(" LabID,LabTestID,TestResults,CreateDate")
                .append("  FROM ")
                .append("dtl_PatientLabResults where LabID > ")
                .append(labResultId)
                .append(" and LabTestID IN (")
                .append(LAB_TESTS).append(")");
        String sql = sbSqlLabResults.toString();
        PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
        ResultSet rs = preparedStatement.executeQuery();
        
        while (rs.next()) {
            if (rs.getBigDecimal("TestResults") == null) {
                continue;
            }
            LabResult labResult = new LabResult();
            labResult = setLabTest(rs, labResult);

            labResult.setResult(rs.getBigDecimal("TestResults").toString());
            labResult.setResultDate(rs.getTimestamp("CreateDate"));
            int labID = rs.getInt("LabID");
            labResult.setVisit(getLabOrderVisit(labID, dbName));
            labResults.add(labResult);
            
            if (rs.isLast()) {
                finalLabId = labID;
            }
        }
        updateLastId(finalLabId, propertyKey);

        return labResults;
    }
    
    private Visit getLabOrderVisit(int labId, String dbName) throws SQLException{
        int visitId = 0;
        VisitDao dao = new VisitDao(this.connection);
        StringBuilder sbSql = new StringBuilder();
        if(StringUtils.isNotEmpty(dbName)){
            sbSql.append("USE ").append(dbName).append("\n");
        }
        sbSql.append("SELECT Ptn_pk,VisitId FROM ")
                .append("ord_PatientLabOrder where LabID = ")
                .append(labId);

        PreparedStatement preparedStatement = this.connection.prepareStatement(sbSql.toString());
        ResultSet orderRs = preparedStatement.executeQuery();
        while (orderRs.next()) {
            visitId = orderRs.getInt("VisitId");
        }
        return dao.getVisit(visitId, dbName);
    }
    
    public LabResult setLabTest(ResultSet rs, LabResult labResult) throws SQLException{
        /*
        * The mapping of lab Ids to the Lab Tests is 
        * currently hard-coded
        */
       switch (rs.getInt("LabTestID")) {
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
       return labResult;
    }

}
