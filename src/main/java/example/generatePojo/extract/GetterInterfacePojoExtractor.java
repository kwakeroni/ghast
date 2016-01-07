package example.generatePojo.extract;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import example.generatePojo.Util;
import example.generatePojo.model.AbstractType;
import example.generatePojo.model.Pojo;
import example.generatePojo.model.Type;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Set;

/**
 * @author Maarten Van Puymbroeck
 */
public class GetterInterfacePojoExtractor extends GetterExtractor {

    public Pojo getPojo(Class<?> getterInterface) {
        return new GetterInterfacePojo(getterInterface);
    }

    public static Predicate<Class<?>> isPojoInterface() {
        return new Predicate<Class<?>>() {
            @Override
            public boolean apply(Class<?> input) {
                return input.isInterface();
            }
        };
    }

    protected static class GetterInterfacePojo extends GetterExtractor.GetterPojo {
        public GetterInterfacePojo(Class<?> baseClass) {
            super(validatePojoInterface(baseClass));
        }

        @Override
        public Set<Type> getImplements() {
            return Collections.singleton(AbstractType.of(getBaseClass()));
        }

        @Override
        protected Iterable<Class<?>> getClassHierarchy(Class<?> base) {
            return Iterables.concat(
                                       Collections.singleton(base),
                                       Util.getRecursiveInterfaces(base));
        }

        @Override
        protected Iterable<Method> getMethods(Class<?> type) {
            return Util.getDirectMethods(type);
        }

        private static Class<?> validatePojoInterface(Class<?> baseClass) {
            if (!isPojoInterface().apply(baseClass)) {
                throw new IllegalArgumentException("No interface: " + baseClass.getName());
            }
            return baseClass;
        }

    }

}
