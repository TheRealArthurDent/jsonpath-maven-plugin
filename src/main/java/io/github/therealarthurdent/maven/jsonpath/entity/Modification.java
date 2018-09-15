package io.github.therealarthurdent.maven.jsonpath.entity;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugins.annotations.Parameter;

public class Modification {
    @Parameter(required = true)
    private String path;

    @Parameter(required = false)
    private String key;

    @Parameter(required = true)
    private String value;

    public String getPath() {
        return this.path;
    }

    public String getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }

    public boolean isAddModification() {
        return StringUtils.isNotEmpty(this.key);
    }
}
