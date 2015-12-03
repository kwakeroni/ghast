package example.generate;

import java.io.FileOutputStream;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Maarten Van Puymbroeck
 */
public class GenerateMappingTest  extends GenerateSupport {

    public static void main(String[] args) throws Exception {
        GenerateMappingTest mapping = new GenerateMappingTest(MappingTest.INITIATE_MANDATE_RESPONSE_BPOST);
        mapping.generate();
    }

    private final MappingTest spec;
    private final MappingExcel mapping;

    private GenerateMappingTest(MappingTest spec) throws Exception {
        this.spec = spec;
        this.mapping = new MappingExcel(spec.getXlsMapping(), spec.getXlsMappingSheet());
    }

    public void generate() throws Exception {
        this.generate(spec.getTemplate(), new FileOutputStream(spec.getResult()));
    }


    public MappingClass getSourceClass() {
        return spec.getSourceClass();
    }

    public MappingClass getTestClass() {
        return spec.getTestClass();
    }

    public MappingClass getMappingClass() {
        return spec.getMappingClass();
    }

    public MappingClass getTargetClass() {
        return spec.getTargetClass();
    }

    public String getRandomString() {
        int a = (int) ((Math.random() * 24d) + 97);
        return new String(new char[]{(char) (a+0), (char) (a+1), (char) (a+2)});
    }

    public Collection<MappingSpec> getMapping(){
        // Freemarker doesn't support Iterable
        return new AbstractCollection<MappingSpec>(){
            @Override
            public Iterator<MappingSpec> iterator() {
                return mapping.iterator();
            }

            @Override
            public int size() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
