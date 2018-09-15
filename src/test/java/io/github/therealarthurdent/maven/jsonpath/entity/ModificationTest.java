package io.github.therealarthurdent.maven.jsonpath.entity;

import io.github.therealarthurdent.maven.jsonpath.testutil.ModificationBuilder;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ModificationTest {

    @Test
    public void getters() throws ReflectiveOperationException {
        Modification modification = new ModificationBuilder().withPath("aPath").withValue("aValue").build();
        assertThat(modification).isNotNull();
        assertThat(modification.isAddModification()).isFalse();
        assertThat(modification.getPath()).isEqualTo("aPath");
        assertThat(modification.getKey()).isNull();
        assertThat(modification.getValue()).isEqualTo("aValue");

        modification = new ModificationBuilder().withPath("aPath").withKey("aKey").withValue("aValue").build();
        assertThat(modification).isNotNull();
        assertThat(modification.isAddModification()).isTrue();
        assertThat(modification.getPath()).isEqualTo("aPath");
        assertThat(modification.getKey()).isEqualTo("aKey");
        assertThat(modification.getValue()).isEqualTo("aValue");
    }

}