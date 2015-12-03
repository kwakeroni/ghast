package example.generatePojo.spi.impl;

import example.generatePojo.model.Pojo;
import example.generatePojo.model.Type;

/**
 * @author Maarten Van Puymbroeck
 */
public interface Plugin {

    Iterable<Class<?>> getImportedClasses(Pojo pojo);

    Iterable<Type> getImplementedInterfaces(Pojo pojo);

    Iterable<String> getMethods(Pojo pojo);
}
