package edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Node;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleId;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;

public class ArticleIdHandlerTest extends TagHandlerTest {

    private ArticleIdHandler handlerToTest = new ArticleIdHandler();
    
    @Test
    public void test_handledTag() {
        Assert.assertEquals("article-id", handlerToTest.handledTag());
    }
    
    @Test
    public void test_handle_correctIdInfo() {
        Node node = getNode("article-id");
        ArticleMeta meta = new ArticleMeta();
        handlerToTest.handle(node, meta);
        
        List<ArticleId> ids = meta.getArticleIds();
        Assert.assertEquals(1, ids.size());
        Assert.assertEquals("jstor-stable", ids.get(0).getPubIdType());
        Assert.assertEquals("jstorstable", ids.get(0).getId());
    }
    
    @Test
    public void test_handle_severalIds() {
        Node node = getNode("article-id");
        ArticleMeta meta = new ArticleMeta();
        handlerToTest.handle(node, meta);
        
        List<ArticleId> ids = meta.getArticleIds();
        Assert.assertEquals(1, ids.size());
        
        handlerToTest.handle(node, meta);
        Assert.assertEquals(2, ids.size());
    }
}
