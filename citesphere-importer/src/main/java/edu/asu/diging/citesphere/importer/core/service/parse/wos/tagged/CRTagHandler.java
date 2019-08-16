package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.impl.Reference;

@Component
public class CRTagHandler extends MetaTagHandler {

    @Override
    public String handledTag() {
        return "CR";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, BibEntry entry,
            boolean isColumnFormat) {

        if (entry.getArticleMeta().getReferences() == null) {
            entry.getArticleMeta().setReferences(new ArrayList<>());
        }

        String[] references = splitValues(value, isColumnFormat);

        for (String reference : references) {
            String[] parts = reference.split(",");
            // let's assume there are at least name, year, and doi or title
            Reference ref = new Reference();
            if (parts.length > 1) {
                String potentialAuthor = parts[0].trim();
                // apprently sometimes they only have a year
                if (!potentialAuthor.matches("[0-9]{4}")) {
                    ref.setAuthorString(potentialAuthor);
                    String potentialYear = parts[1].trim();
                    if (potentialYear.matches("[0-9]{4}")) {
                        ref.setYear(potentialYear);
                    }
                } else {
                    ref.setYear(potentialAuthor);
                }
            }
            if (parts.length > 3) {
                // assume last one is DOI
                setDOI(ref, parts[parts.length - 1]);
            }

            ref.setReferenceString(reference.trim());
            entry.getArticleMeta().getReferences().add(ref);
        }
    }

    private void setDOI(Reference ref, String part) {
        if (part.trim().startsWith("DOI")) {
            // cut off DOI
            ref.setIdentifier(part.trim().substring(4));
            ref.setIdentifierType("doi");
        }
    }
}
