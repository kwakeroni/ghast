package example.generatePojo.model;

import com.google.common.base.Function;

/**
 * @author Maarten Van Puymbroeck
 */
public interface Property {

    public String getName();

    public Type getType();


    public static Function<Property, Type> $getType = new Function<Property, Type>() {
            @Override
            public Type apply(Property input) {
                return input.getType();
            }
    };
}
