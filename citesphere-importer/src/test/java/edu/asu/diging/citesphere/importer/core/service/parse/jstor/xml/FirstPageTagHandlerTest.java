package edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Node;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;

public class FirstPageTagHandlerTest extends TagHandlerTest {

    private FirstPageTagHandler handlerToTest = new FirstPageTagHandler();

    @Test
    public void test_handledTag() {
        Assert.assertEquals("fpage", handlerToTest.handledTag());
    }
    
    @Test
    public void test_handle() {
        Node node = getNode("fpage");
        ArticleMeta meta = new ArticleMeta();
        handlerToTest.handle(node, meta);
        
        Assert.assertEquals(1 + "", meta.getFirstPage());
    }
}
