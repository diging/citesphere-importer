package edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Node;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleCategoryGroup;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;

public class ArticleCategoryHandlerTest extends TagHandlerTest {

    
    private ArticleCategoryHandler handlerToTest = new ArticleCategoryHandler();
    
    @Test
    public void test_handledTag() {
        Assert.assertEquals("article-categories", handlerToTest.handledTag());
    }

    @Test
    public void test_handle() {
        Node node = getNode("article-categories");
        ArticleMeta meta = new ArticleMeta();
        handlerToTest.handle(node, meta);
        
        List<ArticleCategoryGroup> categories = meta.getCategories();
        Assert.assertEquals(1, categories.size());
        
        ArticleCategoryGroup group = categories.get(0);
        Assert.assertEquals(1, group.getCategories().size());
        Assert.assertEquals("Heading", group.getCategories().get(0).getSubject());
        Assert.assertEquals(1, group.getSubGroups().size());
        
        ArticleCategoryGroup subgroup = group.getSubGroups().get(0);
        Assert.assertEquals(1, subgroup.getCategories().size());
        Assert.assertEquals("Subheading", subgroup.getCategories().get(0).getSubject());
        
    }
}
