package example.generatePojo.spi.impl;

import example.generatePojo.model.Pojo;
import example.generatePojo.model.Property;
import example.generatePojo.model.Type;

import java.util.Collections;

/**
 * @author Maarten Van Puymbroeck
 */
public abstract class PluginSupport implements Plugin {

    @Override
    public Iterable<Type> getImplementedInterfaces(Pojo pojo) {
        return Collections.emptySet();
    }

    @Override
    public Iterable<Class<?>> getImportedClasses(Pojo pojo) {
        return Collections.emptySet();
    }

    @Override
    public Iterable<String> getMethods(Property property) {
        return Collections.emptySet();
    }

    @Override
    public Iterable<String> getMethods(Pojo pojo) {
        return Collections.emptySet();
    }
}
