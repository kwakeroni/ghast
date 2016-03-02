package example.generatePojo;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import example.generatePojo.dependency.ClassSource;
import example.generatePojo.extract.GetterInterfacePojoExtractor;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static example.generatePojo.dependency.ClassSource.*;

/**
 * @author Maarten Van Puymbroeck
 */
public class GenerationContext {

    private final ClassSource classes;
    private final List<String> sourceClasses;
    private final List<GenerationRun> generations = new ArrayList<>();
    private final PojoExtractor<Class<?>> extractor;

    protected GenerationContext(Builder<?, ?> builder) {
        classes = builder.classes.build();
        this.sourceClasses = builder.sourceClasses;
        for (GenerationRun.Builder<? extends GenerationRun.Builder<?,?>, ?> generator : builder.generators) {
            this.generations.add(generator.context(this).build());
        }
        extractor = (builder.extractor == null) ? new GetterInterfacePojoExtractor() : builder.extractor;
    }

    public PojoExtractor<Class<?>>  getExtractor(){
        return extractor;
    }

    public Class<?> classForName(String name) {
        return this.classes.forName(name);
    }

    public Iterable<Class<?>> getSources(){
        return Iterables.transform(this.sourceClasses, classForName());
    }

    public void generate() throws Exception {
        for (GenerationRun generation : this.generations){
            generation.generate();
        }
    }


    public static Builder<GenerationContextBuilder, GenerationContext> newBuilder() {
        return new GenerationContextBuilder();
    }

    public static class GenerationContextBuilder extends Builder<GenerationContextBuilder, GenerationContext> {
        @Override
        public GenerationContext build() {
            return new GenerationContext(this);
        }
    }

    public static abstract class Builder<BuilderType, Result extends GenerationContext> {
        private ClassSource.Builder classes = ClassSource.newBuilder(Generator.class.getClassLoader());
        private final List<String> sourceClasses = new ArrayList<>();
        private PojoExtractor<Class<?>> extractor;

        private Path rootModule;
        private Path currentModule;
        private List<GenerationRun.Builder<? extends GenerationRun.Builder<?,?>, GenerationContext.Builder<BuilderType, Result>>> generators = new java.util.ArrayList<>();

        private final BuilderType result;

        protected Builder() {
            this.result = (BuilderType) this;
        }

        protected Builder(BuilderType result) {
            this.result = result;
        }

        public BuilderType configure(Object configuration) {
            this.classes.configure(configuration);
            return this.result;
        }

        public BuilderType atRoot(String rootModule) {
            return atRoot(Paths.get(rootModule));
        }

        public BuilderType atRoot(Path rootModule) {
            this.rootModule = rootModule;
            return this.result;
        }

        public BuilderType inModule(String module) {
            return inModule(Paths.get(module));
        }

        public BuilderType inModule(Path module) {
            if (this.currentModule != null) {
                this.currentModule = this.currentModule.resolve(module);
            } else {
                this.currentModule = module;
            }

            if (!Files.exists(getCurrentModuleTarget())){
                throw new IllegalArgumentException("Non-existing module: " + this.currentModule);
            }

            return this.result;
        }

        public BuilderType back(){
            this.currentModule = this.currentModule.getParent();
            return this.result;
        }

        public BuilderType addClasses() {
            return addClassesFrom(mavenModule(getCurrentModuleTarget()));
        }

        public BuilderType addClassesFrom(URL url) {
            classes.add(url);
            return this.result;
        }

        public BuilderType addClassesFrom(ClassSource.ConfiguredURLSupplier urlSupplier) {
            classes.add(urlSupplier);
            return this.result;
        }

        public BuilderType addClassesFrom(Supplier<? extends URL> urlSupplier) {
            classes.add(urlSupplier);
            return this.result;
        }

        public BuilderType addSources(String... classNames){
            this.sourceClasses.addAll(Arrays.asList(classNames));
            return this.result;
        }

        public BuilderType addSourcesIn(String sourcePath, String pkg){
            try {
                List<String> classes = new ArrayList<>();
                for (Path p : Files.newDirectoryStream( getCurrentModuleTarget().resolve(sourcePath).resolve(pkg.replace('.', '/')) )){
                    String fileName = p.getFileName().toString();
                    if (fileName.endsWith(".java")){
                        classes.add(pkg+"."+fileName.substring(0, fileName.length()-5));
                    }
                }
                return addSources(classes.toArray(new String[classes.size()]));
            } catch (IOException exc){
                throw new RuntimeException(exc);
            }
        }

        public BuilderType extractAs(PojoExtractor<Class<?>> extractor){
            this.extractor = extractor;
            return this.result;
        }

        public <RunBuilderType extends GenerationRun.Builder<?,?>> RunBuilderType generate(Supplier<? extends GenerationRun.Builder<RunBuilderType, BuilderType>> generatorSupplier) {
            GenerationRun.Builder<RunBuilderType, GenerationContext.Builder<BuilderType, Result>>
                generator = generatorSupplier.get()
                                .target(getCurrentModuleTarget())
                                .parent(this);
            this.generators.add(generator);
            return (RunBuilderType) generator;
        }

        private Path getCurrentModuleTarget() {
            if (this.currentModule == null) {
                return this.rootModule;
            } else {
                return this.rootModule.resolve(this.currentModule);
            }
        }

        public abstract Result build();

    }

            private Function<String, Class<?>> classForName(){
            return new Function<String, Class<?>>() {
                @Override
                public Class<?> apply(String input) {
                    return classForName(input);
                }
            };
        }
}
