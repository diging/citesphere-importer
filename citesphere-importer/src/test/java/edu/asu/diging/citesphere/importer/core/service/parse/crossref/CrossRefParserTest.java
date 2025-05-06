package edu.asu.diging.citesphere.importer.core.service.parse.crossref;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.Issn;
import edu.asu.diging.citesphere.importer.core.service.parse.impl.CrossRefParser;
import edu.asu.diging.crossref.model.Date;
import edu.asu.diging.crossref.model.IssnType;
import edu.asu.diging.crossref.model.Item;
import edu.asu.diging.crossref.model.Person;
import edu.asu.diging.crossref.model.Review;
import edu.asu.diging.crossref.model.impl.IssnTypeImpl;

public class CrossRefParserTest {
    private CrossRefParser crossRefParser;

    @Before
    public void setUp() {
        crossRefParser = new CrossRefParser();
    }

    @Test
    public void test_parseJournalMeta_withValidData() {
        Item item = Mockito.mock(Item.class);
        Mockito.when(item.getContainerTitle()).thenReturn(Collections.singletonList("Journal of Testing"));
        Mockito.when(item.getPublisher()).thenReturn("Test Publisher");
        Mockito.when(item.getPublisherLocation()).thenReturn("Test City");

        IssnType issnType1 = new IssnTypeImpl();
        issnType1.setValue("1234-5678");
        issnType1.setType("issn");
        IssnType issnType2 = new IssnTypeImpl();
        issnType2.setValue("8765-4321");
        issnType2.setType("issn");
        Mockito.when(item.getIssnType()).thenReturn(Arrays.asList(issnType1, issnType2));

        ContainerMeta result = crossRefParser.parseJournalMeta(item);

        Assert.assertNotNull(result);
        Assert.assertEquals("Journal of Testing", result.getContainerTitle());
        Assert.assertEquals("Test Publisher", result.getPublisherName());
        Assert.assertEquals("Test City", result.getPublisherLocation());

        List<Issn> issns = result.getIssns();
        Assert.assertNotNull(issns);
        Assert.assertEquals(2, issns.size());
        Assert.assertEquals("1234-5678", issns.get(0).getIssn());
        Assert.assertEquals("issn", issns.get(0).getPubType());
        Assert.assertEquals("8765-4321", issns.get(1).getIssn());
        Assert.assertEquals("issn", issns.get(1).getPubType());
    }

    @Test
    public void test_parseJournalMeta_withNoIssns() {
        Item item = Mockito.mock(Item.class);
        Mockito.when(item.getContainerTitle()).thenReturn(Collections.singletonList("Journal of Testing"));
        Mockito.when(item.getPublisher()).thenReturn("Test Publisher");
        Mockito.when(item.getPublisherLocation()).thenReturn("Test City");
        Mockito.when(item.getIssnType()).thenReturn(null);

        ContainerMeta result = crossRefParser.parseJournalMeta(item);

        Assert.assertNotNull(result);
        Assert.assertEquals("Journal of Testing", result.getContainerTitle());
        Assert.assertEquals("Test Publisher", result.getPublisherName());
        Assert.assertEquals("Test City", result.getPublisherLocation());
        Assert.assertNotNull(result.getIssns());
        Assert.assertTrue(result.getIssns().isEmpty());
    }

    @Test
    public void test_parseJournalMeta_withNoContainerTitle() {
        Item item = Mockito.mock(Item.class);
        Mockito.when(item.getContainerTitle()).thenReturn(Collections.emptyList());
        Mockito.when(item.getPublisher()).thenReturn("Test Publisher");
        Mockito.when(item.getPublisherLocation()).thenReturn("Test City");

        try {
            crossRefParser.parseJournalMeta(item);
            Assert.fail("Expected IndexOutOfBoundsException to be thrown");
        } catch (IndexOutOfBoundsException exception) {
            Assert.assertNotNull(exception);
        }
    }

    @Test
    public void test_parseJournalMeta_withNullItem() {
        try {
            crossRefParser.parseJournalMeta(null);
            Assert.fail("Expected NullPointerException to be thrown");
        } catch (NullPointerException exception) {
            Assert.assertNotNull(exception);
        }
    }

    @Test
    public void test_parseJournalMeta_withEmptyFields() {
        Item item = Mockito.mock(Item.class);
        Mockito.when(item.getContainerTitle()).thenReturn(Collections.singletonList(""));
        Mockito.when(item.getPublisher()).thenReturn("");
        Mockito.when(item.getPublisherLocation()).thenReturn("");
        Mockito.when(item.getIssnType()).thenReturn(Collections.emptyList());

        ContainerMeta result = crossRefParser.parseJournalMeta(item);

        Assert.assertNotNull(result);
        Assert.assertEquals("", result.getContainerTitle());
        Assert.assertEquals("", result.getPublisherName());
        Assert.assertEquals("", result.getPublisherLocation());
        Assert.assertNotNull(result.getIssns());
        Assert.assertTrue(result.getIssns().isEmpty());
    }
    
    @Test
    public void test_parseArticleMeta_withValidData() {
        // Arrange
        Item item = Mockito.mock(Item.class);
        Mockito.when(item.getTitle()).thenReturn(Collections.singletonList("Test Article Title"));
        Person author = mockPerson("John", "Doe", "1234");
        Mockito.when(item.getAuthor()).thenReturn(Arrays.asList(author));
        Mockito.when(item.getEditor()).thenReturn(null);
        Mockito.when(item.getTranslator()).thenReturn(null);
        Mockito.when(item.getChair()).thenReturn(null);

        Date publishedDate = Mockito.mock(Date.class);
        Mockito.when(publishedDate.getIndexedDateParts()).thenReturn(Arrays.asList(2023, 12, 25));
        Mockito.when(item.getPublished()).thenReturn(publishedDate);

        Mockito.when(item.getVolume()).thenReturn("10");
        Mockito.when(item.getIssue()).thenReturn("2");
        Mockito.when(item.getPartNumber()).thenReturn("A");
        Mockito.when(item.getPage()).thenReturn("123");
        Mockito.when(item.getUrl()).thenReturn("http://example.com");
        Mockito.when(item.getAbstractText()).thenReturn("Test Abstract");
        Mockito.when(item.getLanguage()).thenReturn("en");

        Review review = Mockito.mock(Review.class);
        Mockito.when(review.getCompetingInterestStatement()).thenReturn("No conflicts");
        Mockito.when(item.getReview()).thenReturn(review);

        Mockito.when(item.getType()).thenReturn("Research Article");
        Mockito.when(item.getReference()).thenReturn(Collections.emptyList());
        Mockito.when(item.getReferenceCount()).thenReturn(5);

        // Act
        ArticleMeta result = crossRefParser.parseArticleMeta(item);

        // Assert
        Assert.assertNotNull(result);
        Assert.assertEquals("Test Article Title", result.getArticleTitle());
        Assert.assertNotNull(result.getContributors());
        Assert.assertEquals(1, result.getContributors().size());
        Assert.assertEquals("John", result.getContributors().get(0).getGivenName());
        Assert.assertEquals("Doe", result.getContributors().get(0).getSurname());
        Assert.assertEquals("25", result.getPublicationDate().getPublicationDate());
        Assert.assertEquals("12", result.getPublicationDate().getPublicationMonth());
        Assert.assertEquals("2023", result.getPublicationDate().getPublicationYear());
        Assert.assertEquals("10", result.getVolume());
        Assert.assertEquals("2", result.getIssue());
        Assert.assertEquals("A", result.getPartNumber());
        Assert.assertEquals("123", result.getFirstPage());
        Assert.assertEquals("http://example.com", result.getSelfUri());
        Assert.assertEquals("Test Abstract", result.getArticleAbstract());
        Assert.assertEquals("en", result.getLanguage());
        Assert.assertEquals("No conflicts", result.getReviewInfo().getFullDescription());
        Assert.assertEquals("Research Article", result.getDocumentType());
        Assert.assertEquals("5", result.getReferenceCount());
    }

    @Test
    public void test_parseArticleMeta_withNullItem() {
        try {
            crossRefParser.parseArticleMeta(null);
            Assert.fail("Expected NullPointerException");
        } catch (NullPointerException exception) {
            Assert.assertNotNull(exception);
        }
    }

    @Test
    public void test_parseArticleMeta_withEmptyTitle() {
        Item item = Mockito.mock(Item.class);
        Mockito.when(item.getTitle()).thenReturn(Collections.emptyList());

        try {
            crossRefParser.parseArticleMeta(item);
            Assert.fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException exception) {
            // Assert
            Assert.assertNotNull(exception);
        }
    }

    private Person mockPerson(String given, String family, String orcid) {
        Person person = Mockito.mock(Person.class);
        Mockito.when(person.getGiven()).thenReturn(given);
        Mockito.when(person.getFamily()).thenReturn(family);
        Mockito.when(person.getName()).thenReturn(given + " " + family);
        Mockito.when(person.getOrcid()).thenReturn(orcid);
        Mockito.when(person.getAffiliation()).thenReturn(Collections.emptyList());
        return person;
    }
    
}
