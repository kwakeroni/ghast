package example.generatePojo.extract;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import example.generatePojo.Util;
import example.generatePojo.model.Pojo;

import java.lang.reflect.Method;

/**
 * @author Maarten Van Puymbroeck
 */
public class BeanGetterExtractor extends GetterExtractor {

    public Pojo getPojo(Class<?> getterClass) {
        return new GetterBean(getterClass);
    }

    public static Predicate<Class<?>> isClass() {
        return new Predicate<Class<?>>() {
            @Override
            public boolean apply(Class<?> input) {
                return ! input.isInterface();
            }
        };
    }

    protected static class GetterBean extends GetterExtractor.GetterPojo {
        public GetterBean(Class<?> baseClass) {
            super(validateBeanClass(baseClass));
        }

        @Override
        protected Iterable<Class<?>> getClassHierarchy(Class<?> base) {
            return Util.getRecursiveSuperclasses(base);
        }

        @Override
        protected Iterable<Method> getMethods(Class<?> type) {
            return Iterables.filter(Util.getDirectMethods(type), isGetter());
        }


        private static Class<?> validateBeanClass(Class<?> baseClass) {
            if (!isClass().apply(baseClass)) {
                throw new IllegalArgumentException("No class: " + baseClass.getName());
            }
            return baseClass;
        }

    }
}
