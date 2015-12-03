package example.generatePojo.spi.impl;

import example.generatePojo.Util;
import example.generatePojo.model.Pojo;
import example.generatePojo.model.Property;
import example.generatePojo.model.Type;
import example.generatePojo.spi.PojoWriterSupport;
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
public class PojoImplWriter extends PojoWriterSupport<Pojo> {

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

    @Override
    protected Iterable<String> toCode(Pojo pojo) {
        return Iterables.concat(
                                   getPackageDeclaration(pojo),
                                   emptyLine(),
                                   getImports(pojo),
                                   emptyLine(),
                                   getClassCode(pojo)
        );
    }

    private Iterable<String> getPackageDeclaration(Pojo pojo) {
        return Collections.singleton("package " + pojo.getPackage() + ";");
    }

    private Iterable<String> getImports(Pojo pojo) {
        return

            Iterables.transform(Iterables.concat(Iterables.transform(this.plugins, imports(pojo))),
                                   importClass()

            );
    }


    private Iterable<String> getClassCode(Pojo pojo) {
        return Util.concat(
                              getClassSignature(pojo) + " {",
                              Iterables.concat(
                                                  emptyLine(),
                                                  getFields(pojo),
                                                  getMethods(pojo)),
                              "}"
        );
    }

    private Iterable<String> getMethods(Pojo pojo) {
        return Iterables.concat(Iterables.transform(this.plugins, methods(pojo)));
    }


    private String getClassSignature(Pojo pojo) {
        StringBuilder builder = new StringBuilder("public class ");
        builder.append(pojo.getSimpleName());
        if (pojo.getExtends() != null) {
            builder.append(" extends " + pojo.getExtends().getSimpleName());
        }
        boolean first = true;
        for (Type impl : Iterables.concat(Iterables.transform(plugins, implementedInterfaces(pojo)))) {
            if (first) {
                first = false;
                builder.append(" implements ");
            } else {
                builder.append(", ");
            }
            builder.append(impl.getSimpleName());
        }
        return builder.toString();
    }

    private Iterable<String> getFields(Pojo pojo) {
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

    private Function<Class<?>, String> importClass() {
        return new Function<Class<?>, String>() {
            @Override
            public String apply(Class<?> input) {
                return "import " + input.getName() + ";";
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
                return Iterables.concat(emptyLine(), input.getMethods(pojo));
            }
        };
    }

    private static List<Plugin> asList(Plugin first, Plugin... rest) {
        List<Plugin> list = new ArrayList<>(1 + rest.length);
        list.add(new BasicPlugin());
        list.addAll(Arrays.asList(rest));
        return list;
    }

}
