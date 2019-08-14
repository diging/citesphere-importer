package edu.asu.diging.citesphere.importer.core.zotero.template.impl;

import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.importer.core.model.impl.Publication;
import edu.asu.diging.citesphere.importer.core.zotero.template.ItemJsonGenerator;

@Service
public class LetterGenerator extends ItemJsonGenerator {

    @Override
    public String responsibleFor() {
        return Publication.LETTER;
    }

}
