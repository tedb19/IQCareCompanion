
package iqcarecompanion.core.domain;

import static iqcarecompanion.core.domain.RegimenFactory.getCurrentRegimen;
import iqcarecompanion.core.entities.Observation;
import iqcarecompanion.core.entities.Visit;
import iqcarecompanion.core.jsonMapper.Event;
import iqcarecompanion.core.utils.ConstantProperties;
import static iqcarecompanion.core.utils.ConstantProperties.LOG_PREFIX;
import iqcarecompanion.core.utils.DBConnector;
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
public class ObservationFactory {
    
    final static Logger logger = Logger.getLogger(ObservationFactory.class.getName());
    
    public static Observation getObservation(Event event, Visit visit) throws SQLException {
        Connection dbConnection;
        Observation observation = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs;
        String sql;
        String observationValue = "";
        Date eventDate;
        StringBuilder sbSql = new StringBuilder();
        if (StringUtils.equals(event.visitIdColumn, "")) {
            sbSql.append("SELECT * FROM ")
                    .append(ConstantProperties.DB_NAME)
                    .append(".dbo.")
                    .append(event.tableName)
                    .append(" where Ptn_pk = ")
                    .append(visit.getPatientId());
            
        } else {
            sbSql.append("SELECT * FROM ")
                    .append(ConstantProperties.DB_NAME)
                    .append(".dbo.")
                    .append(event.tableName)
                    .append(" where ")
                    .append(event.visitIdColumn)
                    .append(" = ")
                    .append(visit.getVisitId());
        }
        sql = sbSql.toString();
        
        try {
            dbConnection = DBConnector.connectionInstance();
            preparedStatement = dbConnection.prepareStatement(sql,
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            rs = preparedStatement.executeQuery();
            if (rs.isBeforeFirst()) {
                rs.first();
                observation = new Observation();
                observation.setObservationDate(DateUtil.parseDate(visit.getVisitDate()));
                observation.setObservationName(event.eventName);
                String datatype = event.eventValueDataType;
                eventDate = rs.getDate(event.eventDateColumn);
                if (rs.getObject(event.eventValueColumn) != null) {
                    switch (datatype) {
                        case "INT":
                            observationValue = Integer.toString(rs.getInt(event.eventValueColumn));
                            break;
                        case "STRING":
                            observationValue = rs.getString(event.eventValueColumn);
                            break;
                        case "DATE":
                            observationValue = DateUtil.parseDate(rs.getTimestamp(event.eventValueColumn));
                            break;
                        case "DOUBLE":
                            observationValue = Double.toString(rs.getDouble(event.eventValueColumn));
                            break;
                        case "DECIMAL":
                            observationValue = (rs.getBigDecimal(event.eventValueColumn)).toString();
                            break;
                    }
                }
                
                observation = setTransformations(observation, observationValue, event, eventDate, visit);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"{0} An error occured during the execution of the followiing query:\n{1}\n{2}" ,
                    new Object[]{LOG_PREFIX, sql, e.toString()});
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
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
    private static Observation setTransformations(Observation observation, String observationValue, Event event, Date eventDate, Visit visit){
        if (!StringUtils.isEmpty(observationValue)) {
            int totalTransformations = event.transformations.length;

            if (totalTransformations > 0) {

                String[] splitTransformer;
                for (String transformation : event.transformations) {
                    if (totalTransformations == 1) {
                        if (observationValue.equals(transformation)) {
                            observation.setObservationValue(DateUtil.parseDate(eventDate));
                        }
                    } else {
                        splitTransformer = transformation.split(":");
                        String key = splitTransformer[0];
                        String value = splitTransformer[1];
                        if (observationValue.equalsIgnoreCase(key)) {
                            if (StringUtils.equals(event.eventName, "FIRST_LINE_REGIMEN") || StringUtils.equals(event.eventName, "SECOND_LINE_REGIMEN")) {
                                String regimen = getCurrentRegimen(visit.getVisitId());
                                observation.setObservationValue(regimen);
                            } else {
                                observation.setObservationValue(value);
                            }
                        }
                    }
                }
            } else {
                observation.setObservationValue(observationValue);
            }
        } else {
            observation = null;
        }
        return observation;
    }
    
   
}
