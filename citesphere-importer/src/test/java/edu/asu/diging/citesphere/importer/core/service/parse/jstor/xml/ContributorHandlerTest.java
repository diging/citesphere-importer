package edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Node;

import edu.asu.diging.citesphere.importer.core.model.impl.Affiliation;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.Contributor;

public class ContributorHandlerTest extends TagHandlerTest {

    private ContributorHandler handlerToTest = new ContributorHandler();

    @Test
    public void test_handledTag() {
        Assert.assertEquals("contrib-group", handlerToTest.handledTag());
    }

    @Test
    public void test_handle() {
        Node node = getNode("contrib-group");
        ArticleMeta meta = new ArticleMeta();
        handlerToTest.handle(node, meta);

        List<Contributor> contributors = meta.getContributors();
        Assert.assertEquals(2, contributors.size());
        // first author
        Assert.assertEquals("Max", contributors.get(0).getGivenName());
        Assert.assertEquals("Musterman", contributors.get(0).getSurname());
        Assert.assertEquals("author", contributors.get(0).getContributionType());

        List<Affiliation> affiliations = contributors.get(0).getAffiliations();
        Assert.assertEquals(1, affiliations.size());
        Assert.assertEquals("*affiliation", affiliations.get(0).getName());

        // second author
        Assert.assertEquals("Petra", contributors.get(1).getGivenName());
        Assert.assertEquals("Musterman", contributors.get(1).getSurname());
        Assert.assertEquals("author", contributors.get(1).getContributionType());

        List<Affiliation> affiliations2 = contributors.get(1).getAffiliations();
        Assert.assertEquals(1, affiliations2.size());
        Assert.assertEquals("*affiliation", affiliations2.get(0).getName());
    }
}
