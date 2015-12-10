
package iqcarecompanion.core.jsonmapper;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;

/**
 *
 * @author Teddy
 * This class was generated at http://timboudreau.com/blog/json/read using a sample json
 */

public final class Event {
    public final String eventName;
    public final String tableName;
    public final String eventValueColumn;
    public final String eventDateColumn;
    public final String eventValueDataType;
    public final String visitIdColumn;
    public final String[] transformations;

    @JsonCreator
    public Event(@JsonProperty("eventName") String eventName, @JsonProperty("tableName") String tableName, @JsonProperty("eventValueColumn") String eventValueColumn, @JsonProperty("eventDateColumn") String eventDateColumn, @JsonProperty("eventValueDataType") String eventValueDataType, @JsonProperty("visitIdColumn") String visitIdColumn, @JsonProperty("transformations") String[] transformations){
        this.eventName = eventName;
        this.tableName = tableName;
        this.eventValueColumn = eventValueColumn;
        this.eventDateColumn = eventDateColumn;
        this.eventValueDataType = eventValueDataType;
        this.visitIdColumn = visitIdColumn;
        this.transformations = transformations;
    }

    @Override
    public String toString() {
        return "Event{" + "eventName=" + eventName + ", tableName=" + tableName + ", eventValueColumn=" + eventValueColumn + ", eventDateColumn=" + eventDateColumn + ", eventValueDataType=" + eventValueDataType + ", visitIdColumn=" + visitIdColumn + ", transformations=" + Arrays.toString(transformations) + '}';
    }
    
}


