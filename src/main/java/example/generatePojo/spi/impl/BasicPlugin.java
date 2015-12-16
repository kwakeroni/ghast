package example.generatePojo.spi.impl;

import example.generatePojo.Util;
import example.generatePojo.model.Pojo;
import example.generatePojo.model.Property;
import example.generatePojo.model.Type;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.Arrays;

import static example.generatePojo.spi.impl.PojoImplWriter.*;

/**
 * @author Maarten Van Puymbroeck
 */
public class BasicPlugin extends PluginSupport {

    @Override
    public Iterable<Class<?>> getImportedClasses(Pojo pojo) {
        return
            Util.distinct(
                             Iterables.filter(Iterables.concat(
                                                                  Iterables.transform(
                                                                                         Iterables.concat(Util.fromNullable(pojo.getExtends()),
                                                                                                             pojo.getImplements(),
                                                                                                             Iterables.concat(Iterables.transform(pojo.getProperties(), Property.$getType))),
                                                                                         Type.$getTypeDeclarationDependencies)),
                                                 isNotJavaLang()
                             ));
    }

    @Override
    public Iterable<Type> getImplementedInterfaces(Pojo pojo) {
        return pojo.getImplements();
    }

    private Predicate<Class<?>> isNotJavaLang() {
        return new Predicate<Class<?>>() {
            @Override
            public boolean apply(Class<?> input) {
                return input.getPackage() != null && !input.getPackage().getName().equals("java.lang");
            }
        };
    }

}
