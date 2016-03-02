package example.generatePojo;

import example.generatePojo.extract.Extractors;
import example.generatePojo.spi.Generators;
import example.generatePojo.spi.impl.GetterPlugin;
import example.generatePojo.spi.impl.PojoImplGenerationRun;
import example.generatePojo.spi.json.JSonGenerationRun;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;
import java.util.stream.IntStream;

import static example.generatePojo.dependency.ClassSource.*;
import static example.generatePojo.extract.Extractors.*;
import static example.generatePojo.spi.Generators.*;

public class Generator {

    public static void _main(String[] args) throws Exception {
        Instant start = Instant.now();

        new Random().ints().sorted().findAny().ifPresent(i -> System.out.println(i));

        System.out.println(Duration.between(start, Instant.now()).toString());
    }

    public static void main(String[] args) throws Exception {
        inServices(        GenerationContext.newBuilder()
            .configure(maven().at("/home/talend/.m2/repository"))
//            .addClassesFrom(artefact("be.voo.esb.services.mobilelifecycle", "mobilelifecycle-api", "3.2.0-SNAPSHOT"))
//            .addClassesFrom(artefact("be.voo.esb.services.mobileactivation", "mobileactivation-api", "1.0.1"))
//            .addClassesFrom(artefact("be.voo.esb.voodatamodel-api", "channel-effortel-api", "3.1.0"))
//            .addSources(
//                "be.voo.services.mobilelifecycle.api.event.SubscriptionProductChanged"
//                        )
            .extractAs(Extractors.getterInterface())
        );
    }

    public static void inServices(GenerationContext.GenerationContextBuilder builder) throws Exception {
        builder
            .atRoot("/home/talend/voo-services/")
                .inModule("external")
                .inModule("jira")
                .inModule("jira-api")
                    .addClasses()
                    .addSourcesIn("src/main/java/", "be.voo.esb.external.jira.api.domain")
//                    .addSources("be.voo.esb.external.jira.bmx.api.domain.BmxIssueCreation")
//                    .addSources("be.voo.esb.external.jira.bmx.api.domain.BmxIssueIdentifier")
            .back()
            .inModule("jira-core")
//                      .generate(Generators.pojoImpl())
//                      .withPlugins(new GetterPlugin())
//                      .inPackage("be.voo.esb.services.bmx.jackson.event")
//                      .namedAs("Jackson{0}")

//                .inModule("mobileorderprocess-commandhandlers")
//                .inModule("mobileorderprocess-cancelcrecoclearance/mobileorderprocess-cancelcrecoclearance-core")
//                    .generate(Generators.pojoImpl())
//                            .withPlugins(new GetterPlugin())
//                            .inPackage("be.voo.esb.external.jira.core.domain")
//                            .namedAs("Jackson{0}")
//                        .writeGeneratedContent(true)
//                        .printGeneratedContent()
//                    .endGenerate()
                    .generate(Generators.jsonExample())
                        .writeGeneratedContent(true)
                        .printGeneratedContent()
                        .resourceType("json")
                    .endGenerate()
                    .generate(Generators.template())
                        .inDirectory("src/test/java/be/voo/esb/external/jira/core/comain")
                        .namedAs("Jackson{0}Test.java")
                        .withTemplate(Generator.class.getResource("/MessageTest.ftl"))
                        .with("package", "be.voo.esb.external.jira.core.domain")
                    .writeGeneratedContent(false)
                    .printGeneratedContent()
                    .resourceType("object")
                .endGenerate()
            .build()
            .generate();
    }

    public static void inEsb(GenerationContext.GenerationContextBuilder builder) throws Exception {
        builder
            .atRoot("/home/talend/voo-esb/")
                .inModule("voodatamodel-services")
                    .inModule("chan")
                        .inModule("effortel")
                          .addClasses()
//                        .generate(PojoImplGenerationRun.<GenerationContext.GenerationContextBuilder>pojoImpl())
//                            .withPlugins(new GetterPlugin())
//                            .inPackage("be.voo.esb.services.chan.dol.mobileactivation.command")
//                            .namedAs("Jackson{0}")
//                            .writeGeneratedContent(true)
//                            .printGeneratedContent()
//                            .resourceType("command")
//                        .endGenerate()
                            .generate(Generators.<GenerationContext.GenerationContextBuilder>jsonExample())
                            .writeGeneratedContent(true)
                            .printGeneratedContent()
                            .resourceType("json")
                        .endGenerate()
            .build()
            .generate();

    }



}