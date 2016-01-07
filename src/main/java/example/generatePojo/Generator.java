package example.generatePojo;

import example.generatePojo.extract.Extractors;
import example.generatePojo.spi.Generators;
import example.generatePojo.spi.impl.GetterPlugin;
import example.generatePojo.spi.impl.PojoImplGenerationRun;
import example.generatePojo.spi.json.JSonGenerationRun;

import static example.generatePojo.dependency.ClassSource.*;
import static example.generatePojo.extract.Extractors.*;
import static example.generatePojo.spi.Generators.*;

public class Generator {


    public static void main(String[] args) throws Exception {

        GenerationContext.newBuilder()
            .configure(maven().at("/home/talend/.m2/repository"))
            .addClassesFrom(artefact("be.voo.esb.services.mobilelifecycle", "mobilelifecycle-api", "3.2.0-SNAPSHOT"))
            .addClassesFrom(artefact("be.voo.esb.services.mobileactivation", "mobileactivation-api", "1.0.1"))
            .addClassesFrom(artefact("be.voo.esb.voodatamodel-api", "channel-effortel-api", "3.1.0"))
            .addSources(
                "be.voo.services.mobilelifecycle.api.event.SubscriptionProductChanged"
                        )
            .extractAs(Extractors.getterInterface())

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