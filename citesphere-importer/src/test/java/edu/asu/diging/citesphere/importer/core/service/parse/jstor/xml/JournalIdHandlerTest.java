package edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import edu.asu.diging.citesphere.importer.core.model.impl.JournalId;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;

public class JournalIdHandlerTest extends TagHandlerTest {

    private JournalIdHandler handlerToTest = new JournalIdHandler();

    @Test
    public void test_handledTag() {
        Assert.assertEquals("journal-id", handlerToTest.handledTag());
    }
    
    @Test
    public void test_handle_oneId() {
        Node node = getNode("journal-id");
        ContainerMeta  meta = new ContainerMeta();
        handlerToTest.handle(node, meta);
        
        List<JournalId> ids = meta.getJournalIds();
        Assert.assertEquals(1, ids.size());
        Assert.assertEquals("publisher-id", ids.get(0).getIdType());
        Assert.assertEquals("publisher", ids.get(0).getId());
    }
    
    @Test
    public void test_handle_twoIds() {
        Node node = getNode("journal-id");
        ContainerMeta  meta = new ContainerMeta();
        
        handlerToTest.handle(node, meta);
        
        node.setTextContent("publisher2");
        ((Element)node).setAttribute("journal-id-type", "id2");;
        handlerToTest.handle(node, meta);
        
        List<JournalId> ids = meta.getJournalIds();
        Assert.assertEquals(2, ids.size());
        Assert.assertEquals("publisher-id", ids.get(0).getIdType());
        Assert.assertEquals("publisher", ids.get(0).getId());
        
        Assert.assertEquals("id2", ids.get(1).getIdType());
        Assert.assertEquals("publisher2", ids.get(1).getId());
    }
}
