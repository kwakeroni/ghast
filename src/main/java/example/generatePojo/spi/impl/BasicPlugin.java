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

/**
 * @author Maarten Van Puymbroeck
 */
public class BasicPlugin implements Plugin {

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

    @Override
    public Iterable<String> getMethods(Pojo pojo) {
        return getGettersAndSetters(pojo);
    }


    private Iterable<String> getGettersAndSetters(Pojo pojo) {
        return Iterables.concat(
                                   Iterables.transform(pojo.getProperties(), getterAndSetter())
        );
    }


    private Iterable<String> getGetterAndSetter(Property property) {
        return Iterables.concat(getGetter(property), emptyLine(), getSetter(property), emptyLine());
    }

    private Iterable<String> getGetter(Property property) {
        return Arrays.asList(
                                MessageFormat.format("    public {0} {1}{2}() '{'", property.getType().getSimpleName(), (property.getType().isEqualType(boolean.class)) ? "is" : "get", StringUtils.capitalize(property.getName())),
                                MessageFormat.format("        return this.{0};", property.getName()),
                                MessageFormat.format("    }", "")
        );
    }

    private Iterable<String> getSetter(Property property) {
        return Arrays.asList(
                                MessageFormat.format("    public void set{0}({1} {2}) '{'", StringUtils.capitalize(property.getName()), property.getType().getSimpleName(), property.getName()),
                                MessageFormat.format("        this.{0} = {0};", property.getName()),
                                MessageFormat.format("    }", "")
        );
    }


    private Predicate<Class<?>> isNotJavaLang() {
        return new Predicate<Class<?>>() {
            @Override
            public boolean apply(Class<?> input) {
                return input.getPackage() != null && !input.getPackage().getName().equals("java.lang");
            }
        };
    }

    private Function<Property, Iterable<String>> getterAndSetter() {
        return new Function<Property, Iterable<String>>() {
            @Override
            public Iterable<String> apply(Property input) {
                return getGetterAndSetter(input);
            }
        };
    }
}
