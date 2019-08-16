package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.impl.Issn;

@Component
public class SNTagHandler extends IdHandler {

    private final String ISSN = "issn";

    @Override
    public String handledTag() {
        return "SN";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, BibEntry entry,
            boolean isColumnFormat) {
        if (entry.getContainerMeta().getIssns() == null) {
            entry.getContainerMeta().setIssns(new ArrayList<>());
        }

        Issn issn = new Issn();
        issn.setIssn(value);
        issn.setPubType(ISSN);
        entry.getContainerMeta().getIssns().add(issn);
    }

}
