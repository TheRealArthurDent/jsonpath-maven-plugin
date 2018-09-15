package io.github.therealarthurdent.maven.jsonpath.testutil;

import java.lang.reflect.Field;

public class InjectionHelper {

    public static void inject(final Object injectee, final String fieldname, final Object value) throws ReflectiveOperationException {
        Field field = getField(injectee.getClass(), fieldname);
        field.setAccessible(true);
        field.set(injectee, value);
    }

    private static Field getField(final Class<?> injecteeClass, final String fieldname) throws NoSuchFieldException {
        try {
            return injecteeClass.getDeclaredField(fieldname);
        } catch (NoSuchFieldException e) {
            Class<?> superclass = injecteeClass.getSuperclass();
            if (superclass != null) {
                return getField(superclass, fieldname);
            } else {
                throw e;
            }
        }
    }

}