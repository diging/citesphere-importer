package edu.asu.diging.citesphere.importer.core.service.parse;

import edu.asu.diging.citesphere.importer.core.service.impl.JobInfo;

public interface IHandlerRegistry {

    void handleFile(JobInfo info, String filePath);

}