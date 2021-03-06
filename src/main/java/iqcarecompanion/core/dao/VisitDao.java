
package iqcarecompanion.core.dao;

import iqcarecompanion.core.entities.Visit;
import static iqcarecompanion.core.utils.ResourceManager.updateLastId;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import static java.sql.ResultSet.CONCUR_READ_ONLY;
import static java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.plexus.util.StringUtils;

/**
 *
 * @author Teddy Odhiambo
 */
public class VisitDao {

    private final Connection connection;
    private final String dbName;
    
    public VisitDao(Connection connection, String dbName){
        this.connection = connection;
        this.dbName = dbName;
    }
   
    public Visit getVisit(int visitId) throws SQLException {
        Visit visit = null;
        StringBuilder sbSql = new StringBuilder();
        
        if(StringUtils.isNotEmpty(dbName)){
            sbSql.append("USE ").append(dbName).append("\n");
        }
        sbSql.append("SELECT * FROM ")
                .append("ord_Visit WHERE Visit_Id = ")
                .append(visitId);
        
        PreparedStatement preparedStatement = this.connection.prepareStatement(sbSql.toString());
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            visit = new Visit();
            visit.setPatientId(rs.getInt("Ptn_Pk"));
            visit.setVisitDate(rs.getTimestamp("VisitDate"));
            visit.setVisitId(visitId);
        }
        return visit;
    }

    private int lastVisitId() throws SQLException {
        StringBuilder sbSql = new StringBuilder();
        
        if(StringUtils.isNotEmpty(dbName)){
            sbSql.append("USE ").append(dbName).append("\n");
        }
        sbSql.append("SELECT MAX(Visit_Id) as lastId FROM ord_Visit");
        
        PreparedStatement preparedStatement = this.connection.prepareStatement(sbSql.toString(),
                TYPE_SCROLL_INSENSITIVE, CONCUR_READ_ONLY);
        ResultSet rs = preparedStatement.executeQuery();
        int lastId = 0;
        while(rs.next()){
            lastId = rs.getInt("lastId");
        }
        return lastId;
    }
    
    /*
     * We dont pick the last visit, since the assumption is that the data clerk 
     * is still putting in the data, thus the record is incomplete until we get
     * to the next visit.
     * Currently this is the only sure way of ensuring we pick a complete visit.
     */
    public List<Visit> getVisits(int limit, String lastVisitId, String propertyKey) throws SQLException {
        List<Visit> visits = new ArrayList<>();
        int finalVisitId = 0;
        StringBuilder sbSql = new StringBuilder();
        
        if(StringUtils.isNotEmpty(dbName)){
            sbSql.append("USE ").append(dbName).append("\n");
        }
        sbSql.append("SELECT top ")
                .append(limit)
                .append(" Visit_Id,Ptn_Pk,VisitDate FROM ")
                .append("ord_Visit WHERE ")
                .append(" Visit_Id > ").append(lastVisitId)
                .append("order by Visit_Id asc ");
        
        PreparedStatement preparedStatement = this.connection.prepareStatement(sbSql.toString(),
                TYPE_SCROLL_INSENSITIVE, CONCUR_READ_ONLY);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            Visit visit = new Visit();
            visit.setPatientId(rs.getInt("Ptn_Pk"));
            visit.setVisitDate(rs.getTimestamp("VisitDate"));
            visit.setVisitId(rs.getInt("Visit_Id"));
            visits.add(visit);

            if (rs.isLast()) {
                finalVisitId = visit.getVisitId();
            }
            if(visit.getVisitId() == lastVisitId()){
                visits.remove(visit);
                break;
            }
        }
        updateLastId(finalVisitId, propertyKey);

        return visits;
    }
    
    
}
