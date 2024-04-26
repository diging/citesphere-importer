package edu.asu.diging.citesphere.importer.core.zotero.template.impl;

import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.importer.core.zotero.template.ItemJsonGenerator;

@Service
public class CrossRefGenerator extends ItemJsonGenerator {

    @Override
    public String responsibleFor() {
        return "CrossRef";
    }

}
