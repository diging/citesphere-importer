package edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleCategory;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticleCategoryGroup;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;

@Component
public class ArticleCategoryHandler extends TagHandler implements ArticleMetaTagHandler {

    @Override
    public String handledTag() {
        return "article-categories";
    }

    @Override
    public void handle(Node node, ArticleMeta articleMeta) {
        if (articleMeta.getCategories() == null) {
            articleMeta.setCategories(new ArrayList<>());
        }
        List<Node> groups = getChildNodes(node, "subj-group");
        for (Node group : groups) {
            articleMeta.getCategories().add(processSubjectGroup(group));
        }
    }
    
    private ArticleCategoryGroup processSubjectGroup(Node node) {
        ArticleCategoryGroup group = new ArticleCategoryGroup();
        group.setType(((Element)node).getAttribute("subj-group-type"));
        group.setCategories(new ArrayList<>());
        group.setSubGroups(new ArrayList<>());
        
        List<Node> subjects = getChildNodes(node, "subject");
        for (Node subject : subjects) {
            ArticleCategory category = new ArticleCategory();
            category.setSubject(subject.getTextContent());
            group.getCategories().add(category);
        }
        
        List<Node> subGroups = getChildNodes(node, "subj-group");
        for (Node subGroup : subGroups) {
            group.getSubGroups().add(processSubjectGroup(subGroup));
        }
        
        return group;
    }

}
