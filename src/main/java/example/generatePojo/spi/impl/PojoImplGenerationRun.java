package example.generatePojo.spi.impl;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import example.generatePojo.GenerationRun;
import example.generatePojo.extract.GetterInterfacePojoExtractor;
import example.generatePojo.PojoExtractor;
import example.generatePojo.Util;
import example.generatePojo.model.AbstractPojo;
import example.generatePojo.model.Pojo;
import example.generatePojo.model.Property;
import example.generatePojo.model.Type;

/**
 * @author Maarten Van Puymbroeck
 */
public class PojoImplGenerationRun extends GenerationRun {

    private Plugin[] plugins;
    private String targetPackage;

    public PojoImplGenerationRun(Builder<?> builder) {
        super(builder);
        this.targetPackage = builder.pkg;
        this.plugins = (builder.plugins == null)? new Plugin[0] : builder.plugins;
    }

    protected void generate(Pojo startPojo, PojoExtractor<Class<?>> extractor) throws Exception {

        Iterable<Pojo> pojoSrcs = Util.distinct(Util.recurse(startPojo, dependentPojos(extractor)), isEqualPojo());


        Iterable<Pojo> pojoImpls = Iterables.transform(
                                                          pojoSrcs,
                                                          toPojoImpl(this.targetPackage, this.namingStrategy));


        PojoImplWriter writer = new PojoImplWriter(targetModule.resolve("src/main/java"), plugins);

        for (Pojo pojo : pojoImpls) {
            try {
                if (printContents) writer.printOut(pojo);
                if (writeContents) writer.write(pojo);
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }


    }

    private static Util.BiPredicate<Pojo> isEqualPojo() {
        return new Util.BiPredicate<Pojo>() {
            @Override
            public boolean evaluate(Pojo t1, Pojo t2) {
                return t1.getName().equals(t2.getName());
            }
        };
    }

    private static Function<Pojo, Pojo> toPojoImpl(final String pkg, final Function<String, String> nameMapper) {
        return new Function<Pojo, Pojo>() {
            @Override
            public Pojo apply(final Pojo input) {
                return new AbstractPojo() {
                    @Override
                    public Iterable<Property> getProperties() {
                        return input.getProperties();
                    }

                    @Override
                    public String getPackage() {
                        return pkg;
                    }

                    @Override
                    public String getSimpleName() {
                        return nameMapper.apply(input.getSimpleName());
                    }

                    @Override
                    public String getName() {
                        return (getPackage() == null)? getSimpleName() : getPackage() + "." + getSimpleName();
                    }

                    @Override
                    public Type getExtends() {
                        return input.getExtends();
                    }

                    @Override
                    public Iterable<Type> getImplements() {
                        return input.getImplements();
                    }

                    @Override
                    public String toString() {
                        return generateToString();
                    }

                };
            }
        };
    }

    private static Function<Class<?>, Pojo> toPojo(final PojoExtractor<Class<?>> extractor) {
        return new Function<Class<?>, Pojo>() {
            @Override
            public Pojo apply(Class<?> input) {
                return extractor.getPojo(input);
            }
        };
    }

    private static Function<Pojo, Iterable<Pojo>> dependentPojos(final PojoExtractor<Class<?>> extractor) {
        return new Function<Pojo, Iterable<Pojo>>() {
            @Override
            public Iterable<Pojo> apply(Pojo input) {
                return Iterables.transform(dependentPojoClasses(input), toPojo(extractor));
            }
        };
    }


    private static Iterable<Class<?>> dependentPojoClasses(Pojo pojo) {
        return Iterables.filter(dependentClasses(pojo), Predicates.and(GetterInterfacePojoExtractor.isPojoInterface(), inPackage(pojo.getPackage())));
    }

    private static Iterable<Class<?>> dependentClasses(Pojo pojo) {
        return Util.distinct(Iterables.concat(
                                                 Iterables.transform(
                                                                        pojo.getProperties(), Functions.compose(Type.$getTypeDeclarationDependencies, Property.$getType)
                                                 )));
    }

    private static Function<Pojo, Iterable<Class<?>>> dependentClasses() {
        return new Function<Pojo, Iterable<Class<?>>>() {
            @Override
            public Iterable<Class<?>> apply(Pojo input) {
                return dependentClasses(input);
            }
        };
    }

    public static final class Builder<ParentBuilder> extends GenerationRun.Builder<Builder<ParentBuilder>, ParentBuilder> {

        String pkg;
        Plugin[] plugins;

        private Builder() {
            super();
        }

        public Builder<ParentBuilder> inPackage(String pkg){
            this.pkg = pkg;
            return this.result;
        }

        public Builder<ParentBuilder> withPlugins(Plugin... plugins){
            this.plugins = plugins;
            return this.result;
        }

        @Override
        protected GenerationRun build() {
            return new PojoImplGenerationRun(this);
        }
    }

    private static final Supplier<Builder<Object>> BUILDER_SUPPLIER = new Supplier<Builder<Object>>() {
        @Override
        public Builder<Object> get() {
            return new Builder<>();
        }
    };

    public static final <ParentType> Supplier<Builder<ParentType>> pojoImpl(){
        return (Supplier<Builder<ParentType>>) (Supplier<?>) BUILDER_SUPPLIER;
    }
}
