package edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Node;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;

public class IssueTagHandlerTest extends TagHandlerTest {

    private IssueTagHandler handlerToTest = new IssueTagHandler();

    @Test
    public void test_handledTag() {
        Assert.assertEquals("issue", handlerToTest.handledTag());
    }
    
    @Test
    public void test_handle() {
        Node node = getNode("issue");
        ArticleMeta meta = new ArticleMeta();
        handlerToTest.handle(node, meta);
        
        Assert.assertEquals("1", meta.getIssue());
    }
}
