package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;

@Component
public class ArticleWoSTagParser implements IArticleWoSTagParser {

    @Autowired
    private ApplicationContext ctx;

    private Map<String, WoSMetaTagHandler> metaHanders;

    @PostConstruct
    public void init() {
        metaHanders = new HashMap<>();
        ctx.getBeansOfType(WoSMetaTagHandler.class).values()
                .forEach(h -> metaHanders.put(h.handledTag(), h));
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml.IArticleTagParser#parseArticleMetaTag(org.w3c.dom.Node, edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta)
     */
    @Override
    public void parseMetaTag(String field, String value, String previousField, int fieldIdx, ContainerMeta containerMeta, ArticleMeta articleMeta) {
        WoSMetaTagHandler handler = metaHanders.get(field);
        if (handler != null) {
            handler.handle(field, value, previousField, fieldIdx, containerMeta, articleMeta);
        }
    }
}
