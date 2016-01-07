package example.generatePojo.extract;

import example.generatePojo.PojoExtractor;

/**
 * @author Maarten Van Puymbroeck
 */
public class Extractors {

    protected Extractors(){

    }

    public static PojoExtractor<Class<?>> getterInterface(){
        return new GetterInterfacePojoExtractor();
    }

    public static PojoExtractor<Class<?>> beanClass(){
        return new BeanGetterExtractor();
    }
}
