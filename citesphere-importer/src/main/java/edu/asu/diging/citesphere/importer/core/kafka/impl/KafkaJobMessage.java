package edu.asu.diging.citesphere.importer.core.kafka.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class KafkaJobMessage {

    private String jobToken;
    
    @JsonCreator
    public KafkaJobMessage() {}
    
    public KafkaJobMessage(String jobToken) {
        this.jobToken = jobToken;
    }

    public String getId() {
        return jobToken;
    }

    public void setId(String id) {
        this.jobToken = id;
    }
}
