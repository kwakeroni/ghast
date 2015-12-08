package example.generatePojo;

import example.generatePojo.model.AbstractPojo;
import example.generatePojo.model.AbstractType;
import example.generatePojo.model.Pojo;
import example.generatePojo.model.Property;
import example.generatePojo.model.Type;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Maarten Van Puymbroeck
 */
public class GetterInterfacePojoExtractor implements PojoExtractor<Class<?>> {

    public Pojo getPojo(Class<?> getterInterface) {
        return new GetterInterfacePojo(getterInterface);
    }

    private static class GetterInterfacePojo extends AbstractPojo {

        private Class<?> getterInterface;

        public GetterInterfacePojo(Class<?> getterInterface) {
            if (!isPojoInterface().apply(getterInterface)) {
                throw new IllegalArgumentException("No interface: " + getterInterface.getName());
            }

            this.getterInterface = getterInterface;
        }

        @Override
        public Type getExtends() {
            return null;
        }

        @Override
        public Set<Type> getImplements() {
            return Collections.singleton(AbstractType.of(getterInterface));
        }

        @Override
        public Iterable<Property> getProperties() {

            return Iterables.<Method, Property>transform( Iterables.filter(
                                                            Iterables.concat(
                                                                                Iterables.transform(
                                                                                                       Iterables.<Class<?>>concat(
                                                                                                                                     Collections.singleton(this.getterInterface),
                                                                                                                                     Util.getRecursiveInterfaces(this.getterInterface)),
                                                                                                       Util.directMethods())
                                                            ), distinct()), toGetterProperty()
            );

        }

        @Override
        public String getPackage() {
            return getterInterface.getPackage().getName();
        }

        @Override
        public String getName() {
            return getterInterface.getName();
        }

        @Override
        public String getSimpleName() {
            return getterInterface.getSimpleName();
        }

        @Override
        public String toString() {
            return generateToString();
        }

    }

    private static class GetterProperty implements Property {

        private static final String[] prefixes = {"get", "is", "has"};

        private final Method method;

        public GetterProperty(Method method) {
            if (method.getParameterTypes().length != 0
                    || method.getReturnType().equals(Void.TYPE)
                    || Util.getPrefix(method.getName(), prefixes) == null) {
                throw new IllegalArgumentException("No getter method: " + method);
            }

            this.method = method;
        }

        @Override
        public String getName() {
            return StringUtils.uncapitalize(
                                               Util.removePrefix(method.getName(), prefixes));
        }

        @Override
        public Type getType() {
            return AbstractType.of(method.getGenericReturnType());
        }

        @Override
        public String toString() {
            return getType().getSimpleName() + " " + getName();
        }
    }

    private static Function<Method, GetterProperty> toGetterProperty() {
        return new Function<Method, GetterProperty>() {
            @Override
            public GetterProperty apply(Method input) {
                return new GetterProperty(input);
            }
        };
    }

    public static Predicate<Class<?>> isPojoInterface() {
        return new Predicate<Class<?>>() {
            @Override
            public boolean apply(Class<?> input) {
                return input.isInterface();
            }
        };
    }

    public static Predicate<Method> distinct() {
        return new Predicate<Method>() {
            private final Set<Method> encountered = new HashSet<>();
            @Override
            public boolean apply(Method input) {
                if (encountered.contains(input)){
                    return false;
                }
                encountered.add(input);
                return true;
            }
        };
    }

}
