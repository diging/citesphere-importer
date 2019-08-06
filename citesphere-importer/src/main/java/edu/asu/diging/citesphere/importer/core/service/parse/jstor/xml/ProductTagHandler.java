package edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.Contributor;
import edu.asu.diging.citesphere.importer.core.model.impl.ReviewInfo;

@Component
public class ProductTagHandler extends TagHandler implements ArticleMetaTagHandler {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public String handledTag() {
        return "product";
    }

    @Override
    public void handle(Node node, ArticleMeta articleMeta) {
        List<Node> contributors = getChildNodes(node, "string-name");
        ReviewInfo info = new ReviewInfo();
        info.setContributors(new ArrayList<>());
        articleMeta.setReviewInfo(info);
        
        createContributors(contributors, info);
        setReviewedTitle(node, info);
        setReviewedYear(node, info);
        setFullDescription(node, info);
        
    }

    private void setReviewedTitle(Node node, ReviewInfo info) {
        List<Node> source = getChildNodes(node, "source");
        if (!source.isEmpty()) {
            // let's assume there is just one source tag
            // or if there are several that the first one is the reviewed title
            String title = source.get(0).getTextContent();
            info.setTitle(title);
        }
    }
    
    private void setReviewedYear(Node node, ReviewInfo info) {
        List<Node> source = getChildNodes(node, "year");
        if (!source.isEmpty()) {
            // let's assume there is just one source tag
            // or if there are several that the first one is the reviewed title
            String year = source.get(0).getTextContent();
            info.setYear(year);
        }
    }

    private void createContributors(List<Node> contributors, ReviewInfo info) {
        for (Node contrib : contributors) {
            NodeList nameParts = contrib.getChildNodes();
            if (nameParts != null) {
                Contributor contributor = new Contributor();
                for (int i = 0; i<nameParts.getLength(); i++) {
                    Node namePart = nameParts.item(i);
                    if (namePart.getNodeName().equals("given-names")) {
                        contributor.setGivenName(namePart.getTextContent());
                        continue;
                    }
                    if (namePart.getNodeName().equals("surname")) {
                        contributor.setSurname(namePart.getTextContent());
                    }
                }
                info.getContributors().add(contributor);
            }
        }
    }
    
    private void setFullDescription(Node productNode, ReviewInfo info) {
        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = transFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            logger.error("Could not create transformer to read abstract.", e);
            return;
        }
        StringWriter buffer = new StringWriter();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        
        NodeList children = productNode.getChildNodes();
        if (children.getLength() > 0) {
            for (int i = 0; i<children.getLength(); i++) {
                Node child = children.item(i);
                try {
                    transformer.transform(new DOMSource(child),
                          new StreamResult(buffer));
                } catch (TransformerException e) {
                    logger.error("Could not extract abstract tag content.", e);
                    continue;
                }
            }
        }
        
        String content = buffer.toString();
        info.setFullDescription(content);
    }

}
