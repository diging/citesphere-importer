package edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;

public class CustomMetaTagHandlerTest extends TagHandlerTest {

    private CustomMetaTagHandler handlerToTest = new CustomMetaTagHandler();

    @Test
    public void test_handledTag() {
        Assert.assertEquals("custom-meta-group", handlerToTest.handledTag());
    }
    
    @Test
    public void test_handle() {
        NodeList list = doc.getElementsByTagName("article-meta");
        Node articleNode = list.item(0);        
        List<Node> nodes = getChildNodes(articleNode, "custom-meta-group");
        ArticleMeta meta = new ArticleMeta();
        handlerToTest.handle(nodes.get(0), meta);
        
        Assert.assertEquals("en", meta.getLanguage());
    }
    
    
}
