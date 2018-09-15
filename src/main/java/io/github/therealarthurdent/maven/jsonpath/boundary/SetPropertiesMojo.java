package io.github.therealarthurdent.maven.jsonpath.boundary;

import com.fasterxml.jackson.databind.JsonNode;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.PathNotFoundException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

@Mojo(name = "set-properties", defaultPhase = LifecyclePhase.INITIALIZE)
public class SetPropertiesMojo extends AbstractMojo {
    @Parameter(defaultValue = "${session}", readonly = true)
    private MavenSession session;

    @Parameter(required = true)
    private Map<String, String> properties;

    @Override
    public void execute() throws MojoExecutionException {
        DocumentContext json = this.getDocumentContext();

        int count = 0;

        Properties sessionProperties = this.session.getUserProperties();
        for (Entry<String, String> entry : this.properties.entrySet()) {
            String propertyName = entry.getKey();
            String propertyJsonPath = entry.getValue();
            this.getLog().debug("Reading value for " + propertyName + " with JsonPath expression " + propertyJsonPath);
            try {
                JsonNode jsonNode = json.read(propertyJsonPath);
                if (jsonNode.isValueNode()) {
                    sessionProperties.setProperty(propertyName, jsonNode.asText());
                    this.getLog().info(propertyName + "=" + jsonNode.asText());
                } else {
                    sessionProperties.setProperty(propertyName, jsonNode.toString());
                    this.getLog().info(propertyName + "=" + jsonNode.toString());
                }
                count++;
            } catch (PathNotFoundException e) {
                this.getLog().warn("Reading value for " + propertyName + " failed! Path was not found.");
            }
        }

        if (count == 0) {
            this.getLog().error(count + " build properties set from json file " + this.file);
            throw new MojoExecutionException("No properties were defined for setting");
        }
        this.getLog().info(count + " build properties set from json file " + this.file);
    }
}
