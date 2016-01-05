
package iqcarecompanion.core.dao;

import iqcarecompanion.core.entities.Observation;
import iqcarecompanion.core.entities.Visit;
import iqcarecompanion.core.jsonmapper.Event;
import iqcarecompanion.core.utils.DateUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import static java.sql.ResultSet.CONCUR_READ_ONLY;
import static java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE;
import java.sql.SQLException;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Teddy Odhiambo
 */
public class ObservationDao {

    private final Connection connection;
    private final String dbName;
    
    public ObservationDao(Connection connection, String dbName){
        this.connection = connection;
        this.dbName = dbName;
    }
    
    public Observation getObservation(Event event, Visit visit) throws SQLException {
        StringBuilder sbSql = new StringBuilder();
        if(StringUtils.isNotEmpty(dbName)){
            sbSql.append("USE ").append(dbName).append("\n");
        }
        if (StringUtils.isEmpty(event.visitIdColumn)) {
            sbSql.append("SELECT * FROM ")
                    .append(event.tableName)
                    .append(" where Ptn_pk = ")
                    .append(visit.getPatientId())
                    .append(" and ").append(event.eventValueColumn)
                    .append(" = ").append(event.transformations[0]);
            
        } else {
            sbSql.append("SELECT * FROM ")
                    .append(event.tableName)
                    .append(" where ")
                    .append(event.visitIdColumn)
                    .append(" = ")
                    .append(visit.getVisitId());
        }
        String sql = sbSql.toString();
        
        PreparedStatement preparedStatement = connection.prepareStatement(sql,
                TYPE_SCROLL_INSENSITIVE, CONCUR_READ_ONLY);
        ResultSet rs = preparedStatement.executeQuery();
        Observation observation = null;
        while(rs.next()){
            if (rs.getObject(event.eventValueColumn) == null) {
                continue;
            }
            observation = new Observation();
            observation.setObservationDate(DateUtil.parseDate(visit.getVisitDate()));
            observation.setObservationName(event.eventName);
            if(StringUtils.isNotEmpty(event.eventDateColumn) &&
                    rs.getDate(event.eventDateColumn) != null){
                observation.setEventDate(DateUtil.parseDate(rs.getDate(event.eventDateColumn)));
            }
            String value = rs.getObject(event.eventValueColumn).toString();
            observation.setObservationValue(value);
            observation.setVisit(visit);
        }
        return observation;
    }
    
}
