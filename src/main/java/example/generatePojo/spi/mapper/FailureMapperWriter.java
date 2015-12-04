package example.generatePojo.spi.mapper;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import example.generatePojo.Util;
import example.generatePojo.model.AbstractType;
import example.generatePojo.model.Pojo;
import example.generatePojo.model.Property;
import example.generatePojo.model.Type;
import example.generatePojo.spi.ClassWriterSupport;
import example.generatePojo.spi.CodeWriterSupport;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author Maarten Van Puymbroeck
 */
public class FailureMapperWriter extends ClassWriterSupport<Pojo> {



    public FailureMapperWriter(Path src) {
        super(src);
    }

    @Override
    protected Path getTarget(Pojo pojo) {
        return getPackagePath(pojo).resolve(pojo.getSimpleName() + "FailureMapper.java");
    }

    private Path getPackagePath(Pojo pojo){
        return Paths.get(pojo.getPackage().replaceAll("\\.", "/"));
    }

    @Override
    protected String getClassSignature(Pojo pojo) {

        Type pojoType = AbstractType.ofExternal(pojo.getSimpleName());
        Type failureType = AbstractType.ofExternal(pojo.getSimpleName() + "ProcessingFailed");

        return getClassSignature(pojo.getSimpleName() + "FailureMapper", Optional.<Type>absent(), Collections.singleton(AbstractType.ofExternal("Mapper", pojoType, failureType)));

    }

    @Override
    protected Iterable<String> getPackageDeclaration(Pojo pojo) {
        return getPackageDeclaration(getPackagePath(pojo).toString().replaceAll("\\/", "."));
    }

    @Override
    protected Iterable<String> getImports(Pojo pojo) {
        return Iterables.transform(Arrays.asList(
                                                    "be.voo.esb.unification.interfaces.Mapper",
                                                    pojo.getName(),
                                                    pojo.getName() + "ProcessingFailed"),
                                      importClassByName());
    }

    @Override
    protected Iterable<String> getFields(Pojo pojo) {
        return Collections.emptyList();
    }

    @Override
    protected Iterable<String> getMethods(Pojo pojo) {
        return indent(getMapMethod(pojo));
    }

    private Iterable<String> getMapMethod(Pojo pojo){
        String fromType = pojo.getSimpleName();
        String toType =  pojo.getSimpleName() + "ProcessingFailed";
        return Iterables.concat(
                                   Arrays.asList("@Override",
                                                    "public " + toType + " map(" + fromType + " event){"),
                                   indent(Iterables.concat(
                                                         Arrays.asList(toType + " failure = new " + toType + "();"),
                                                         emptyLine(),
                                                         Iterables.transform(pojo.getProperties(), copy("event", "failure")),
                                                         emptyLine(),
                                                         Arrays.asList("return failure;")
                                                         )),
                                   line("}")
        );
    }

    private Iterable<String> indent(Iterable<String> iterable){
        return Iterables.transform(iterable, indent("    "));
    }

    private Function<String, String> indent(String prefix){
        return new Function<String, String>() {
            @Override
            public String apply(String input) {
                return "    " + input;
            }
        };
    }

    private Function<Property, String> copy(final String from, final String to) {
        return new Function<Property, String>() {
            @Override
            public String apply(Property input) {
                return to + "." + method("set", input) + "(" + from + "." + method("get", input) + "());";
            }
        };
    }

    private String method(String name, Property property){
        return name + StringUtils.capitalize(property.getName());
    }
}
