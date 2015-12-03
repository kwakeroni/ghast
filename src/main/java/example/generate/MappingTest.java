package example.generate;

import java.io.File;
import java.net.URL;

/**
 * @author Maarten Van Puymbroeck
 */
public enum MappingTest {
    NOTIFY_MANDATE {
        @Override
        protected void init() {
            this.result = new File("voodatamodel-services/conn/sap4/src/test/java/NotifyMandateMappingTest.java");
            this.xlsMapping = new File("/home/talend/transfer/temp/Mapping iDoc-canonical-bpost v9.xlsx");
            this.testClass = new MappingClass("", "NotifyMandateMappingTest");
            this.mappingClass = new MappingClass("be.voo.esb.services.conn.sap4.idoc.command", "NotifyMandateCommand");
            this.sourceClass = new MappingClass("be.voo.esb.api.ws.managecustomerpayments", "SepaDirectDebitMandate");
            this.targetClass = new MappingClass("be.voo.esb.model.sap4.managecustomerpayments", "ZISUSDD");
        }

        @Override
        public MappingExcel.MappingSheet getXlsMappingSheet() {
            return new MappingExcel.MappingSheet("Mapping (initiate response)", getSourceClass().getPackage(), getTargetClass().getPackage(), 1, 2, 1, 1, 0);
        }
    },
    NOTIFY_MANDATE_ERROR {
        @Override
        protected void init() {
            this.result = new File("voodatamodel-services/conn/sap4/src/test/java/NotifyMandateErrorMappingTest.java");
            this.xlsMapping = new File("/home/talend/transfer/temp/Mapping iDoc-canonical-bpost v13.xlsx");
            this.testClass = new MappingClass("be.voo.esb.services.conn.sap4.idoc.command", "NotifyMandateErrorMappingTest");
            this.mappingClass = new MappingClass("be.voo.esb.services.conn.sap4.idoc.command", "NotifyMandateErrorCommand");
            this.sourceClass = new MappingClass("be.voo.esb.api.ws.managecustomerpayments", "SepaDirectDebitMandate");
            this.targetClass = new MappingClass("be.voo.esb.model.sap4.managecustomerpayments", "ZISUSDD");
        }

        @Override
        public MappingExcel.MappingSheet getXlsMappingSheet() {
            return new MappingExcel.MappingSheet("Mapping (errors)", getSourceClass().getPackage(), getTargetClass().getPackage(), 1, 2, 1, 1, 0);
        }
    },
    INITIATE_MANDATE_RESPONSE_BPOST {
        @Override
        protected void init() {
            this.result = new File("voodatamodel-services/chan/bpost/mandateResponseHandling/src/test/java/InitiateResponseMappingTest.java");
            this.xlsMapping = new File("/home/talend/transfer/temp/Mapping iDoc-canonical-bpost v17.xlsx");
            this.testClass = new MappingClass("be.voo.esb.services.chan.bpost.mandateResponseHandling", "InitiateResponseMappingTest");
            this.mappingClass = new MappingClass("be.voo.esb.services.chan.bpost.mandateResponseHandling", "Converter");
            this.sourceClass = new MappingClass("", "");
            this.targetClass = new MappingClass("be.voo.esb.api.ws.managecustomerpayments", "SepaDirectDebitMandateRequest");
        }

        @Override
        public MappingExcel.MappingSheet getXlsMappingSheet() {
            return new MappingExcel.MappingSheet("Mapping (initiate request)", getSourceClass().getPackage(), getTargetClass().getPackage(), 0, 1, 1, 0, 1);
        }
    },
    INITIATE_MANDATE_REQUEST {
        @Override
        protected void init() {
            this.result = new File("voodatamodel-services/chan/sap4chan/src/test/java/SEPACreateMandateMappingTest.java");
            this.xlsMapping = new File("/home/talend/transfer/temp/Mapping iDoc-canonical-bpost v9.xlsx");
            this.testClass = new MappingClass("be.voo.esb.voochannel.sap4.idoc.command", "SEPACreateMandateMappingTest");
            this.mappingClass = new MappingClass("be.voo.esb.voochannel.sap4.idoc.command", "SEPACreateMandateCommand");
            this.sourceClass = new MappingClass("be.voo.esb.model.sap4.managecustomerpayments", "ZISUSDD");
            this.targetClass = new MappingClass("be.voo.esb.api.ws.managecustomerpayments", "SepaDirectDebitMandateRequest");
        }

        @Override
        public MappingExcel.MappingSheet getXlsMappingSheet() {
            return new MappingExcel.MappingSheet("Mapping (initiate request)", getSourceClass().getPackage(), getTargetClass().getPackage(), 0, 1, 1, 0, 1);
        }
    },
    CANCEL_MANDATE_REQUEST {
        @Override
        protected void init() {
            this.result = new File("voodatamodel-services/chan/sap4chan/src/test/java/SEPACancelMandateMappingTest.java");
            this.xlsMapping = new File("/home/talend/transfer/temp/Mapping iDoc-canonical-bpost v9.xlsx");
            this.testClass = new MappingClass("be.voo.esb.voochannel.sap4.idoc.command", "SEPACancelMandateMappingTest");
            this.mappingClass = new MappingClass("be.voo.esb.voochannel.sap4.idoc.command", "SEPACancelMandateCommand");
            this.sourceClass = new MappingClass("be.voo.esb.model.sap4.managecustomerpayments", "ZISUSDD");
            this.targetClass = new MappingClass("be.voo.esb.api.ws.managecustomerpayments", "SepaDirectDebitMandateRequest");
        }

        @Override
        public MappingExcel.MappingSheet getXlsMappingSheet() {
            return new MappingExcel.MappingSheet("Mapping (cancel request)", getSourceClass().getPackage(), getTargetClass().getPackage(), 0, 1, 1, 0, 1);
        }
    },
    REGISTER_DEVICE {
        @Override
        protected void init() {
            this.result = new File("voodatamodel-services/chan/cisco/src/test/java/RegisterDeviceMappingTest.java");
            this.xlsMapping = new File("/home/talend/transfer/temp/UHE_Cisco.xlsx");
            this.testClass = new MappingClass("be.voo.esb.channel.cisco.mapping.device", "DeviceMappingTest");
            this.mappingClass = new MappingClass("be.voo.esb.channel.cisco.command", "RegisterDeviceCommand");
            this.sourceClass = new MappingClass("be.voo.esb.channel.cisco.mapping.device", "RegisterDevice");
            this.targetClass = new MappingClass("be.voo.csp.esb1.schema.common", "LogicalResource");
        }

        @Override
        public MappingExcel.MappingSheet getXlsMappingSheet() {
            return new MappingExcel.MappingSheet("RegisterDevice", getSourceClass().getPackage(), getTargetClass().getPackage(), 0, 1, 4, 0, 1);
        }
    };
    protected static URL template = GenerateMappingTest.class.getResource("/MappingTest.java.template");


    protected File result;
    protected File xlsMapping;

    protected MappingClass testClass;
    protected MappingClass mappingClass;
    protected MappingClass sourceClass;
    protected MappingClass targetClass;

    private MappingTest(){
        init();
    }

    protected abstract void init();


    public URL getTemplate() {
        return template;
    }

    public File getResult() {
        return result;
    }

    public File getXlsMapping() {
        return xlsMapping;
    }

    public abstract MappingExcel.MappingSheet getXlsMappingSheet();

    public MappingClass getSourceClass() {
        return sourceClass;
    }

    public MappingClass getTestClass() {
        return testClass;
    }

    public MappingClass getMappingClass() {
        return mappingClass;
    }

    public MappingClass getTargetClass() {
        return targetClass;
    }

}
