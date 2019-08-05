package edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Node;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;

public class AuthorNotesTagHandlerTest extends TagHandlerTest {

    private AuthorNotesTagHandler handlerToTest = new AuthorNotesTagHandler();
    
    @Test
    public void test_handledTag() {
        Assert.assertEquals("author-notes", handlerToTest.handledTag());
    }
    
    @Test
    public void test_handle() {
        Node node = getNode("author-notes");
        ArticleMeta meta = new ArticleMeta();
        handlerToTest.handle(node, meta);
        
        Assert.assertEquals("author notes", meta.getAuthorNotesCorrespondence());
    }
}
