package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.ArrayList;
import java.util.List;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleCategory;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticleCategoryGroup;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;

public abstract class CategoryHandler implements WoSMetaTagHandler {

    protected void addCategories(String value, String previousField, ArticleMeta articleMeta, String categoryGroup) {
        if (articleMeta.getCategories() == null) {
            articleMeta.setCategories(new ArrayList<>());
        }

        ArticleCategoryGroup group = null;
        for (ArticleCategoryGroup catGroup : articleMeta.getCategories()) {
            if (catGroup.getType().equals(categoryGroup)) {
                group = catGroup;
                break;
            }
        }

        if (group == null) {
            group = new ArticleCategoryGroup();
            group.setCategories(new ArrayList<>());
            group.setType(categoryGroup);
        }

        String[] categories = value.split(";");
        List<ArticleCategory> categoryList = group.getCategories();
        if (categories.length > 0) {
            for (int i = 0; i < categories.length; i++) {
                ArticleCategory categoryObj = null;

                // if the last line had categories and the last one was incomplete (didn't end
                // with ';')
                if (i == 0 && previousField.equals(handledTag()) && categoryList.size() > 0
                        && categoryList.get(categoryList.size() - 1).isMightBeIncomplete()) {
                    categoryObj = categoryList.get(categoryList.size() - 1);
                    categoryObj.setSubject(categoryObj.getSubject() + " " + categories[i]);
                }
                else {
                    categoryObj = new ArticleCategory();
                    categoryObj.setSubject(categories[i]);
                    // if the line does not end with ';' the category might be completed in the next line
                    if ((i == categories.length - 1) && !value.endsWith(";")) {
                        categoryObj.setMightBeIncomplete(true);
                    }
                    categoryList.add(categoryObj);
                }
            }
        }
    }
}