package io.github.therealarthurdent.maven.jsonpath.boundary;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class AbstractMojo extends org.apache.maven.plugin.AbstractMojo {
    @Parameter(property = "jsonpath.file", required = true)
    protected String file;

    @Override
    public abstract void execute() throws MojoExecutionException;

    protected DocumentContext getDocumentContext() throws MojoExecutionException {
        FileSystem fs = FileSystems.getDefault();
        Path inputJson = fs.getPath(this.file);

        Configuration configuration = Configuration.builder()
                .jsonProvider(new JacksonJsonNodeJsonProvider())
                .mappingProvider(new JacksonMappingProvider())
                .build();

        DocumentContext json;
        try (InputStream in = Files.newInputStream(inputJson)) {
            json = JsonPath.using(configuration).parse(in, "UTF-8");
        } catch (IOException e) {
            this.getLog().error("Unable to read input json file");
            throw new MojoExecutionException("Unable to read file '" + this.file + "'", e);
        }
        return json;
    }
}
