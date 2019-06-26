package edu.asu.diging.citesphere.importer.core.service.parse.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.importer.core.exception.IteratorCreationException;
import edu.asu.diging.citesphere.importer.core.service.impl.JobInfo;
import edu.asu.diging.citesphere.importer.core.service.parse.BibEntryIterator;
import edu.asu.diging.citesphere.importer.core.service.parse.FileHandler;
import edu.asu.diging.citesphere.importer.core.service.parse.IHandlerRegistry;
import edu.asu.diging.citesphere.importer.core.service.parse.iterators.MultipleEntryIterator;

@Service
public class ZipFileHandler implements FileHandler {

    @Override
    public boolean canHandle(String path) {
        if (path.toLowerCase().endsWith(".zip")) {
            return true;
        }
        return false;
    }

    @Override
    public BibEntryIterator getIterator(String path, IHandlerRegistry callback, JobInfo info)
            throws IteratorCreationException {
        try (ZipFile zipFile = new ZipFile(new File(path))) {
            MultipleEntryIterator iterator = new MultipleEntryIterator();
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            String storageFolder = new File(path).getParent();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    String destPath = storageFolder + File.separator + entry.getName();
                    File file = new File(destPath);
                    file.mkdirs();
                } else {
                    String destPath = storageFolder + File.separator + entry.getName();

                    try (InputStream inputStream = zipFile.getInputStream(entry);
                            FileOutputStream outputStream = new FileOutputStream(destPath);) {
                        int data = inputStream.read();
                        while (data != -1) {
                            outputStream.write(data);
                            data = inputStream.read();
                        }
                    }

                    iterator.addIterator(callback.handleFile(info, destPath));
                }
            }

            return iterator;
        } catch (ZipException e) {
            throw new IteratorCreationException("Could not read zip file.", e);
        } catch (IOException e) {
            throw new IteratorCreationException("Could not read zip file.", e);
        }
    }

}
