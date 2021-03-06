package edu.asu.diging.citesphere.importer.core.service.parse.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.io.input.BOMInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.importer.core.exception.HandlerTestException;
import edu.asu.diging.citesphere.importer.core.exception.IteratorCreationException;
import edu.asu.diging.citesphere.importer.core.service.impl.JobInfo;
import edu.asu.diging.citesphere.importer.core.service.parse.BibEntryIterator;
import edu.asu.diging.citesphere.importer.core.service.parse.FileHandler;
import edu.asu.diging.citesphere.importer.core.service.parse.IHandlerRegistry;
import edu.asu.diging.citesphere.importer.core.service.parse.iterators.WoSTaggedFieldsIterator;
import edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged.IArticleWoSTagParser;

@Service
public class WoSHandler implements FileHandler {

    @Autowired
    private IArticleWoSTagParser parserRegistry;
    
    @Value("${_citesphere_download_path}")
    private String downloadPath;

    @Override
    public boolean canHandle(String path) throws HandlerTestException {
        File file = new File(path);
        try {
            BOMInputStream inputStream = new BOMInputStream(FileUtils.openInputStream(file), false,
                    ByteOrderMark.UTF_8, ByteOrderMark.UTF_16BE, ByteOrderMark.UTF_16LE,
                    ByteOrderMark.UTF_32BE, ByteOrderMark.UTF_32LE);
            if (inputStream.hasBOM()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                File fileCopy = new File(file.getParent() +  File.separator + "CopyOf" + file.getName());
                while(reader.ready()) {
                    FileUtils.write(fileCopy, reader.readLine(), inputStream.getBOMCharsetName(), true);
                }
                reader.close();
                Files.copy(fileCopy.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                Files.deleteIfExists(fileCopy.toPath());
            }
        } catch (IOException e1) {
            throw new HandlerTestException("Unsupported file format.", e1);
        }

        if (path.toLowerCase().endsWith(".txt") && !file.getName().startsWith(".")) {
            try (LineIterator it = FileUtils.lineIterator(file)) {
                int linesToRead = 10;
                int linesRead = 0;

                // we check the first 10 lines if they start with two capitals letters
                // followed by a space; if they all match, we assume it's WoS' data format.
                while (it.hasNext() && linesRead < linesToRead) {
                    String line = it.nextLine();
                    line = line.replaceAll("\\p{C}", "");
                    if (!line.matches("([A-Z0-9]{2}| {2})( .*$|$)") && !line.trim().isEmpty()) {
                        return false;
                    }
                    linesRead++;
                }

                return true;
            } catch (IOException e) {
                throw new HandlerTestException("Could not read lines.", e);
            }
        }
        return false;
    }

    @Override
    public BibEntryIterator getIterator(String path, IHandlerRegistry callback, JobInfo info)
            throws IteratorCreationException {
        return new WoSTaggedFieldsIterator(path, parserRegistry);
    }

}
