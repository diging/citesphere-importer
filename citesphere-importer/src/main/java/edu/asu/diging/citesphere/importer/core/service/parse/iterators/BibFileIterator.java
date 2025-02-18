package edu.asu.diging.citesphere.importer.core.service.parse.iterators;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.zotero.api.Data;
import org.springframework.social.zotero.api.Item;

import edu.asu.diging.citesphere.factory.impl.ParseExtra;
import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.impl.Affiliation;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticleId;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticlePublicationDate;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContributionType;
import edu.asu.diging.citesphere.importer.core.model.impl.Contributor;
import edu.asu.diging.citesphere.importer.core.model.impl.Issn;
import edu.asu.diging.citesphere.importer.core.model.impl.Publication;
import edu.asu.diging.citesphere.importer.core.model.impl.Reference;
import edu.asu.diging.citesphere.importer.core.repository.UserRepository;
import edu.asu.diging.citesphere.importer.core.service.IGilesConnector;
import edu.asu.diging.citesphere.importer.core.service.giles.impl.GilesConnector;
import edu.asu.diging.citesphere.importer.core.service.impl.JobInfo;
import edu.asu.diging.citesphere.importer.core.service.parse.BibEntryIterator;
import edu.asu.diging.citesphere.model.bib.IAffiliation;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.ICreator;
import edu.asu.diging.citesphere.model.bib.IGilesUpload;
import edu.asu.diging.citesphere.model.bib.IPerson;
import edu.asu.diging.citesphere.model.bib.IReference;
import edu.asu.diging.citesphere.model.bib.impl.Citation;
import edu.asu.diging.citesphere.model.bib.impl.Person;
import edu.asu.diging.citesphere.user.IUser;
import edu.asu.diging.citesphere.user.impl.User;

public class BibFileIterator implements BibEntryIterator {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private ParseExtra parseExtra;

    private String filePath;
    private String groupId;
    private String collectionId;
    private JobInfo info;
    private Iterator<String> lineIterator;
    private Map<String, String> typeMap;
    private IGilesConnector gilesConnector;
    private UserRepository userRepository;

    public BibFileIterator(String filePath, JobInfo info, UserRepository userRepository) {
        this.filePath = filePath;
        this.groupId = info.getGroupId();
        this.collectionId = info.getCollectionId();
        this.info = info;
        this.gilesConnector = new GilesConnector();
        this.userRepository = userRepository;
        parseExtra = new ParseExtra();
        parseExtra.init();
        init();
    }

    private void init() {
        try {
            lineIterator = FileUtils.lineIterator(new File(filePath), "UTF-8");
        } catch (IOException e) {
            logger.error("Could not create line iterator.", e);
        }
        typeMap = new HashMap<String, String>();
        typeMap.put("article", Publication.ARTICLE);
        typeMap.put("book", Publication.BOOK);
        typeMap.put("book-chapter", Publication.BOOK_CHAPTER); 
        typeMap.put("monograph", Publication.BOOK);
        typeMap.put("proceedings-article", Publication.PROCEEDINGS_PAPER);
        typeMap.put("book-section", Publication.BOOK_CHAPTER);
        typeMap.put("research-article", Publication.ARTICLE);
        typeMap.put("book-review", Publication.REVIEW);
        typeMap.put("patent", Publication.PROCEEDINGS_PAPER);
    }

    @Override
    public BibEntry next() {
        BibEntry entry = new Publication();
        Map<String, String> fields = new HashMap<>();
        while (lineIterator.hasNext()) {
            String line = lineIterator.next().trim();
            if(!line.isBlank() && line.charAt(0)=='@') {
                entry.setArticleType(typeMap.get(line.substring(1, line.indexOf('{'))));
            } else if (line.equals("}")) {
                entry.setJournalMeta(parseJournalMeta(fields));
                entry.setArticleMeta(parseArticleMeta(fields));
                fields.clear();
                break;
            } else if (line.contains("=")) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    if (value.endsWith(",")) {
                        value = value.substring(0, value.length()-1).replaceAll("^\\{|\\}$", "");
                    }
                    fields.put(key, value);
                }
            }
        }

        return entry;
    }

    private ContainerMeta parseJournalMeta(Map<String, String> fields) {
        ContainerMeta meta = new ContainerMeta();

        if(fields.containsKey("journal")) {
            List<String> journalAbbrev = new ArrayList<>();
            journalAbbrev.add(fields.get("journal"));
            meta.setJournalAbbreviations(journalAbbrev);
        }
        meta.setPublisherName(fields.get("publisher"));
        meta.setPublisherLocation(fields.get("place"));
        List<Issn> issnList = new ArrayList<Issn>();
        if(fields.get("issn") != null) {
            for(String issnString : fields.get("issn").split("and")) {
                Issn issn = new Issn();
                issn.setPubType("issn");
                issn.setIssn(issnString.trim());
                issnList.add(issn);
            }
        }
        meta.setIssns(issnList);
        meta.setSeriesTitle(fields.get("series"));
        return meta;
    }

    private ArticleMeta parseArticleMeta(Map<String, String> fields) {
        ArticleMeta meta = new ArticleMeta();
        ICitation citation = new Citation();
        Item item = new Item();
        Data data = new Data();
        if(fields.containsKey("annote")) {
            data.setNote(fields.get("annote").replaceAll("\\\\", "").replaceAll("\\{textbackslash\\}n", ""));
        }
        if(fields.containsKey("note")) {
            data.setExtra(fields.get("note").replace("\\\\", "").replaceAll("\\{textbackslash\\}n", ""));
        }
        item.setData(data);

        Set<IPerson> authors = new HashSet<>();
        if (fields.containsKey("author")) {
            String[] authorStringList = fields.get("author").split("and");
            for(String authorString: authorStringList) {
                IPerson author = new Person();
                String[] authorParts = authorString.split(",");
                author.setLastName(authorParts[0].trim());
                author.setFirstName(authorParts[1].trim());
                authors.add(author);
            }
        }
        citation.setAuthors(authors);

        Set<IPerson> editors = new HashSet<>();
        if (fields.containsKey("editor")) {
            String[] editorStringList = fields.get("editor").split("and");
            for(String editorString: editorStringList) {
                IPerson editor = new Person();
                String[] editorParts = editorString.split(",");
                editor.setLastName(editorParts[0].trim());
                editor.setFirstName(editorParts[1].trim());
                editors.add(editor);
            }
        }
        citation.setEditors(editors);


        Set<ICreator> creators = new HashSet<>();
        citation.setOtherCreators(creators);

        parseExtra.parseMetaDataNote(citation, item);
        parseExtra.parseExtra(data, citation);

        List<String> collectionIds = new ArrayList<>();
        if (collectionId != null && !collectionId.trim().isEmpty()) {
            collectionIds.add(collectionId);
        }
        meta.setCollections(collectionIds);
        meta.setArticleTitle(fields.get("title"));
        meta.setArticleShortTitle(fields.get("shorttitle"));

        List<Contributor> contributors = new ArrayList<>();
        // List of authors
        if(citation.getAuthors() != null) {
            contributors.addAll(mapPersonToContributor(citation.getAuthors(), ContributionType.AUTHOR));
        }
        // List of editors
        if(citation.getEditors() != null) {
            contributors.addAll(mapPersonToContributor(citation.getEditors(), ContributionType.EDITOR));
        }
        // List of other creators
        if(citation.getOtherCreators() != null) {
            contributors.addAll(mapCreatorToContributor(citation.getOtherCreators()));
            
        }
        meta.setContributors(contributors);
        ArticlePublicationDate publicationDate = new ArticlePublicationDate();
        publicationDate.setPublicationYear(fields.get("year"));
        meta.setPublicationDate(publicationDate);
        meta.setVolume(fields.get("volume"));
        meta.setIssue(fields.get("number"));
        if(fields.containsKey("pages")) {
            meta.setFirstPage(fields.get("pages").split("--")[0].trim());
            meta.setLastPage(fields.get("pages").split("--")[1].trim());
        }
        meta.setSelfUri(fields.get("url"));
        meta.setDoi(fields.get("doi"));        
        ArticleId doiId = new ArticleId();
        doiId.setPubIdType("doi");
        doiId.setId(fields.get("doi"));
        ArticleId isbnId = new ArticleId();
        isbnId.setPubIdType("isbn");
        isbnId.setId(fields.get("isbn"));
        List<ArticleId> articleIds = new ArrayList<>();
        articleIds.add(doiId);
        articleIds.add(isbnId);
        meta.setArticleIds(articleIds);
        meta.setArticleAbstract(fields.get("abstract"));
        meta.setLanguage(fields.get("language"));

        if(citation.getReferences() != null) {
            meta.setReferences(mapReferences(citation.getReferences()));
            meta.setReferenceCount(meta.getReferences().size()+"");
        }
        
        if(fields.containsKey("file")) {
            String[] fileParts = fields.get("file").split(":");
//            meta.setDocumentType(fileParts[2]);
//            meta.setFilePath(fileParts[1]);
            IGilesUpload upload = createGilesUpload(fileParts[1], info);
            List<IGilesUpload> uploads = new ArrayList<>();
            uploads.add(upload);
            meta.setGilesUpload(uploads);
//            =================
        }
        
        
        return meta;
    }

    private List<Contributor> mapPersonToContributor(Set<IPerson> citationContributors, String contributionType) {
        List<Contributor> contributors = new ArrayList<Contributor>();
        for(IPerson person: citationContributors) {
            Contributor contributor = mapSinglePerson(person, contributionType);
            contributors.add(contributor);
        }
        return contributors;
    }
    
    private Contributor mapSinglePerson(IPerson person, String contributionType) {
        Contributor contributor = new Contributor();
        contributor.setContributionType(contributionType);
        contributor.setGivenName(person.getFirstName());
        contributor.setSurname(person.getLastName());
        contributor.setFullName(person.getName());
        contributor.setUri(person.getUri());

        List<Affiliation> affiliations = new ArrayList<>();
        if(person.getAffiliations()!= null) {
            for(IAffiliation institute: person.getAffiliations()) {                
                Affiliation affiliation = new Affiliation();
                affiliation.setName(institute.getName());
                affiliation.setUri(institute.getUri());
                affiliation.setLocalAuthorityId(institute.getLocalAuthorityId());
                affiliations.add(affiliation);
            }
        }
        contributor.setAffiliations(affiliations);
        return contributor;
    }
    
    private List<Contributor> mapCreatorToContributor(Set<ICreator> creators) {
        List<Contributor> contributors = new ArrayList<Contributor>();
        for(ICreator creator: creators) {
            contributors.add(mapSinglePerson(creator.getPerson(), creator.getRole()));
        }
        return contributors;
    }

    private List<Reference> mapReferences(Set<IReference> citationReferences) {
        List<Reference> references = new ArrayList<Reference>();
        for(IReference citationRef: citationReferences) {
            references.add(mapSingleReference(citationRef));
        }

        return references;
    }

    private Reference mapSingleReference(IReference citationRef) {
        Reference ref = new Reference();
        ref.setAuthorString(citationRef.getAuthorString());
        ref.setTitle(citationRef.getTitle());
        ref.setYear(citationRef.getYear());
        ref.setIdentifier(citationRef.getIdentifier());
        ref.setIdentifierType(citationRef.getIdentifierType());
        ref.setFirstPage(citationRef.getFirstPage());
        ref.setEndPage(citationRef.getEndPage());
        ref.setVolume(citationRef.getVolume());
        ref.setSource(citationRef.getSource());
        ref.setReferenceId(citationRef.getReferenceId());
        ref.setReferenceLabel(citationRef.getReferenceLabel());
        ref.setPublicationType(citationRef.getPublicationType());
        ref.setCitationId(citationRef.getCitationId());
        ref.setReferenceString(citationRef.getReferenceString());
        ref.setReferenceStringRaw(citationRef.getReferenceStringRaw());

        return ref;
    }

    private IGilesUpload createGilesUpload(String gilesFilePath, JobInfo info) {
        System.out.println(gilesFilePath + "=================================");

        IUser user = null;
        Optional<User> foundUser = userRepository.findById(info.getUsername());
        if (foundUser.isPresent()) {
            user = (IUser) foundUser.get();
        }

        File file = new File(gilesFilePath);
        byte[] fileBytes = null;
        try {
            fileBytes = Files.readAllBytes(Path.of(gilesFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        IGilesUpload upload = gilesConnector.uploadFile(user, info.getGiles(), file.getName(), fileBytes);
        return upload;
    }
    
    @Override
    public boolean hasNext() {
        return lineIterator.hasNext();
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

}
