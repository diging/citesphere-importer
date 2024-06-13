package edu.asu.diging.citesphere.importer.core.service;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.diging.citesphere.importer.core.exception.IteratorCreationException;
import edu.asu.diging.citesphere.importer.core.kafka.impl.KafkaRequestProducer;
import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.ItemType;
import edu.asu.diging.citesphere.importer.core.model.impl.CrossRefPublication;
import edu.asu.diging.citesphere.importer.core.service.impl.CrossrefReferenceImportProcessor;
import edu.asu.diging.citesphere.importer.core.service.impl.JobInfo;
import edu.asu.diging.citesphere.importer.core.service.parse.BibEntryIterator;
import edu.asu.diging.citesphere.importer.core.service.parse.IHandlerRegistry;
import edu.asu.diging.citesphere.importer.core.zotero.IZoteroConnector;
import edu.asu.diging.citesphere.importer.core.zotero.template.IJsonGenerationService;
import edu.asu.diging.citesphere.messages.model.KafkaJobMessage;
import edu.asu.diging.citesphere.messages.model.ResponseCode;
import edu.asu.diging.citesphere.messages.model.Status;

@RunWith(MockitoJUnitRunner.class)
public class CrossrefReferenceImportProcessorTest {

    @Spy
    @InjectMocks
    private CrossrefReferenceImportProcessor processor;

    @Mock
    private IZoteroConnector zoteroConnector;

    @Mock
    private IJsonGenerationService generationService;

    @Mock
    private IHandlerRegistry handlerRegistry;

    @Mock
    private BibEntryIterator bibIterator;

    @Mock
    private KafkaJobMessage message;

    @Mock
    private JobInfo info;

    @Mock
    private KafkaRequestProducer requestProducer;

    private ObjectMapper mapper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        processor.init();
        mapper = new ObjectMapper();
        when(message.getId()).thenReturn("testMessageId");
        doNothing().when(processor).sendMessage(any(), anyString(), any(Status.class), any(ResponseCode.class));
    }

    @Test
    public void testStartImport_successful() throws Exception {
        when(info.getDois()).thenReturn(Arrays.asList("10.1234/example1", "10.5678/example2"));
        when(handlerRegistry.handleFile(info, null)).thenReturn(bibIterator);

        BibEntry entry = mock(BibEntry.class);
        when(bibIterator.hasNext()).thenReturn(true, false);
        when(bibIterator.next()).thenReturn(entry);
        when(entry.getArticleType()).thenReturn(CrossRefPublication.ARTICLE);

        JsonNode template = mock(ObjectNode.class);
        when(zoteroConnector.getTemplate(ItemType.JOURNAL_ARTICLE)).thenReturn(template);
        ObjectNode bibNode = mapper.createObjectNode();
        when(generationService.generateJson(template, entry)).thenReturn(bibNode);

        processor.startImport(message, info);

        verify(zoteroConnector).addEntries(eq(info), any(ArrayNode.class));
        verify(processor).sendMessage(any(), eq("testMessageId"), eq(Status.DONE), eq(ResponseCode.S00));
    }

    @Test
    public void testStartImport_iteratorCreationException() throws Exception {
        when(info.getDois()).thenReturn(Arrays.asList("10.1234/example1", "10.5678/example2"));
        when(handlerRegistry.handleFile(info, null)).thenThrow(new IteratorCreationException("error"));

        processor.startImport(message, info);

        verify(processor).sendMessage(null, message.getId(), Status.FAILED, ResponseCode.X30);
    }

}
