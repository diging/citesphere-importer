package edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class TagHandler {

    protected List<Node> getChildNodes(Node node, String nodeName) {
        List<Node> nodes = new ArrayList<>();
        NodeList nodeList = node.getChildNodes();
        if (nodeList != null) {
            for (int i=0; i<nodeList.getLength(); i++) {
                Node childNode = nodeList.item(i);
                if (childNode.getNodeName().equals(nodeName)) {
                    nodes.add(childNode);
                }
            }
        }
        return nodes;
    }

}