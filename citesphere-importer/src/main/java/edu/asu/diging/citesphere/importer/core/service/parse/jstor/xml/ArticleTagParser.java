package edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;

@Component
public class ArticleTagParser implements IArticleTagParser {

    @Autowired
    private ApplicationContext ctx;

    private Map<String, JournalMetaTagHandler> journalMetaHandlers;
    private Map<String, ArticleMetaTagHandler> articleMetaHanders;

    @PostConstruct
    public void init() {
        journalMetaHandlers = new HashMap<>();
        ctx.getBeansOfType(JournalMetaTagHandler.class).values()
                .forEach(h -> journalMetaHandlers.put(h.handledTag(), h));

        articleMetaHanders = new HashMap<>();
        ctx.getBeansOfType(ArticleMetaTagHandler.class).values()
                .forEach(h -> articleMetaHanders.put(h.handledTag(), h));
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml.IArticleTagParser#parseJournalMetaTag(org.w3c.dom.Node, edu.asu.diging.citesphere.importer.core.model.impl.JournalMeta)
     */
    @Override
    public void parseJournalMetaTag(Node node, ContainerMeta meta) {
        JournalMetaTagHandler handler = journalMetaHandlers.get(node.getNodeName());
        if (handler != null) {
            handler.handle(node, meta);
        }
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml.IArticleTagParser#parseArticleMetaTag(org.w3c.dom.Node, edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta)
     */
    @Override
    public void parseArticleMetaTag(Node node, ArticleMeta meta) {
        ArticleMetaTagHandler handler = articleMetaHanders.get(node.getNodeName());
        if (handler != null) {
            handler.handle(node, meta);
        }
    }
}
