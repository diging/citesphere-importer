package edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Node;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;

public class IssueIdTagHandlerTest extends TagHandlerTest {

    private IssueIdTagHandler handlerToTest = new IssueIdTagHandler();

    @Test
    public void test_handledTag() {
        Assert.assertEquals("issue-id", handlerToTest.handledTag());
    }
    
    @Test
    public void test_handle() {
        Node node = getNode("issue-id");
        ArticleMeta meta = new ArticleMeta();
        handlerToTest.handle(node, meta);
        
        Assert.assertEquals("issueid", meta.getIssueId());
    }
}
