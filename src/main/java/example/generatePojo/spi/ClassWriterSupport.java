package example.generatePojo.spi;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import example.generatePojo.Util;
import example.generatePojo.model.Pojo;
import example.generatePojo.model.Type;
import example.generatePojo.spi.CodeWriterSupport;

import java.nio.file.Path;
import java.util.Collections;

/**
 * @author Maarten Van Puymbroeck
 */
public abstract class ClassWriterSupport<Input> extends CodeWriterSupport<Input> {

    public ClassWriterSupport(Path src) {
        super(src);
    }
    

    @Override
    protected Iterable<String> toCode(Input input) {
        return Iterables.concat(
                                   getPackageDeclaration(input),
                                   emptyLine(),
                                   getImports(input),
                                   emptyLine(),
                                   getClassCode(input)
        );
    }
    
    private Iterable<String> getClassCode(Input input) {
        return Util.concat(
                              getClassSignature(input) + " {",
                              Iterables.concat(
                                                  emptyLine(),
                                                  getFields(input),
                                                  getMethods(input)),
                              "}"
        );
    }    
    
    protected abstract Iterable<String> getPackageDeclaration(Input input);

    protected Iterable<String> getPackageDeclaration(String pkg){
        return Collections.singleton("package " + pkg + ";");
    }
    
    protected abstract Iterable<String> getImports(Input input);
    
    protected Iterable<String> getImports(Iterable<Class<?>> importedClasses) {
        return Iterables.transform(importedClasses, importClass());
    }    
    
    
    protected Function<Class<?>, String> importClass() {
        return new Function<Class<?>, String>() {
            @Override
            public String apply(Class<?> input) {
                return "import " + input.getName() + ";";
            }
        };
    }

    protected Function<String, String> importClassByName() {
        return new Function<String, String>() {
            @Override
            public String apply(String input) {
                return "import " + input + ";";
            }
        };
    }


    protected abstract String getClassSignature(Input input);
    
    protected String getClassSignature(String simpleName, Optional<Type> superType, Iterable<Type> implementedInterfaces){

        StringBuilder builder = new StringBuilder("public class ");

        builder.append(simpleName);
        if (superType.isPresent()) {
            builder.append(" extends " + superType.get().getSimpleName());
        }
        boolean first = true;
        for (Type impl : implementedInterfaces) {
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

    protected abstract Iterable<String> getFields(Input input);

    protected abstract Iterable<String> getMethods(Input input);
}
