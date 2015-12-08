package example.generatePojo;

import example.generatePojo.spi.impl.PojoImplGenerationRun;
import example.generatePojo.spi.json.JSonGenerationRun;

import static example.generatePojo.dependency.ClassSource.*;

public class Generator {


    public static void main(String[] args) throws Exception {

        GenerationContext.newBuilder()
            .configure(maven().at("/home/talend/.m2/repository"))
            .addClassesFrom(artefact("be.voo.esb.services.mobilelifecycle", "mobilelifecycle-api", "1.0.0-SNAPSHOT"))
//            .addSources("be.voo.esb.external.fast.managesalesinventory.api.domain.NotifySubscriptionUpdated")

            .atRoot("/home/talend/voo-services/salescommissioning/")
                .inModule("salescommissioning-api")
                    .addClasses()
                    .addSources("be.voo.esb.services.salescommissioning.api.event.StartMobileCommissioningProcessingFailed")
                .back()
                .inModule("salescommissioning-commandhandlers")
                    .inModule("salescommissioning-startmobilecommissioning")
                        .inModule("salescommissioning-startmobilecommissioning-core")
                        .addClasses()
                        .generate(PojoImplGenerationRun.<GenerationContext.GenerationContextBuilder> pojoImpl() )
                            .writeGeneratedContent(true)
                            .printGeneratedContent()
                            .resourceType("event")
                            .endGenerate()
                        .generate(JSonGenerationRun.<GenerationContext.GenerationContextBuilder> jsonExample())
                            .writeGeneratedContent(false)
                            .printGeneratedContent()
                            .resourceType("event")
                            .endGenerate()
            .build()
            .generate();



    }

}