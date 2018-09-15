package io.github.therealarthurdent.maven.jsonpath.testutil;

import io.github.therealarthurdent.maven.jsonpath.entity.Modification;

public class ModificationBuilder {

    private final Modification modification;

    public ModificationBuilder() {
        super();
        this.modification = new Modification();
    }

    public ModificationBuilder withPath(final String path) throws ReflectiveOperationException {
        InjectionHelper.inject(this.modification, "path", path);
        return this;
    }

    public ModificationBuilder withKey(final String key) throws ReflectiveOperationException {
        InjectionHelper.inject(this.modification, "key", key);
        return this;
    }

    public ModificationBuilder withValue(final String value) throws ReflectiveOperationException {
        InjectionHelper.inject(this.modification, "value", value);
        return this;
    }

    public Modification build() {
        return this.modification;
    }
}
