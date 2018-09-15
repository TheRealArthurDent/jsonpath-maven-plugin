package io.github.therealarthurdent.maven.jsonpath.boundary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.jayway.jsonpath.DocumentContext;
import io.github.therealarthurdent.maven.jsonpath.entity.Modification;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Mojo(name = "modify", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class ModifyMojo extends AbstractMojo {

    @Parameter(property = "jsonpath.outputFile", required = false)
    private String outputFile;

    @Parameter(required = true)
    private List<Modification> modifications;

    @Override
    public void execute() throws MojoExecutionException {
        FileSystem fs = FileSystems.getDefault();
        Path outputJson = this.outputFile == null ? fs.getPath(this.file) : fs.getPath(this.outputFile);

        DocumentContext json = this.getDocumentContext();

        int count = 0;

        for (Modification modification : this.modifications) {
            final String path = modification.getPath();
            final String value = modification.getValue();
            if (modification.isAddModification()) {
                final String key = modification.getKey();
                json.put(path, key, value);
                this.getLog().info("added: " + path + "." + key + "=" + value);
            } else {
                json.set(path, value);
                this.getLog().info("modified" + path + "=" + value);
            }
            count++;
        }

        try (OutputStream out = Files.newOutputStream(outputJson)) {
            ObjectWriter writer = new ObjectMapper().writerWithDefaultPrettyPrinter();
            writer.writeValue(out, json.json());
        } catch (IOException e) {
            this.getLog().error("Unable to write output json file");
            throw new MojoExecutionException("Unable write file '" + outputJson + "'", e);
        }

        if (count == 0) {
            this.getLog().error(count + " modifications written to json file " + outputJson);
            throw new MojoExecutionException("No modifications were defined");
        }
        this.getLog().info(count + " modifications written to json file " + outputJson);
    }

}
