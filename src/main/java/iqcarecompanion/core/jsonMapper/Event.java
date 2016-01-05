
package iqcarecompanion.core.jsonmapper;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

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
    public final String visitIdColumn;
    public final String[] transformations;

    @JsonCreator
    public Event(@JsonProperty("eventName") String eventName, @JsonProperty("tableName") String tableName,
            @JsonProperty("eventValueColumn") String eventValueColumn, @JsonProperty("eventDateColumn") String eventDateColumn,
            @JsonProperty("visitIdColumn") String visitIdColumn, @JsonProperty("transformations") String[] transformations){
        this.eventName = eventName;
        this.tableName = tableName;
        this.eventValueColumn = eventValueColumn;
        this.eventDateColumn = eventDateColumn;
        this.visitIdColumn = visitIdColumn;
        this.transformations = transformations;
    }
    
}


