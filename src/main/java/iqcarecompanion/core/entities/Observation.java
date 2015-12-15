/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iqcarecompanion.core.entities;

import iqcarecompanion.core.jsonmapper.Event;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Teddy
 */
public class Observation {
    private String observationName;
    private String observationValue;
    private String observationDate;
    private String eventDate;
    private Visit visit;

    public Visit getVisit() {
        return visit;
    }

    public void setVisit(Visit visit) {
        this.visit = visit;
    }

    
    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getObservationName() {
        return observationName;
    }

    public void setObservationName(String observationName) {
        this.observationName = observationName;
    }

    public String getObservationValue() {
        return observationValue;
    }

    public void setObservationValue(String observationValue) {
        this.observationValue = observationValue;
    }

    public String getObservationDate() {
        return observationDate;
    }

    public void setObservationDate(String observationDate) {
        this.observationDate = observationDate;
    }

    @Override
    public String toString(){
      return this.observationName + " "+ this.observationValue;  
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
    public void setTransformations(Event event){
        int totalTransformations = event.transformations.length;
        
        if (totalTransformations == 0) {
            return;
        }
        for (String transformation : event.transformations) {
            String[] splitTransformer = transformation.split(":");
            if(splitTransformer.length == 1){
                this.observationValue = this.eventDate;
            } else {
                String key = splitTransformer[0];
                String value = splitTransformer[1];

                if (this.observationValue.equalsIgnoreCase(key)) {
                    this.observationValue = value;
                }
            }
        }
    }
    
}
