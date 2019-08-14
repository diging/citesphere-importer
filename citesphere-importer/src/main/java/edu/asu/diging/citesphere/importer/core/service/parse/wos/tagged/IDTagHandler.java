package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.impl.Keyword;
import edu.asu.diging.citesphere.importer.core.model.impl.KeywordType;

@Component
public class IDTagHandler implements WoSMetaTagHandler {

    @Override
    public String handledTag() {
        return "ID";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, BibEntry entry) {
        if (entry.getArticleMeta().getKeywords() == null) {
            entry.getArticleMeta().setKeywords(new ArrayList<Keyword>());
        }
        
        String[] keywords = value.split(";");
        
        if (keywords.length > 0) {
            for (String keyword : keywords) {
                Keyword authorKeyword = new Keyword();
                authorKeyword.setKeyword(keyword.trim());
                authorKeyword.setCreator(KeywordType.KEYWORDS_PLUS);
                entry.getArticleMeta().getKeywords().add(authorKeyword);
            }
        }
    }

}
