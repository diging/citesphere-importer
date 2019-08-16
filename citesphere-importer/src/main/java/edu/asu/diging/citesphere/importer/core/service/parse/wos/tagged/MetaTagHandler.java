package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

public abstract class MetaTagHandler implements WoSMetaTagHandler {

    protected String[] splitValues(String value, boolean isColumnFormat) {
        if (value == null || value.trim().isEmpty()) {
            return new String[0];
        }
        if (isColumnFormat) {
            return value.split(";");
        }
        return new String[] { value };
    }
}
