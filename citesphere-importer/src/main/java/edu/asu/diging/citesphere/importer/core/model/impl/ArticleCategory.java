package edu.asu.diging.citesphere.importer.core.model.impl;

public class ArticleCategory {

    private String subject;
    private boolean mightBeIncomplete = false;
    
    public boolean isMightBeIncomplete() {
        return mightBeIncomplete;
    }
    public void setMightBeIncomplete(boolean mightBeIncomplete) {
        this.mightBeIncomplete = mightBeIncomplete;
    }
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
}
