package edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TagHandlerTest {

    protected Document doc;
    
    @Before
    public void init() throws SAXException, IOException, ParserConfigurationException {
        InputStream stream = getClass().getResourceAsStream("jstor-xml-test.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        doc = dBuilder.parse(stream);
    }
    
    protected Node getNode(String name) {
        NodeList list = doc.getElementsByTagName(name);
        return list.item(0);
    }
}
