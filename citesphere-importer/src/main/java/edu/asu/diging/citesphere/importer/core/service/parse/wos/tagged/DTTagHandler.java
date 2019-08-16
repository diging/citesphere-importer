package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.impl.Publication;

@Component
public class DTTagHandler implements WoSMetaTagHandler {

    /**
     * Map to map WoS document types ({@link WoSDocumentTypes} constants) to
     * internal bibliographical types ({@link Publication} constants).
     */
    private Map<String, String> publicationsType;

    @PostConstruct
    private void init() {
        publicationsType = new HashMap<String, String>();
        publicationsType.put(WoSDocumentTypes.ARTICLE, Publication.ARTICLE);
        publicationsType.put(WoSDocumentTypes.BOOK, Publication.BOOK);
        publicationsType.put(WoSDocumentTypes.BOOK_CHAPTER, Publication.BOOK_CHAPTER);
        publicationsType.put(WoSDocumentTypes.LETTER, Publication.ARTICLE);
        publicationsType.put(WoSDocumentTypes.REVIEW, Publication.ARTICLE);
        publicationsType.put(WoSDocumentTypes.BOOK_REVIEW, Publication.ARTICLE);
        publicationsType.put(WoSDocumentTypes.NEWS_ITEM, Publication.NEWS_ITEM);
        publicationsType.put(WoSDocumentTypes.PROCEEDINGS_PAPER, Publication.PROCEEDINGS_PAPER);
        publicationsType.put(WoSDocumentTypes.ARTICLE_PROCEEDINGS_PAPER, Publication.PROCEEDINGS_PAPER);
    }

    @Override
    public String handledTag() {
        return "DT";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, BibEntry entry,
            boolean isColumnFormat) {
        entry.getArticleMeta().setDocumentType(value);

        String pubType = publicationsType.get(value.trim());
        if (pubType == null) {
            String[] types = value.split(";");
            if (types.length > 0) {
                pubType = publicationsType.get(types[0].trim());
                if (pubType == null && types.length > 1) {
                    // let's try the next one if we can't find the first type
                    pubType = publicationsType.get(types[1].trim());
                }
            }
        }
        if (pubType == null) {
            entry.setArticleType(Publication.DOCUMENT);
        } else {
            entry.setArticleType(pubType);
        }

        if (value.equals(WoSDocumentTypes.LETTER)) {
            String title = "Letter to the Editor: ";
            if (entry.getArticleMeta().getArticleTitle() != null) {
                title += entry.getArticleMeta().getArticleTitle();
            }
            entry.getArticleMeta().setArticleTitle(title);
        }
    }

}
