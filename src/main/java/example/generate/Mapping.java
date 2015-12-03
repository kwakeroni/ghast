package example.generate;

import java.io.File;
import java.net.URL;

/**
 * @author Maarten Van Puymbroeck
 */
public enum Mapping {
     NOTIFY_MANDATE_ERROR {
         @Override
         protected void init() {
             this.result = new File("voodatamodel-services/conn/sap4/src/main/java/NotifyMandateErrorCommand.java");
             this.xlsMapping = new File("/home/talend/transfer/temp/Mapping iDoc-canonical-bpost v13.xlsx");
             this.mappingClass = new MappingClass("be.voo.esb.services.conn.sap4.idoc.command", "NotifyMandateErrorCommand");
             this.sourceClass = new MappingClass("be.voo.esb.api.ws.managecustomerpayments", "SepaDirectDebitMandate");
             this.targetClass = new MappingClass("be.voo.esb.model.sap4.managecustomerpayments", "ZISUSDD");
         }

         @Override
         public MappingExcel.MappingSheet getXlsMappingSheet() {
             return new MappingExcel.MappingSheet("Mapping (errors)", getSourceClass().getPackage(), getTargetClass().getPackage(), 1, 2, 1, 1, 0);
         }
     };
    protected static URL template = GenerateMappingTest.class.getResource("/Mapping.java.template");


    protected File result;
    protected File xlsMapping;

    protected MappingClass mappingClass;
    protected MappingClass sourceClass;
    protected MappingClass targetClass;

    private Mapping(){
        init();
    }

    protected abstract void init();

    public abstract MappingExcel.MappingSheet getXlsMappingSheet();


    public URL getTemplate() {
        return template;
    }

    public File getResult() {
        return result;
    }

    public File getXlsMapping() {
        return xlsMapping;
    }

    public MappingClass getSourceClass() {
        return sourceClass;
    }

    public MappingClass getMappingClass() {
        return mappingClass;
    }

    public MappingClass getTargetClass() {
        return targetClass;
    }
}
