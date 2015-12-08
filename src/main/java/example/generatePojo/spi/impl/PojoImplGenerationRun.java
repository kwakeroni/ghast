package example.generatePojo.spi.impl;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import example.generatePojo.GenerationRun;
import example.generatePojo.GetterInterfacePojoExtractor;
import example.generatePojo.PojoExtractor;
import example.generatePojo.Util;
import example.generatePojo.model.AbstractPojo;
import example.generatePojo.model.Pojo;
import example.generatePojo.model.Property;
import example.generatePojo.model.Type;
import javafx.scene.ParentBuilder;

/**
 * @author Maarten Van Puymbroeck
 */
public class PojoImplGenerationRun extends GenerationRun {

        public final String TARGET_PACKAGE = "be.voo.esb.services.salescommissioning.startmobilecommissioning.core." + resourceType;

    private Plugin[] plugins = new Plugin[0];

    public PojoImplGenerationRun(GenerationRun.Builder<?, ?> builder) {
        super(builder);
    }

    protected void generate(Pojo startPojo, PojoExtractor<Class<?>> extractor) throws Exception {

        Iterable<Pojo> pojoSrcs = Util.distinct(Util.recurse(startPojo, dependentPojos(extractor)), isEqualPojo());


        Iterable<Pojo> pojoImpls = Iterables.transform(
                                                          pojoSrcs,
                                                          toPojoImpl(TARGET_PACKAGE, NAMING_STRATEGY));


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
                        return getPackage() + "." + getSimpleName();
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

        private Builder() {
            super();
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
