
package iqcarecompanion.core.dao;

import iqcarecompanion.core.entities.Observation;
import iqcarecompanion.core.entities.Visit;
import iqcarecompanion.core.jsonmapper.Event;
import static iqcarecompanion.core.utils.ConstantProperties.DB_NAME;
import static iqcarecompanion.core.utils.ConstantProperties.LOG_PREFIX;
import iqcarecompanion.core.utils.DateUtil;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Teddy Odhiambo
 */
public class ObservationDao {
    
    private static final Logger LOGGER = Logger.getLogger(ObservationDao.class.getName());
    private final Connection connection;
    
    public ObservationDao(Connection connection){
        this.connection = connection;
    }
    
    public Observation getObservation(Event event, Visit visit) throws SQLException {
        Date eventDate = null;
        StringBuilder sbSql = new StringBuilder();
        if (StringUtils.isEmpty(event.visitIdColumn)) {
            sbSql.append("SELECT * FROM ")
                    .append(DB_NAME)
                    .append(".dbo.")
                    .append(event.tableName)
                    .append(" where Ptn_pk = ")
                    .append(visit.getPatientId());
            
        } else {
            sbSql.append("SELECT * FROM ")
                    .append(DB_NAME)
                    .append(".dbo.")
                    .append(event.tableName)
                    .append(" where ")
                    .append(event.visitIdColumn)
                    .append(" = ")
                    .append(visit.getVisitId());
        }
        String sql = sbSql.toString();
        
        PreparedStatement preparedStatement = this.connection.prepareStatement(sql,
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

        ResultSet rs = preparedStatement.executeQuery();
        if (!rs.isBeforeFirst()) {
            return null;
        }
        rs.first();
        Observation observation = new Observation();
        observation.setObservationDate(DateUtil.parseDate(visit.getVisitDate()));
        observation.setObservationName(event.eventName);
        if(StringUtils.isNotEmpty(event.eventDateColumn)){
                eventDate = rs.getDate(event.eventDateColumn);
        }
        if (rs.getObject(event.eventValueColumn) == null) {
            return null;
        }

        String observationValue = setObservationValue(event.eventValueDataType, event.eventValueColumn, rs);
        observation = setTransformations(
                observation,
                observationValue,
                event,
                eventDate,
                visit
        );
        
        return observation;
    }
    
    /*
    * This method processes the transformations appropriately using the following rules:
    * - If an event has one transformation listed, then this is a transformation on the event itself, and not on the event value.
    *   For such events, we pick the event's datetime as the event value
    * - If an event has no transformations, no transformation is done, and the event value is picked as is
    * - If an event has multiple transformers, we split it by the ":" character, and form a key-value pair.
    *   The key represents what is in the EMR database, while the value represents what we need to send to HQ.
    *   Look at the events.txt file to have a better understanding
    */
    private Observation setTransformations(Observation observation, String observationValue, Event event, Date eventDate, Visit visit){
        int totalTransformations = event.transformations.length;

        if (totalTransformations > 0) {

            String[] splitTransformer;
            for (String transformation : event.transformations) {
                if (totalTransformations == 1 && observationValue.equals(transformation)) {
                    observation.setObservationValue(DateUtil.parseDate(eventDate));
                } else {
                    splitTransformer = transformation.split(":");
                    String key = splitTransformer[0];
                    String value = splitTransformer[1];
                    
                    if (observationValue.equalsIgnoreCase(key)) {
                        observation.setObservationValue(value);
                        observation = checkRegimenEvent(event, visit, observation);
                    }
                }
            }
        } else {
            observation.setObservationValue(observationValue);
        }
        return observation;
    }
    
    private static String setObservationValue(String eventValueDataType, String eventValueColumn, ResultSet rs) throws SQLException{
        String observationValue;
        switch (eventValueDataType) {
            case "INT":
                observationValue = Integer.toString(rs.getInt(eventValueColumn));
                break;
            case "STRING":
                observationValue = rs.getString(eventValueColumn);
                break;
            case "DATE":
                observationValue = DateUtil.parseDate(rs.getTimestamp(eventValueColumn));
                break;
            case "DOUBLE":
                observationValue = Double.toString(rs.getDouble(eventValueColumn));
                break;
            case "DECIMAL":
                observationValue = (rs.getBigDecimal(eventValueColumn)).toString();
                break;
            default:
                observationValue = rs.getObject(eventValueColumn).toString();
                break;
        }
        
        return observationValue;
    }
    
    private Observation checkRegimenEvent(Event event, Visit visit, Observation observation){
        if (StringUtils.equals(event.eventName, "FIRST_LINE_REGIMEN") ||
                StringUtils.equals(event.eventName, "SECOND_LINE_REGIMEN")) {
            RegimenDao regimenDao = new RegimenDao(this.connection);
            String regimen = null;
            try {
                regimen = regimenDao.getCurrentRegimen(visit.getVisitId(), DB_NAME);
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "An error occurred during the execution of the following query:" , ex);
            }
            observation.setObservationValue(regimen);
        }
        return observation;
    }
}
