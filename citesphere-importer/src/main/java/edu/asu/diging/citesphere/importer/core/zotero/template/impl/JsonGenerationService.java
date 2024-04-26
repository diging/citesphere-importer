package edu.asu.diging.citesphere.importer.core.zotero.template.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.impl.CrossRefPublication;
import edu.asu.diging.citesphere.importer.core.zotero.template.IJsonGenerationService;
import edu.asu.diging.citesphere.importer.core.zotero.template.ItemJsonGenerator;

@Service
public class JsonGenerationService implements IJsonGenerationService {

    private Map<String, ItemJsonGenerator> generators;
    
    @Autowired
    private ApplicationContext ctx;
    
    @PostConstruct
    public void init() {
        generators = new HashMap<>();
        Map<String, ItemJsonGenerator> beanMap = ctx.getBeansOfType(ItemJsonGenerator.class);
        for (ItemJsonGenerator generator : beanMap.values()) {
            generators.put(generator.responsibleFor(), generator);
        }
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.importer.core.zotero.template.impl.IGenerationService#generateJson(com.fasterxml.jackson.databind.JsonNode, edu.asu.diging.citesphere.importer.core.model.BibEntry)
     */
    @Override
    public ObjectNode generateJson(JsonNode template, BibEntry entry) {
        ItemJsonGenerator generator = generators.get(entry.getArticleType());
        if (generator != null) {
            return generator.generate(template, entry);
        } else if (entry instanceof CrossRefPublication){
            generator = generators.get("CrossRef");
            return generator.generate(template, entry);
        }

        return null;
    }
}
