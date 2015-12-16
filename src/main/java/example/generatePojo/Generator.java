package example.generatePojo;

import example.generatePojo.spi.impl.GetterPlugin;
import example.generatePojo.spi.impl.PojoImplGenerationRun;
import example.generatePojo.spi.json.JSonGenerationRun;

import static example.generatePojo.dependency.ClassSource.*;

public class Generator {


    public static void main(String[] args) throws Exception {

        GenerationContext.newBuilder()
            .configure(maven().at("/home/talend/.m2/repository"))
            .addClassesFrom(artefact("be.voo.esb.services.mobilelifecycle", "mobilelifecycle-api", "3.0.0"))
            .addSources("be.voo.services.mobilelifecycle.api.event.SubscriptionMsisdnChanged")

            .atRoot("/home/talend/voo-esb/")
                .inModule("voodatamodel-api")
                    .inModule("chan")
                        .inModule("effortel")
                            .addClasses()
//                            .addSources("be.voo.csp.esb1.channel.mobile.effortel.mobileorderhandling_v1.NotifyMSISDNChangeCompletedRequest")
//                            .extractAs(new BeanGetterExtractor())
                        .back()
                    .back()
                .back()
                .inModule("voodatamodel-services")
                    .inModule("chan")
                        .inModule("effortel")
                        .addClasses()
                        .generate(PojoImplGenerationRun.<GenerationContext.GenerationContextBuilder>pojoImpl())
                            .withPlugins(new GetterPlugin())
                            .inPackage("be.voo.esb.voochannel.effortel.event")
                            .namedAs("Default{0}")
                            .writeGeneratedContent(false)
                            .printGeneratedContent()
                            .resourceType("event")
                        .endGenerate()
                        .generate(JSonGenerationRun.<GenerationContext.GenerationContextBuilder> jsonExample())
                            .writeGeneratedContent(true)
                            .printGeneratedContent()
                            .resourceType("json")
                        .endGenerate()
            .build()
            .generate();

    }

}