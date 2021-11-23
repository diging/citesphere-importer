package edu.asu.diging.citesphere.importer.core.service.oauth;

public enum OAuthScope {
    READ("read"),
    WRITE("write");
    
    private String scope;
    
    private OAuthScope(String scope) {
        this.scope = scope;
    }
    
    public String getScope() {
        return scope;
    }
}
