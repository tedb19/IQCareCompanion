
package iqcarecompanion.core.domain;

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

/**
 *
 * @author Teddy Odhiambo
 */
public class VisitFactory {
    
    final static Logger logger = Logger.getLogger(VisitFactory.class.getName());
   
    public static Visit getVisit(int visit_id) throws SQLException {
        Visit visit = null;
        Connection dbConnection;
        PreparedStatement preparedStatement = null;
        ResultSet rs;
        
        StringBuilder sbSql = new StringBuilder();
        sbSql.append("SELECT * FROM ")
                .append(DB_NAME)
                .append(".dbo.ord_Visit as visit WHERE visit.Visit_Id = ?");
        String sql = sbSql.toString();
        try {
            dbConnection = DBConnector.connectionInstance();
            preparedStatement = dbConnection.prepareStatement(sql);
            preparedStatement.setInt(1, visit_id);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                visit = new Visit();
                visit.setPatientId(rs.getInt("Ptn_Pk"));
                visit.setVisitDate(rs.getTimestamp("VisitDate"));
                visit.setVisitId(rs.getInt("Visit_Id"));

            }
        } catch (SQLException e) {
            StringBuilder sb = new StringBuilder();
            sb.append(LOG_PREFIX)
                    .append(" An error occurred during the execution of the following query:\n")
                    .append(sql);
            logger.log(Level.SEVERE,sb.toString(),e);
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }

        }
        return visit;
    }

    public static List<Visit> getVisits(int limit, String lastVisitId) {
        List<Visit> visits = new ArrayList<>();
        Visit visit;
        Connection dbConnection;
        PreparedStatement preparedStatement = null;
        ResultSet rs;
        int final_visit_id = 0;
        final String LAST_VISIT_ID_RECORDED = "last_visit_id";//the key in the properties file
        
        StringBuilder sbSql = new StringBuilder();
        sbSql.append("SELECT top ")
                .append(limit)
                .append(" Visit_Id,Ptn_Pk,VisitDate FROM ")
                .append(DB_NAME)
                .append(".dbo.ord_Visit as visits WHERE ")
                .append(" visits.Visit_Id > ?")
                .append("order by Visit_Id asc ");
        String sql = sbSql.toString();
        try {
            dbConnection = DBConnector.connectionInstance();
            preparedStatement = dbConnection.prepareStatement(sql);
            preparedStatement.setString(1, lastVisitId);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                visit = new Visit();
                visit.setPatientId(rs.getInt("Ptn_Pk"));
                visit.setVisitDate(rs.getTimestamp("VisitDate"));
                visit.setVisitId(rs.getInt("Visit_Id"));
                visits.add(visit);

                if (rs.isLast()) {
                    final_visit_id = visit.getVisitId();
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
                    logger.log(Level.SEVERE, sb.toString(), ex);
                }
            }
        }
        updateLastId(final_visit_id, LAST_VISIT_ID_RECORDED);

        return visits;
    }
    
    
}
