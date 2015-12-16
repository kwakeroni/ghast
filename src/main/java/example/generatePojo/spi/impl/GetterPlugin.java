package example.generatePojo.spi.impl;

import example.generatePojo.model.Property;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.Arrays;

/**
 * @author Maarten Van Puymbroeck
 */
public class GetterPlugin extends PluginSupport {

    @Override
    public Iterable<String> getMethods(Property property) {

        return Arrays.asList(   "",
                                MessageFormat.format("    public {0} {1}{2}() '{'", property.getType().getSimpleName(), (property.getType().isEqualType(boolean.class)) ? "is" : "get", StringUtils.capitalize(property.getName())),
                                MessageFormat.format("        return this.{0};", property.getName()),
                                MessageFormat.format("    }", "")
        );
    }

}
