package edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import edu.asu.diging.citesphere.importer.core.model.impl.Affiliation;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.Contributor;

@Component
public class ContributorHandler extends TagHandler implements ArticleMetaTagHandler {

    @Autowired
    private ContributorHelper contributorHelper;
    
    @Override
    public String handledTag() {
        return "contrib-group";
    }

    @Override
    public void handle(Node node, ArticleMeta articleMeta) {
        if (articleMeta.getContributors() == null) {
            articleMeta.setContributors(new ArrayList<>());
        }
        
        List<Node> contribs = getChildNodes(node, "contrib");
        Map<String, List<Contributor>> contributorAffMap = new HashMap<>();
        for (Node contrib : contribs) {
            Contributor contributor = new Contributor();
            if (contrib instanceof Element) {
                contributor.setContributionType(((Element) contrib).getAttribute("contrib-type"));
                List<Node> names = getChildNodes(contrib, "string-name");
                if (!names.isEmpty()) {
                    // there should be only one name inside a contributor tag
                    Node stringName = names.get(0);
                    contributorHelper.setContributorData(stringName, contributor);
                    
                    articleMeta.getContributors().add(contributor);
                }
                
                List<Node> affPointers = getChildNodes(contrib, "xref");
                for (Node affPointer : affPointers) {
                    if (affPointer instanceof Element) {
                        String refType = ((Element) affPointer).getAttribute("ref-type");
                        if (refType != null && refType.equals("aff")) {
                            String affId = ((Element) affPointer).getAttribute("rid");
                            if (affId != null) {
                                if (contributorAffMap.get(affId) == null) {
                                    contributorAffMap.put(affId, new ArrayList<>());
                                }
                                contributorAffMap.get(affId).add(contributor);
                            }
                        }
                    }
                }
            }
        }
        
        List<Node> affs = getChildNodes(node, "aff");
        
        if (!affs.isEmpty() && contributorAffMap.isEmpty()) {
            handleUnlinkedAffiliations(articleMeta, affs);
        } else {
            handleLinkedAffiliations(contributorAffMap, affs);
        }
    }

    /**
     * Use the contributor affiliation map from before to link affiliations to their
     * contributors.
     * @param contributorAffMap
     * @param affs
     */
    private void handleLinkedAffiliations(Map<String, List<Contributor>> contributorAffMap, List<Node> affs) {
        for (Node aff : affs) {
            String id = ((Element)aff).getAttribute("id");
            String name = aff.getTextContent();
            
            Affiliation affiliation = new Affiliation();
            affiliation.setName(name);
            
            if (contributorAffMap.get(id) != null) {
                for (Contributor contributor : contributorAffMap.get(id)) {
                    if (contributor.getAffiliations() == null) {
                        contributor.setAffiliations(new ArrayList<>());
                    }
                    contributor.getAffiliations().add(affiliation);
                }
            }
        }
    }

    /**
     * Method to handle the case that there is no link between author and affiliations.
     * Instead we assume that if there is only affiliation listed, it belongs to all 
     * contributors; otherwise first contributor, first affiliation, etc.
     * @param articleMeta
     * @param affs
     */
    private void handleUnlinkedAffiliations(ArticleMeta articleMeta, List<Node> affs) {
        // if there is just one affiliation for all
        if (affs.size() == 1) {
            for (Contributor contributor : articleMeta.getContributors()) {
                contributor.setAffiliations(new ArrayList<>());
                Affiliation affiliation = new Affiliation();
                affiliation.setName(affs.get(0).getTextContent());
                contributor.getAffiliations().add(affiliation);
            }
        } else {
            Affiliation affiliation = null;
            for (int i=0; i<affs.size(); i++) {
                // if not use the last one
                if (articleMeta.getContributors().size() < i) {
                    affiliation = new Affiliation();
                    affiliation.setName(affs.get(i).getTextContent());
                }
                articleMeta.getContributors().get(i).setAffiliations(new ArrayList<>());
                articleMeta.getContributors().get(i).getAffiliations().add(affiliation);
            }
        }
    }

    
}
