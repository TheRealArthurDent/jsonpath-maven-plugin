package io.github.therealarthurdent.maven.jsonpath.boundary;

import io.github.therealarthurdent.maven.jsonpath.testutil.InjectionHelper;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.Mockito.when;

public class SetPropertiesMojoTest {

    private final AbstractMojo mojo = new SetPropertiesMojo();
    private final MavenSession mavenSession = Mockito.mock(MavenSession.class);

    @Before
    public void before() throws ReflectiveOperationException, URISyntaxException {
        URL inputFile = Thread.currentThread().getContextClassLoader().getResource("input.json");
        final String inputFileName = inputFile.toURI().getPath();
        InjectionHelper.inject(this.mojo, "file", inputFileName);
        InjectionHelper.inject(this.mojo, "session", this.mavenSession);
    }

    @Test
    public void execute() throws ReflectiveOperationException, MojoExecutionException, URISyntaxException {

        Map<String, String> properties = new HashMap<>(4);
        properties.put("testProperty1", "$.root.properties.testProperty1");
        properties.put("testProperty2", "$.root.properties.testProperty2");
        properties.put("testProperty3", "$.root.properties.testProperty3");
        properties.put("testPropertyComplex", "$.root.properties.testPropertyComplex");

        final Properties resultProperties = new Properties();

        InjectionHelper.inject(this.mojo, "properties", properties);

        when(this.mavenSession.getUserProperties()).thenReturn(resultProperties);

        this.mojo.execute();

        assertThat(resultProperties).isNotNull();
        assertThat(resultProperties.getProperty("testProperty1")).isEqualTo("propertyValue1");
        assertThat(resultProperties.getProperty("testProperty2")).isEqualTo("42");
        assertThat(resultProperties.getProperty("testProperty3")).isNull();
        assertThat(resultProperties.getProperty("testPropertyComplex")).isNotEmpty();
        assertThat(resultProperties.getProperty("testPropertyComplex")).contains("4711");
    }

    @Test
    public void executeNoProperties() throws ReflectiveOperationException, MojoExecutionException {
        Map<String, String> properties = Collections.emptyMap();
        final Properties resultProperties = new Properties();

        InjectionHelper.inject(this.mojo, "properties", properties);

        when(this.mavenSession.getUserProperties()).thenReturn(resultProperties);

        try {
            this.mojo.execute();
            fail("Expected MojoExecutionException to be thrown.");
        } catch (MojoExecutionException e) {
            assertThat(e.getMessage()).isEqualTo("No properties were defined for setting");
        }
    }
}