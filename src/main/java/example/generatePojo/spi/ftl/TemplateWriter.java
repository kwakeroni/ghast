package example.generatePojo.spi.ftl;

import example.generatePojo.PojoExtractor;
import example.generatePojo.model.Pojo;
import example.generatePojo.spi.CodeWriterSupport;
import freemarker.cache.TemplateLoader;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Maarten Van Puymbroeck
 */
public class TemplateWriter extends CodeWriterSupport<Pojo> {

    private final String filenamePattern;
    private final URL templateUrl;
    private final Configuration configuration;
    private final String resourceType;
    private final Map<String, Object> parameters;

    public TemplateWriter(Path target,
                          String filenamePattern,
                          URL templateUrl,
                          String resourceType,
                          Map<String, Object> parameters
    ) {
        super(target);
        this.filenamePattern = filenamePattern;
        this.templateUrl = templateUrl;
        this.configuration = getFreemarkerConfig(this.templateUrl);
        this.resourceType = resourceType;
        this.parameters = parameters;
    }

    @Override
    protected Path getTarget(Pojo pojo) {
        return Paths.get(MessageFormat.format(filenamePattern, pojo.getSimpleName()));
    }

    @Override
    protected Iterable<String> toCode(Pojo pojo) {

        Map<String, Object> data = new HashMap<>(2 + parameters.size());
        data.put("pojo", pojo);
        data.put("resourceType", resourceType);
        data.putAll(parameters);

        try {
            Template template = this.configuration.getTemplate(this.templateUrl.getFile());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            template.process(data, new OutputStreamWriter(baos));
            return Collections.singleton(new String(baos.toByteArray()));
        } catch (IOException | TemplateException exc){
            throw new RuntimeException(exc);
        }
    }

    private Configuration getFreemarkerConfig(final URL templateFile) {
        Configuration config = new Configuration();

        try {
            config.setDirectoryForTemplateLoading(new File(templateFile.toURI()).getParentFile());
        } catch (IOException | URISyntaxException exc){
            throw new IllegalArgumentException(exc);
        }


        config.setObjectWrapper(new BeansWrapper());
        config.setDefaultEncoding("UTF-8");
        config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        config.setIncompatibleImprovements(new Version(2, 3, 20));

        config.setTemplateLoader(new TemplateLoader() {
            @Override
            public Object findTemplateSource(String name) throws IOException {
                return templateFile;
            }

            @Override
            public long getLastModified(Object templateSource) {
                try {
                    return new File(templateFile.toURI()).lastModified();
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public Reader getReader(Object templateSource, String encoding) throws IOException {
                return new BufferedReader(new InputStreamReader(templateFile.openStream()));
            }

            @Override
            public void closeTemplateSource(Object templateSource) throws IOException {

            }
        });

        return config;
    }
}
