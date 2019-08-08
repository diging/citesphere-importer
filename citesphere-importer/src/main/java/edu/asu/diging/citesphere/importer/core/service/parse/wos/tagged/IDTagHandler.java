package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.Keyword;
import edu.asu.diging.citesphere.importer.core.model.impl.KeywordType;

@Component
public class IDTagHandler implements WoSMetaTagHandler {

    @Override
    public String handledTag() {
        return "ID";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, ContainerMeta containerMeta,
            ArticleMeta articleMeta) {
        if (articleMeta.getKeywords() == null) {
            articleMeta.setKeywords(new ArrayList<Keyword>());
        }
        
        String[] keywords = value.split(";");
        
        if (keywords.length > 0) {
            for (String keyword : keywords) {
                Keyword authorKeyword = new Keyword();
                authorKeyword.setKeyword(keyword);
                authorKeyword.setCreator(KeywordType.KEYWORDS_PLUS);
                articleMeta.getKeywords().add(authorKeyword);
            }
        }
    }

}
