package example.generatePojo.spi.impl;

import example.generatePojo.model.Property;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.Arrays;

/**
 * @author Maarten Van Puymbroeck
 */
public class SetterPlugin extends PluginSupport {

    @Override
    public Iterable<String> getMethods(Property property) {

        return Arrays.asList(   "",
                                MessageFormat.format("    public void set{0}({1} {2}) '{'", StringUtils.capitalize(property.getName()), property.getType().getSimpleName(), property.getName()),
                                MessageFormat.format("        this.{0} = {0};", property.getName()),
                                MessageFormat.format("    }", "")
        );
    }


}
