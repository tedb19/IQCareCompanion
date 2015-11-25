
package iqcarecompanion.core.domain;

import iqcarecompanion.core.entities.Visit;
import iqcarecompanion.core.utils.DBConnector;
import iqcarecompanion.core.utils.ResourceManager;
import java.io.IOException;
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
   
    public static Visit getVisit(int visit_id, String dbName) throws SQLException {
        Visit visit = null;
        Connection dbConnection;
        PreparedStatement preparedStatement = null;
        ResultSet rs;
        
        String sql = "SELECT * FROM " + dbName + ".dbo.ord_Visit as visit WHERE visit.Visit_Id = ?";

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
            logger.log(Level.SEVERE, e.toString(), e);
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }

        }
        return visit;
    }

    public static List<Visit> getVisits(int limit, String lastVisitId,String dbName) throws SQLException {
        List<Visit> visits = new ArrayList<>();
        Visit visit;
        Connection dbConnection;
        PreparedStatement preparedStatement = null;
        ResultSet rs;
        int final_visit_id = 0;
        
        String sql = "SELECT top " + limit + " Visit_Id,Ptn_Pk,VisitDate FROM " + dbName;
        sql += ".dbo.ord_Visit as visits WHERE ";
        sql += " visits.Visit_Id > ?";
        sql += "order by Visit_Id asc ";

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
            logger.log(Level.SEVERE, e.toString(), e);
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }

        }
        updateVisitId(final_visit_id);

        return visits;
    }
    
    private static void updateVisitId(int finalVisitId){
        if (finalVisitId != 0) {
            try {
                ResourceManager.modifyConfigFile("last_visit_id", Integer.toString(finalVisitId), "runtime.properties");
            } catch (IOException ex) {
                logger.log(Level.SEVERE, ex.toString(), ex);
            }
        }
    }
}
