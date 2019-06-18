package edu.asu.diging.citesphere.importer.core.model.impl;

import java.util.List;

public class ArticleCategoryGroup {

    private String type;
    private List<ArticleCategory> categories;
    private List<ArticleCategoryGroup> subGroups;
    
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public List<ArticleCategory> getCategories() {
        return categories;
    }
    public void setCategories(List<ArticleCategory> categories) {
        this.categories = categories;
    }
    public List<ArticleCategoryGroup> getSubGroups() {
        return subGroups;
    }
    public void setSubGroups(List<ArticleCategoryGroup> subGroups) {
        this.subGroups = subGroups;
    }
    
    
}
