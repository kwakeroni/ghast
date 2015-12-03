package example.generate;

import freemarker.cache.TemplateLoader;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author Maarten Van Puymbroeck
 */
public class GenerateSupport {


    public void generate(URL templateURL, OutputStream output) throws Exception {
        Configuration config = getFreemarkerConfig(templateURL);
        Template template = config.getTemplate(templateURL.getFile());
        template.process(this, new OutputStreamWriter(output));
    }

    private Configuration getFreemarkerConfig(final URL templateFile) throws Exception {
        Configuration config = new Configuration();


        config.setDirectoryForTemplateLoading(new File(templateFile.toURI()).getParentFile());
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
