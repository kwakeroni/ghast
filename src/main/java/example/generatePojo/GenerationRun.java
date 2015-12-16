package example.generatePojo;

import com.google.common.base.Supplier;
import example.generatePojo.dependency.ClassSource;
import example.generatePojo.model.Pojo;
import com.google.common.base.Function;
import com.google.common.base.Predicate;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static example.generatePojo.dependency.ClassSource.*;

/**
 * @author Maarten Van Puymbroeck
 */
public abstract class GenerationRun {

    protected final Path targetModule;
    protected final boolean printContents;
    protected final boolean writeContents;
    protected final String resourceType;
    protected final GenerationContext context;
    protected final Function<String, String> namingStrategy;

    protected GenerationRun(Builder<?, ?> builder){
        targetModule = builder.targetModule;
        printContents = builder.printContents;
        writeContents = builder.writeContents;
        resourceType = builder.resourceType;
        context = builder.context;
        namingStrategy = builder.namingStrategy;
    }
    

    public void generate() throws Exception {
        generate(this.context.getExtractor(), this.context.getSources());
    }

    private void generate(PojoExtractor<Class<?>> extractor, Iterable<Class<?>> classes) throws Exception {
        for (Class<?> clazz : classes) {
            generate(extractor, clazz);
        }
    }

    private void generate(PojoExtractor<Class<?>> extractor, Class<?> start) throws Exception {

        System.out.println(start);

        Pojo startPojo = extractor.getPojo(start);

        generate(startPojo, extractor);

        System.out.println("--");
    }
    
    protected abstract void generate(Pojo pojo, PojoExtractor<Class<?>> extractor) throws Exception;    

//    private void generateFailureMapper(Pojo startPojo, PojoExtractor<Class<?>> extractor) throws Exception {
//
//        FailureMapperWriter writer = new FailureMapperWriter(Paths.get(PROJECT_DIR, "/src/main/java"));
//
//        Pojo pojo = toPojoImpl(TARGET_PACKAGE, Util.messageFormat("{0}")).apply(startPojo);
//
//        if (PRINT_CONTENTS) writer.printOut(pojo);
//        if (WRITE_CONTENTS) writer.write(pojo);
//        System.out.println("--");
//    }



    protected static void printlns(Iterable<?> coll) {
        System.out.println("[");
        for (Object o : coll) {
            System.out.println(o);
        }
        System.out.println("]");
    }

    public static Predicate<Class<?>> inPackage(final String pkg) {
        return new Predicate<Class<?>>() {
            @Override
            public boolean apply(Class<?> input) {
                return input.getPackage().getName().equals(pkg);
            }
        };
    }


    public static abstract class Builder<BuilderType, ParentBuilder> {
        private Path targetModule;
        private ParentBuilder parent;
        private GenerationContext context;

        private boolean printContents = false;
        private boolean writeContents = false;
        private String resourceType;
        private Function<String, String> namingStrategy;

        protected final BuilderType result;

        protected Builder(){
            this.result = (BuilderType) this;
        }

        protected Builder(BuilderType result) {
            this.result = result;
        }

        public BuilderType printGeneratedContent() {
            return printGeneratedContent(true);
        }
        public BuilderType printGeneratedContent(boolean doPrint) {
            this.printContents = doPrint;
            return this.result;
        }

        public BuilderType writeGeneratedContent() {
            return writeGeneratedContent(true);
        }
        public BuilderType writeGeneratedContent(boolean doWrite) {
            this.writeContents = doWrite;
            return this.result;
        }

        Builder<BuilderType, ParentBuilder> target(Path targetModule){
            this.targetModule = targetModule;
            return this;
        }

        <NewParentBuilder> Builder<BuilderType, NewParentBuilder> parent(NewParentBuilder parent){
            Builder<BuilderType, NewParentBuilder> myself = (Builder<BuilderType, NewParentBuilder>) this;
            myself.parent = parent;
            return myself;
        }

        public BuilderType resourceType(String resourceType) {
            this.resourceType = resourceType;
            return this.result;
        }

        public BuilderType namedAs(Function<String, String> namingStrategy){
            this.namingStrategy = namingStrategy;
            return this.result;
        }

        public BuilderType namedAs(String namingFormat){
            return namedAs(Util.messageFormat(namingFormat));
        }

        public ParentBuilder endGenerate(){
            return this.parent;
        }

        BuilderType context(GenerationContext context){
            this.context = context;
            return this.result;
        }

        protected abstract GenerationRun build();

    }

}
