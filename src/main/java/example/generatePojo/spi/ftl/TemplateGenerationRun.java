package example.generatePojo.spi.ftl;

import com.google.common.base.Supplier;
import example.generatePojo.GenerationRun;
import example.generatePojo.PojoExtractor;
import example.generatePojo.model.Pojo;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Maarten Van Puymbroeck
 */
public class TemplateGenerationRun extends GenerationRun {

    private final String targetDirectory;
    private final String filenamePattern;
    private final URL templateUrl;
    private final Map<String, Object> parameters;

    public TemplateGenerationRun(Builder<?> builder) {
        super(builder);
        this.targetDirectory = builder.targetDirectory;
        this.filenamePattern = builder.filenamePattern;
        this.templateUrl = builder.templateUrl;
        this.parameters = builder.parameters;
    }

    protected void generate(Pojo pojo, PojoExtractor<Class<?>> extractor) throws Exception {

        Objects.requireNonNull(this.targetDirectory, "target package");

        TemplateWriter writer = new TemplateWriter(
            targetModule.resolve(targetDirectory),
            filenamePattern,
            templateUrl,
            resourceType,
            parameters
            );

        try {
            if (printContents) writer.printOut(pojo);
            if (writeContents) writer.write(pojo);
        } catch (Exception exc){
            exc.printStackTrace();
        }


    }


    public static final class Builder<ParentBuilder> extends GenerationRun.Builder<Builder<ParentBuilder>, ParentBuilder> {

        private String targetDirectory;
        private String filenamePattern;
        private URL templateUrl;
        private Map<String, Object> parameters = new HashMap<>(4);

        private Builder() {
            super();
        }

        public Builder<ParentBuilder> inDirectory(String dir) {
            this.targetDirectory = dir;
            return this.result;
        }

        public Builder<ParentBuilder> namedAs(String pattern){
            this.filenamePattern = pattern;
            return this.result;
        }

        public Builder<ParentBuilder> withTemplate(URL templateUrl){
            this.templateUrl = templateUrl;
            return this.result;
        }

        public Builder<ParentBuilder> with(String param, Object value){
            this.parameters.put(param, value);
            return this.result;
        }

        @Override
        protected GenerationRun build() {
            return new TemplateGenerationRun(this);
        }
    }

    private static final Supplier<Builder<Object>> BUILDER_SUPPLIER = new Supplier<Builder<Object>>() {
        @Override
        public Builder<Object> get() {
            return new Builder<>();
        }
    };

    public static final <ParentType> Supplier<Builder<ParentType>> pojoTemplate() {
        return (Supplier<Builder<ParentType>>) (Supplier<?>) BUILDER_SUPPLIER;
    }
}
