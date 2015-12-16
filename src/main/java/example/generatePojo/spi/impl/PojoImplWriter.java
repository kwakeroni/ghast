package example.generatePojo.spi.impl;

import com.google.common.base.Optional;
import example.generatePojo.Util;
import example.generatePojo.model.Pojo;
import example.generatePojo.model.Property;
import example.generatePojo.model.Type;
import example.generatePojo.spi.ClassWriterSupport;
import example.generatePojo.spi.CodeWriterSupport;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Maarten Van Puymbroeck
 */
public class PojoImplWriter extends ClassWriterSupport<Pojo> {

    private List<Plugin> plugins;

    public PojoImplWriter(Path src) {
        this(src, Collections.<Plugin>singletonList(new BasicPlugin()));
    }

    public PojoImplWriter(Path src, Plugin... plugins) {
        this(src, asList(new BasicPlugin(), plugins));
    }

    private PojoImplWriter(Path src, List<Plugin> plugins) {
        super(src);
        this.plugins = plugins;
    }


    @Override
    protected Path getTarget(Pojo pojo) {
        return Paths.get(pojo.getPackage().replaceAll("\\.", "/")).resolve(pojo.getSimpleName() + ".java");
    }

    protected Iterable<String> getPackageDeclaration(Pojo pojo) {
        return getPackageDeclaration(pojo.getPackage());
    }


    protected Iterable<String> getImports(Pojo pojo) {
        return getImports(Iterables.concat(Iterables.transform(this.plugins, imports(pojo))));
    }




    protected Iterable<String> getMethods(Pojo pojo) {
        return Iterables.concat(
                    emptyLine(),
                    Iterables.concat(Iterables.transform(this.plugins, methods(pojo))),
                    Iterables.concat(Iterables.transform(pojo.getProperties(), propertyMethods()))
        );
    }

    protected Iterable<String> getMethods(Property property) {
        return Iterables.concat(Iterables.transform(this.plugins, methods(property)));
    }


    protected String getClassSignature(Pojo pojo) {
        return getClassSignature(pojo.getSimpleName(),
                                    Optional.fromNullable(pojo.getExtends()),
                                    Iterables.concat(Iterables.transform(plugins, implementedInterfaces(pojo))));
    }

    protected Iterable<String> getFields(Pojo pojo) {
        return Iterables.transform(pojo.getProperties(), fieldDeclaration());
    }


    private String getFieldDeclaration(Property property) {
        return MessageFormat.format("    private {0} {1};", property.getType().getSimpleName(), property.getName());
    }

    private Function<Property, String> fieldDeclaration() {
        return new Function<Property, String>() {
            @Override
            public String apply(Property input) {
                return getFieldDeclaration(input);
            }
        };
    }


    private Function<Plugin, Iterable<Class<?>>> imports(final Pojo pojo) {
        return new Function<Plugin, Iterable<Class<?>>>() {
            @Override
            public Iterable<Class<?>> apply(Plugin input) {
                return input.getImportedClasses(pojo);
            }
        };
    }

    private Function<Plugin, Iterable<Type>> implementedInterfaces(final Pojo pojo) {
        return new Function<Plugin, Iterable<Type>>() {
            @Override
            public Iterable<Type> apply(Plugin input) {
                return input.getImplementedInterfaces(pojo);
            }
        };
    }

    private Function<Plugin, Iterable<String>> methods(final Pojo pojo) {
        return new Function<Plugin, Iterable<String>>() {
            @Override
            public Iterable<String> apply(Plugin input) {
                return input.getMethods(pojo);
            }
        };
    }

    private Function<Plugin, Iterable<String>> methods(final Property property) {
        return new Function<Plugin, Iterable<String>>() {
            @Override
            public Iterable<String> apply(Plugin input) {
                return input.getMethods(property);
            }
        };
    }

    private Function<Property, Iterable<String>> propertyMethods(){
        return new Function<Property, Iterable<String>>() {
            @Override
            public Iterable<String> apply(Property input) {
                return getMethods(input);
            }
        };
    }



    private static List<Plugin> asList(Plugin first, Plugin... rest) {
        List<Plugin> list = new ArrayList<>(1 + ((rest == null)? 0 : rest.length));
        list.add(new BasicPlugin());
        list.addAll(Arrays.asList(rest));
        return list;
    }

}
