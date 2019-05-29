package edu.asu.diging.citesphere.importer.core.zotero.template;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;

public abstract class ItemJsonGenerator {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    protected Map<String, Method> methods;
    protected ObjectMapper mapper;
    
    @PostConstruct
    public void init() {
        mapper = new ObjectMapper();
        methods = new HashMap<>();
        
        Method[] declaredMethods = getClass().getMethods();
        for (Method method : declaredMethods) {
            methods.put(method.getName(), method);
        }
    }

    public abstract Class<?> responsibleFor();

    public String generate(JsonNode node, BibEntry bibEntry) {
        Map<String, Object> objectMap = new HashMap<>();
        Iterator<Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            Object result = null;
            Entry<String, JsonNode> entry = fields.next();
            Method processMethod = methods.get("process" + StringUtils.capitalize(entry.getKey()));
            if (processMethod == null) {
                logger.warn("No such method: " + "process" + StringUtils.capitalize(entry.getKey()));
                continue;
            }
            try {
                result = processMethod.invoke(this, entry.getValue(), bibEntry);
            } catch (SecurityException e) {
                logger.warn("There is no method or method is not callable to process field: " + entry.getKey(), e);
            } catch (IllegalAccessException e) {
                logger.warn("Could not call processing method.", e);
            } catch (IllegalArgumentException e) {
                logger.warn("Method does not take provided argument.", e);
            } catch (InvocationTargetException e) {
                logger.warn("An error occurred when calling processing method.", e);
            }
            
            if (result != null) {
                objectMap.put(entry.getKey(), result);
            }
        }
        
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(objectMap);
        } catch (JsonProcessingException e) {
            logger.error("Could not write JSON.");
            return null;
        }
    }
    
    public String processItemType(JsonNode node, BibEntry bibEntry) {
        return node.asText();
    }
    
    public ArrayNode processTags(JsonNode node, BibEntry bibEntry) {
        return mapper.createArrayNode();
    }
    
    public JsonNode processRelations(JsonNode node, BibEntry bibEntry) {
        return mapper.createObjectNode();
    }
    
    public JsonNode processCollections(JsonNode node, BibEntry bibEntry) {
        return mapper.createArrayNode();
    }
    
    protected ObjectMapper getObjectMapper() {
        return mapper;
    }
}
