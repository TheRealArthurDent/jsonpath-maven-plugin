package io.github.therealarthurdent.maven.jsonpath.boundary;

import io.github.therealarthurdent.maven.jsonpath.entity.Modification;
import io.github.therealarthurdent.maven.jsonpath.testutil.InjectionHelper;
import io.github.therealarthurdent.maven.jsonpath.testutil.ModificationBuilder;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

public class ModifyMojoTest {

    private final AbstractMojo mojo = new ModifyMojo();

    @Before
    public void before() throws ReflectiveOperationException, URISyntaxException {
        URL inputFile = Thread.currentThread().getContextClassLoader().getResource("input.json");
        final String inputFileName = inputFile.toURI().getPath();
        final String outputFileName = inputFileName.replace("input.json", "output.json");
        InjectionHelper.inject(this.mojo, "file", inputFileName);
        InjectionHelper.inject(this.mojo, "outputFile", outputFileName);
    }

    @Test
    public void execute() throws MojoExecutionException, ReflectiveOperationException, IOException {

        List<Modification> modifications = Arrays.asList(
                new ModificationBuilder().withPath("$.root.branch_1[:1]").withValue("leaf_1.1_changed").build(),
                new ModificationBuilder().withPath("$.root").withKey("branch_3").withValue("branch_3_added").build());

        InjectionHelper.inject(this.mojo, "modifications", modifications);

        this.mojo.execute();

        InputStream outFileStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("output.json");
        String outFileString = this.getAsString(outFileStream);
        assertThat(outFileString).isNotEmpty();
        assertThat(outFileString).contains("\"branch_3\":\"branch_3_added\"");
        assertThat(outFileString).contains("\"leaf_1.1_changed\"");
        assertThat(outFileString).doesNotContain("\"leaf_1.1\"");
    }

    @Test
    public void executeModificationsEmpty() throws ReflectiveOperationException, MojoExecutionException {
        InjectionHelper.inject(this.mojo, "modifications", Collections.emptyList());

        try {
            this.mojo.execute();
            fail("Expected MojoExecutionException to be thrown.");
        } catch (MojoExecutionException e) {
            assertThat(e.getMessage()).isEqualTo("No modifications were defined");
        }
    }

    /**
     * Gets the complete file as a linearized {@link String}.
     * Thereby removes all blanks which is not really correct, because it also removes blanks within text values.
     * However for our testing purposes, that is fine.
     *
     * @param fileStream {@link InputStream} on the file to read
     * @return the file content as a linearized {@link String}
     */
    private String getAsString(InputStream fileStream) throws IOException {
        final StringBuilder stringBuilder = new StringBuilder();
        String line = null;

        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(fileStream, StandardCharsets.UTF_8))) {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line.replace(" ", ""));
            }
        }
        return stringBuilder.toString();
    }

}