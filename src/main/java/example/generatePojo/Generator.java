package example.generatePojo;

import example.generatePojo.model.AbstractPojo;
import example.generatePojo.model.Pojo;
import example.generatePojo.model.Property;
import example.generatePojo.model.Type;
import example.generatePojo.spi.impl.Plugin;
import example.generatePojo.spi.impl.PojoImplWriter;
import example.generatePojo.spi.json.JSonExampleWriter;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

import java.nio.file.Paths;

/**
 * @author Maarten Van Puymbroeck
 */
public class Generator {


    public static final boolean PRINT_CONTENTS = true;
    public static final boolean WRITE_CONTENTS = true;
    public static final Plugin[] PLUGINS = new Plugin[]{
//                                                           new ExternalizablePlugin(),
    };
    public static final GeneratePojo GENERATE = jsonExample(); // pojoImpl(PLUGINS);
    public static final String RESOURCE_TYPE = "event";
    public static final String PARENT_DIR = "/home/talend/voo-services/mobilecustomermanagement/";
    public static final String PROJECT_DIR = PARENT_DIR + "mobilecustomermanagement-mobilecustomermanagement-eventhandlers/mobilecustomermanagement-customerandorbacreated/mobilecustomermanagement-customerandorbacreated-core";
    public static final String TARGET_PACKAGE = "be.voo.esb.services.mobilecustomermanagement.customerandorbacreated.core.event";
    public static final String[] SOURCE_CLASSES = new String[]{
                    "be.voo.esb.services.mobilecustomermanagement.api.event.CustomerAndBACreationRequested",
                    "be.voo.esb.services.mobilecustomermanagement.api.event.BACreationRequested",
                    "be.voo.esb.services.mobilecustomermanagement.api.event.CustomerAndOrBACreated",
                    "be.voo.esb.services.mobilecustomermanagement.api.event.CustomerAndBACreated",
                    "be.voo.esb.services.mobilecustomermanagement.api.event.BACreated",
                    "be.voo.esb.services.mobilecustomermanagement.api.event.BACreationTimedout",
                    "be.voo.esb.services.mobilecustomermanagement.api.event.CustomerAndOrBACreationCompleted"
    };

    public static final Function<String, String> NAMING_STRATEGY = Util.messageFormat("{0}Event");


    public static void main(String[] args) throws Exception {
        MavenClassSource mavenClass = MavenClassSource.newBuilder(Generator.class.getClassLoader())
                                          .atRepository("/home/talend/.m2/repository")
//                                          .addArtefact("be.voo.esb.services.mobileactivation", "mobileactivation-api", "1.0.0-SNAPSHOT")
//                                          .addArtefact("be.voo.esb.services.mobilelifecycle", "mobilelifecycle-api", "1.0.0-SNAPSHOT")
                                          .addCompilationTarget(Paths.get(PARENT_DIR, "mobilecustomermanagement-api"))
                                          .addCompilationTarget(Paths.get(PROJECT_DIR))
                                          .build();


        PojoExtractor<Class<?>> extractor = new GetterInterfacePojoExtractor();

        generate(GENERATE, mavenClass, extractor, SOURCE_CLASSES);

    }

    private static void generate(GeneratePojo generationType, MavenClassSource mavenClass, PojoExtractor<Class<?>> extractor, String... classes) throws Exception {
        for (String clazz : classes) {
            generate(generationType, extractor, mavenClass.forName(clazz));
        }
    }

    private static void generate(GeneratePojo generationType, PojoExtractor<Class<?>> extractor, Class<?> start) throws Exception {

        System.out.println(start);

        Pojo startPojo = extractor.getPojo(start);

        generationType.generate(startPojo, extractor);

    }


    private static void generateJSonExample(Pojo startPojo, PojoExtractor<Class<?>> extractor) throws Exception {
        JSonExampleWriter writer = new JSonExampleWriter(Paths.get(PROJECT_DIR, "/src/test/resources/" + RESOURCE_TYPE + "/"), extractor);

        if (PRINT_CONTENTS) writer.printOut(startPojo);
        if (WRITE_CONTENTS) writer.write(startPojo);
        System.out.println("--");
    }


    private static void generatePojoImpl(Pojo startPojo, PojoExtractor<Class<?>> extractor, Plugin... plugins) throws Exception {

        Iterable<Pojo> pojoSrcs = Util.distinct(Util.recurse(startPojo, dependentPojos(extractor)), isEqualPojo());


        Iterable<Pojo> pojoImpls = Iterables.transform(
                                                          pojoSrcs,
                                                          toPojoImpl(TARGET_PACKAGE, NAMING_STRATEGY));


        PojoImplWriter writer = new PojoImplWriter(Paths.get(PROJECT_DIR, "/src/main/java"), plugins);

        for (Pojo pojo : pojoImpls) {
            try {
                if (PRINT_CONTENTS) writer.printOut(pojo);
                if (WRITE_CONTENTS) writer.write(pojo);
            } catch (Exception exc) {
                exc.printStackTrace();
            }
            System.out.println("--");
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

    private static void printlns(Iterable<?> coll) {
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


    private static interface GeneratePojo {
        void generate(Pojo pojo, PojoExtractor<Class<?>> extractor) throws Exception;
    }

    private static final GeneratePojo jsonExample() {
        return new GeneratePojo() {
            @Override
            public void generate(Pojo pojo, PojoExtractor<Class<?>> extractor) throws Exception {
                generateJSonExample(pojo, extractor);
            }
        };
    }

    private static final GeneratePojo pojoImpl(final Plugin... plugins){
        return new GeneratePojo() {
            @Override
            public void generate(Pojo pojo, PojoExtractor<Class<?>> extractor) throws Exception {
                generatePojoImpl(pojo, extractor, plugins);
            }
        };
    }

}
