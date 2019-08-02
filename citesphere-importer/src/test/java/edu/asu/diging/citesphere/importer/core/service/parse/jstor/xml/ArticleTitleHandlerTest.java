package edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Node;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;

public class ArticleTitleHandlerTest extends TagHandlerTest {

    private ArticleTitleHandler handlerToTest = new ArticleTitleHandler();
    
    @Test
    public void test_handledTag() {
        Assert.assertEquals("title-group", handlerToTest.handledTag());
    }
    
    @Test
    public void test_handle() {
        Node node = getNode("title-group");
        ArticleMeta meta = new ArticleMeta();
        handlerToTest.handle(node, meta);
        
        Assert.assertEquals("Test title", meta.getArticleTitle());
    }
}
