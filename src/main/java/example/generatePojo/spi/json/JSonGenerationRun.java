package example.generatePojo.spi.json;

import com.google.common.base.Supplier;
import example.generatePojo.GenerationRun;
import example.generatePojo.PojoExtractor;
import example.generatePojo.model.Pojo;

/**
 * @author Maarten Van Puymbroeck
 */
public class JSonGenerationRun extends GenerationRun {

    public JSonGenerationRun(GenerationRun.Builder<?, ?> builder) {
        super(builder);
    }

    protected void generate(Pojo startPojo, PojoExtractor<Class<?>> extractor) throws Exception {
        JSonExampleWriter writer = new JSonExampleWriter(targetModule.resolve("src/test/resources/" + resourceType + "/"), extractor);

        if (printContents) writer.printOut(startPojo);
        if (writeContents) writer.write(startPojo);
    }

    public static final class Builder<ParentBuilder> extends GenerationRun.Builder<Builder<ParentBuilder>, ParentBuilder> {

        private Builder() {
            super();
        }


        protected JSonGenerationRun build() {
            return new JSonGenerationRun(this);
        }
    }

    private static final Supplier<Builder<Object>> BUILDER_SUPPLIER = new Supplier<Builder<Object>>() {
        @Override
        public Builder<Object> get() {
            return new Builder<>();
        }
    };

    public static final <ParentType> Supplier<Builder<ParentType>> jsonExample(){
        return (Supplier<Builder<ParentType>>) (Supplier<?>) BUILDER_SUPPLIER;
    }

}
