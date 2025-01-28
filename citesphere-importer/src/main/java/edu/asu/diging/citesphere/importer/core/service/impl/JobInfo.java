package edu.asu.diging.citesphere.importer.core.service.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class JobInfo {

    private String zotero;
    private String zoteroId;
    private String groupId;
    private String collectionId;
    private String username;
    
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
    public String getCollectionId() {
        return collectionId;
    }
    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    
}
