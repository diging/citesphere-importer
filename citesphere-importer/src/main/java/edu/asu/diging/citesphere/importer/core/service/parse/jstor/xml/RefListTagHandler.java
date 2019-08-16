package edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.smartcardio.ATR;
import javax.xml.XMLConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.sun.org.apache.xerces.internal.dom.DeferredAttrImpl;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.Contributor;
import edu.asu.diging.citesphere.importer.core.model.impl.Reference;

@Component
public class RefListTagHandler extends TagHandler implements ArticleMetaTagHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ContributorHelper contributorHelper;

    private Map<String, BiConsumer<Node, Reference>> attributeMethods;
    private Map<String, BiConsumer<Node, Reference>> nodeNameMethods;

    @PostConstruct
    public void init() {
        attributeMethods = new HashMap<String, BiConsumer<Node, Reference>>();
        attributeMethods.put("id", this::setCitationId);
        attributeMethods.put("publication-type", this::setPublicationType);

        nodeNameMethods = new HashMap<>();
        nodeNameMethods.put("person-group", this::processPersonGroup);
        nodeNameMethods.put("fpage", this::processFirstPage);
        nodeNameMethods.put("lpage", this::processLastPage);
        nodeNameMethods.put("volume", this::processVolume);
        nodeNameMethods.put("source", this::processSource);
        nodeNameMethods.put("year", this::processYear);
    }

    @Override
    public String handledTag() {
        return "ref-list";
    }

    @Override
    public void handle(Node node, ArticleMeta articleMeta) {
        List<Node> refNodes = getChildNodes(node, "ref");
        if (articleMeta.getReferences() == null) {
            articleMeta.setReferences(new ArrayList<>());
        }

        Transformer tf = null;
        try {
            tf = TransformerFactory.newInstance().newTransformer();
            tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        } catch (TransformerConfigurationException | TransformerFactoryConfigurationError e1) {
            logger.error("Could not create transformer.", e1);
        }

        for (Node refNode : refNodes) {
            Element refElement = (Element) refNode;
            Reference ref = new Reference();
            articleMeta.getReferences().add(ref);
            if (refElement.hasAttribute("id")) {
                ref.setReferenceId(((Element) refNode).getAttribute("id"));
            }

            NodeList refChildren = refElement.getChildNodes();
            for (int i = 0; i < refChildren.getLength(); i++) {
                Node refChild = refChildren.item(i);
                if (refChild.getNodeName().equals("label")) {
                    ref.setReferenceLabel(refChild.getNodeValue());
                } else if (refChild.getNodeName().equals("mixed-citation")) {

                    for (String attr : attributeMethods.keySet()) {
                        if (((Element) refChild).hasAttribute(attr)) {
                            attributeMethods.get(attr).accept(refChild, ref);
                        }
                    }
                    // set raw reference (including xml markup)
                    printReferenceNode(tf, ref, refChild);

                    NodeList citationChildren = refChild.getChildNodes();
                    for (int childIdx = 0; childIdx < citationChildren.getLength(); childIdx++) {
                        Node citationChild = citationChildren.item(childIdx);
                        BiConsumer<Node, Reference> tagProcessingMethod = nodeNameMethods
                                .get(citationChild.getNodeName());
                        if (tagProcessingMethod != null) {
                            tagProcessingMethod.accept(citationChild, ref);
                        }
                    }

                    if (ref.getReferenceString() != null && !ref.getReferenceString().isEmpty()) {
                        // if there is no year, let's try to find it
                        // we assume that if there is a year with parentheses, e.g. (2000), it's
                        // the publication year
                        if (ref.getYear() == null || ref.getYear().trim().isEmpty()) {
                            String refString = ref.getReferenceString();
                            Pattern pattern = Pattern.compile("\\(([0-9]{4})[a-z]?\\)");
                            Matcher match = pattern.matcher(refString);
                            if (match.find()) {
                                ref.setYear(match.group(1));
                            }
                        }

                        // let's try to find the title
                        if (ref.getTitle() == null || ref.getTitle().trim().isEmpty()) {
                            String refString = ref.getReferenceString();
                            Pattern pattern = Pattern.compile("\".+?\"");
                            Matcher match = pattern.matcher(refString);
                            if (match.find()) {
                                ref.setTitle(match.group());
                            } else {
                                pattern = Pattern.compile("“.+?”");
                                match = pattern.matcher(refString);
                                if (match.find()) {
                                    ref.setTitle(match.group());
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    private void setCitationId(Node refNode, Reference ref) {
        ref.setCitationId(((Element) refNode).getAttribute("id"));
    }

    private void setPublicationType(Node refNode, Reference ref) {
        ref.setPublicationType(((Element) refNode).getAttribute("publication-type"));
    }

    private void processPersonGroup(Node citationNode, Reference ref) {
        ref.setContributors(new ArrayList<>());
        addContributors(citationNode, ref.getContributors());
    }

    private void processFirstPage(Node citationNode, Reference ref) {
        ref.setFirstPage(citationNode.getTextContent());
    }

    private void processLastPage(Node citationNode, Reference ref) {
        ref.setEndPage(citationNode.getTextContent());
    }

    private void processVolume(Node citationNode, Reference ref) {
        ref.setVolume(citationNode.getTextContent());
    }

    private void processSource(Node citationNode, Reference ref) {
        ref.setSource(citationNode.getTextContent());
    }

    private void processYear(Node citationNode, Reference ref) {
        ref.setYear(citationNode.getTextContent());
    }

    private void printReferenceNode(Transformer tf, Reference ref, Node child) {
        Node citationNodeClone = child.cloneNode(true);

        // only keep typesetting tags that
        NodeList citationContent = citationNodeClone.getChildNodes();
        for (int childIdx = 0; childIdx < citationContent.getLength(); childIdx++) {
            // we assume the only markup in the refernce text is 'italic' or 'bold'
            Node citationChildNode = citationContent.item(childIdx);
            if (!(citationChildNode instanceof Text) && !citationChildNode.getNodeName().equals("italic") && !citationChildNode.getNodeName().equals("bold")) {
                citationNodeClone.removeChild(citationChildNode);
            }
        }

        NamedNodeMap attributes = citationNodeClone.getAttributes();
        Map<String, String> namespaces = new HashMap<>();
        for (int attrIdx = 0; attrIdx < attributes.getLength(); attrIdx++) {
            Attr attr = (Attr) attributes.item(attrIdx);
            if (attr.getNamespaceURI() != null) {
                namespaces.put("xmlns:" + attr.getPrefix(), attr.getNamespaceURI());
            }
        }

        for (String key : namespaces.keySet()) {
            ((Element) citationNodeClone).setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, key, namespaces.get(key));
        }

        if (tf != null) {
            DOMSource source = new DOMSource();
            source.setNode(citationNodeClone);
            Writer nodeString = new StringWriter();
            try {
                tf.transform(source, new StreamResult(nodeString));
                ref.setReferenceStringRaw(nodeString.toString());
            } catch (TransformerException e) {
                logger.error("Could not write raw reference string.", e);
            }
        }

        String refString = citationNodeClone.getTextContent().trim();
        ref.setReferenceString(refString);

    }

    private void addContributors(Node personGroupNode, List<Contributor> contributors) {
        List<Node> persons = getChildNodes(personGroupNode, "string-name");
        for (Node person : persons) {
            Contributor contributor = new Contributor();
            contributors.add(contributor);
            contributorHelper.setContributorData(person, contributor);
        }
    }

}
