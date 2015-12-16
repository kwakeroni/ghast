package example.generatePojo.spi.impl;

import example.generatePojo.model.AbstractType;
import example.generatePojo.model.Pojo;
import example.generatePojo.model.Property;
import example.generatePojo.model.Type;
import example.generatePojo.spi.CodeWriterSupport;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author Maarten Van Puymbroeck
 */
public class ExternalizablePlugin extends PluginSupport {

    @Override
    public Iterable<Class<?>> getImportedClasses(Pojo pojo) {
        return Arrays.asList(Externalizable.class, ObjectInput.class, ObjectOutput.class, IOException.class);
    }

    @Override
    public Iterable<Type> getImplementedInterfaces(Pojo pojo) {
        return Collections.singleton(AbstractType.of(Externalizable.class));
    }

    @Override
    public Iterable<String> getMethods(Pojo pojo) {
        return Iterables.concat(
                                   getExternalizableRead(pojo),
                                   CodeWriterSupport.emptyLine(),
                                   getExternalizableWrite(pojo)
        );
    }


    private Iterable<String> getExternalizableRead(Pojo pojo) {
        return Iterables.concat(
                                   Arrays.asList("    @Override",
                                                 "    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {",
                                                 "        super.readExternal(in);"
                                   ),
                                   Iterables.transform(pojo.getProperties(), readProperty()),
                                   Arrays.asList("    }")
        );
    }

    private Iterable<String> getExternalizableWrite(Pojo pojo) {
        return Iterables.concat(
                                   Arrays.asList("    @Override",
                                                 "    public void writeExternal(ObjectOutput out) throws IOException {",
                                                 "        super.writeExternal(out);"
                                   ),
                                   Iterables.transform(pojo.getProperties(), writeProperty()),
                                   Arrays.asList("    }")
        );
    }

    private String readProperty(Property property) {
        return MessageFormat.format("        this.{0} = ({1}) in.readObject();", property.getName(), property.getType().getSimpleName());
    }

    private String writeProperty(Property property) {
        return MessageFormat.format("        out.writeObject({0});", property.getName(), property.getType().getSimpleName());
    }


    private Function<Property, String> readProperty() {
        return new Function<Property, String>() {
            @Override
            public String apply(Property input) {
                return readProperty(input);
            }
        };
    }

    private Function<Property, String> writeProperty() {
        return new Function<Property, String>() {
            @Override
            public String apply(Property input) {
                return writeProperty(input);
            }
        };
    }
}
