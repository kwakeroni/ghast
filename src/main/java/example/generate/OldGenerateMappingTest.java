package example.generate;

import example.generate.engine.Generator;
import example.generate.engine.GeneratorContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;

/**
 * @author Maarten Van Puymbroeck
 */
public class OldGenerateMappingTest {

    private URL template = OldGenerateMappingTest.class.getResource("/MappingTest.java.template");
    private File result = new File("voodatamodel-services/conn/sap4/src/test/java/NotifyMandateMappingTest.java");
    private String sourceClassNameSimple = "SepaDirectDebitMandate";
    private String sourceClassName = "be.voo.esb.api.ws.managecustomerpayments." + sourceClassNameSimple;
    private String targetClassNameSimple = "ZISUSDD";
    private String targetClassName = "be.voo.esb.model.sap4.managecustomerpayments." + targetClassNameSimple;

    public static void main(String[] args) throws IOException {
        new OldGenerateMappingTest().generate();
    }

    public void generate() throws IOException {
        System.out.println("Generating to " + result.getAbsolutePath());

        InputStream in = null;
        OutputStream out = null;

        try {
            in = template.openStream();
            out = new FileOutputStream(result);

            Generator.run(new BufferedReader(new InputStreamReader(in)), new PrintWriter(out), context());

        } finally {
            if (in != null){
                try {
                    in.close();
                } catch (Exception exc){
                    exc.printStackTrace();
                }
            }
            if (out != null){
                try {
                    out.close();
                } catch (Exception exc){
                    exc.printStackTrace();
                }
            }
        }

    }







        GeneratorContext<Parameter> context(){
            GeneratorContext<Parameter> context = new GeneratorContext<Parameter>(Parameter.class);
            for (Parameter parameter : Parameter.values()){
                context.set(parameter, parameter.getInitialValue(this));
            }
            return context;
        }

    public static enum Parameter {
        SOURCE_CLASS_NAME{
            public String getInitialValue(OldGenerateMappingTest test){
                return test.sourceClassName;
            }
        },
        SOURCE_CLASS_NAME_SIMPLE{
            public String getInitialValue(OldGenerateMappingTest test){
                return test.sourceClassNameSimple;
            }
        },
        TARGET_CLASS_NAME{
            public String getInitialValue(OldGenerateMappingTest test){
                return test.targetClassName;
            }
        },
        TARGET_CLASS_NAME_SIMPLE{
            public String getInitialValue(OldGenerateMappingTest test){
                return test.targetClassNameSimple;
            }
        }
        ;

        public abstract String getInitialValue(OldGenerateMappingTest test);
    }

}
