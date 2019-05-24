package edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml;

import java.util.ArrayList;

import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleCategory;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;

@Component
public class ArticleCategoryHandler implements ArticleMetaTagHandler {

    @Override
    public String handledTag() {
        return "article-categories";
    }

    @Override
    public void handle(Node node, ArticleMeta articleMeta) {
        if (articleMeta.getCategories() == null) {
            articleMeta.setCategories(new ArrayList<>());
        }
        if (node instanceof Element) {
            NodeList groups = ((Element) node).getChildNodes();
            if (groups != null && groups.getLength() > 0) {
                for (int i = 0 ; i<groups.getLength(); i++) {
                    Node groupNode = groups.item(i);
                    if (!groupNode.getNodeName().equals("subj-group")) {
                        continue;
                    }
                    if (groupNode instanceof Element) {
                        String groupType = ((Element) groupNode).getAttribute("subj-group-type");
                        // parse subjects
                        NodeList subjects = ((Element) groupNode).getChildNodes();
                        if (subjects != null && subjects.getLength() > 0) {
                            for (int j = 0; j<subjects.getLength(); j++) {
                                Node subject = subjects.item(j);
                                if (!subject.getNodeName().equals("subject")) {
                                    continue;
                                }
                                ArticleCategory category = new ArticleCategory();
                                category.setSubject(subject.getTextContent());
                                category.setSubjectGroupType(groupType);
                                articleMeta.getCategories().add(category);
                            }
                        }
                        // parse nested subject groups
                        handle(groupNode, articleMeta);
                    }
                }
            }
        }
    }

}
