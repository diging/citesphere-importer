package edu.asu.diging.citesphere.importer.core.model.impl;

public class ContributorId {

    private String id;
    private String idSystem;
    
    public ContributorId() {}
    
    public ContributorId(String id, String idSystem) {
        this.id = id;
        this.idSystem = idSystem;
    }
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getIdSystem() {
        return idSystem;
    }
    public void setIdSystem(String idSystem) {
        this.idSystem = idSystem;
    }
}
