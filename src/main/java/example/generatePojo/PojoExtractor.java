package example.generatePojo;

import example.generatePojo.model.Pojo;

/**
 * @author Maarten Van Puymbroeck
 */
public interface PojoExtractor<T> {

    public Pojo getPojo(T getterInterface);

}
