package edu.asu.diging.citesphere.importer.core.service.impl;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class JobInfo {

    private String zotero;
    private String zoteroId;
    private String groupId;
    private List<String> dois;
    
    public String getZotero() {
        return zotero;
    }
    public void setZotero(String zotero) {
        this.zotero = zotero;
    }
    public String getZoteroId() {
        return zoteroId;
    }
    public void setZoteroId(String zoteroId) {
        this.zoteroId = zoteroId;
    }
    public String getGroupId() {
        return groupId;
    }
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    public List<String> getDois() {
        return dois;
    }
    public void setDois(List<String> dois) {
        this.dois = dois;
    }
    
}
