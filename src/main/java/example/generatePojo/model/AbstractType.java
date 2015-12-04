package example.generatePojo.model;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author Maarten Van Puymbroeck
 */
public abstract class AbstractType implements Type {

//    public String toString(){
//
//    }

    public static Iterable<Type> of(java.lang.reflect.Type[] types){
        return of(Arrays.asList(types));
    }

    public static Iterable<Type> of(Iterable<java.lang.reflect.Type> types){
        return Iterables.transform(types, reflectAsType());
    }

    private static Iterable<Class<?>> dependenciesOf(Iterable<Type> types){
        return Iterables.concat(Iterables.transform(types, Type.$getTypeDeclarationDependencies));
    }

    private static Function<java.lang.reflect.Type, Type> reflectAsType(){
        return new Function<java.lang.reflect.Type, Type>() {
            @Override
            public Type apply(java.lang.reflect.Type input) {
                return of(input);
            }
        };
    }

    public static Type of(java.lang.reflect.Type type){
        if (type instanceof Class){
            return of((Class<?>) type);
        } else if (type instanceof GenericArrayType){
            return of((GenericArrayType) type);
        } else if (type instanceof ParameterizedType){
            return of((ParameterizedType) type);
        } else if (type instanceof TypeVariable){
            return of((TypeVariable<?>) type);
        } else if (type instanceof WildcardType){
            return of((WildcardType) type);
        } else {
            throw new IllegalArgumentException(MessageFormat.format("Not recognized type {0} of type {1}", type, type.getClass()));
        }
    }

    public static Type ofExternal(final String simpleName, final Type... params){
        return new Type() {
            @Override
            public String getSimpleName() {
                if (params.length == 0){
                    return simpleName;
                }
                StringBuilder builder = new StringBuilder(simpleName).append('<');
                boolean first = true;
                for (Type param : params){
                    if (first){
                        first = false;
                    } else {
                        builder.append(", ");
                    }

                    builder.append(param.getSimpleName());
                }
                return builder.append('>').toString();
            }

            @Override
            public boolean isEqualType(java.lang.reflect.Type type) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Class<?> getRawClass() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Type getParameterType(int index) {
                return params[index];
            }

            @Override
            public Iterable<Class<?>> getTypeDeclarationDependencies() {
                return dependenciesOf(Arrays.asList(params));
            }
        };
    }

    public static Type ofGeneric(final Class<?> rawClass, final Type... params){
        if (rawClass.getTypeParameters().length != params.length){
            throw new IllegalArgumentException("Cannot parameterize " + rawClass + " with " + params.length + " type parameters");
        }

        return new Type() {
            @Override
            public String getSimpleName() {
                if (params.length == 0){
                    return rawClass.getSimpleName();
                }
                StringBuilder builder = new StringBuilder(rawClass.getSimpleName()).append('<');
                boolean first = true;
                for (Type param : params){
                    if (first){
                        first = false;
                    } else {
                        builder.append(", ");
                    }
                    builder.append(param.getSimpleName());

                }
                return builder.append('>').toString();
            }

            @Override
            public boolean isEqualType(java.lang.reflect.Type type) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Class<?> getRawClass() {
                return rawClass;
            }

            @Override
            public Type getParameterType(int index) {
                return params[index];
            }

            @Override
            public Iterable<Class<?>> getTypeDeclarationDependencies() {
                return Iterables.concat(Collections.singleton(rawClass), dependenciesOf(Arrays.asList(params)));
            }
        };
    }

    private static Type of(final Class<?> clazz){
        return new Type() {

            public Class<?> getRawClass() {
                return clazz;
            }

            @Override
            public Type getParameterType(int index) {
                throw new UnsupportedOperationException(clazz + " has no generic parameters");
            }

            @Override
            public String getSimpleName() {
                return clazz.getSimpleName();
            }

            @Override
            public Iterable<Class<?>> getTypeDeclarationDependencies() {
                return Collections.<Class<?>> singleton(clazz);
            }

            @Override
            public boolean isEqualType(java.lang.reflect.Type type) {
                return clazz.equals(type);
            }
        };
    }

    private static Type of(final ParameterizedType type){
        final Class<?> rawClass = (Class<?>) type.getRawType();
        return new Type() {

            public Class<?> getRawClass() {
                return rawClass;
            }

            @Override
            public Type getParameterType(int index) {
                return of(type.getActualTypeArguments()[index]);
            }

            @Override
            public String getSimpleName() {
                StringBuilder builder = new StringBuilder(rawClass.getSimpleName()).append('<');
                for (java.lang.reflect.Type param : type.getActualTypeArguments()){
                    builder.append(of(param).getSimpleName());
                }
                return builder.append('>').toString();
            }

            @Override
            public Iterable<Class<?>> getTypeDeclarationDependencies() {
                return Iterables.concat(Collections.singleton(rawClass), dependenciesOf(of(type.getActualTypeArguments())));
            }

            @Override
            public boolean isEqualType(java.lang.reflect.Type other) {
                return type.equals(other);
            }
        };
    }

    private static Type of(final GenericArrayType type){
        final Type componentType = of(type.getGenericComponentType());
        return new Type() {
            @Override
            public Class<?> getRawClass() {
                return Array.newInstance(componentType.getRawClass(), 0).getClass();
            }

            @Override
            public Type getParameterType(int index) {
                throw new UnsupportedOperationException("array has no generic parameters");
            }

            @Override
            public String getSimpleName() {
                return componentType.getSimpleName()+"[]";
            }

            @Override
            public Iterable<Class<?>> getTypeDeclarationDependencies() {
                return of(type.getGenericComponentType()).getTypeDeclarationDependencies();
            }

            @Override
            public boolean isEqualType(java.lang.reflect.Type other) {
                return type.equals(other);
            }
        };
    }

    private static Type of(final TypeVariable<?> type){
        return new Type() {
            @Override
            public Class<?> getRawClass() {
                return Object.class;
            }

            @Override
            public Type getParameterType(int index) {
                throw new UnsupportedOperationException("type parameter has no generic parameters");
            }

            @Override
            public String getSimpleName() {
                return type.getName();
            }

            @Override
            public Iterable<Class<?>> getTypeDeclarationDependencies() {
                return dependenciesOf(of(type.getBounds()));
            }

            @Override
            public boolean isEqualType(java.lang.reflect.Type other) {
                return type.equals(other);
            }
        };
    }

    private static Type of(final WildcardType type){
        return new Type() {
            @Override
            public Class<?> getRawClass() {
                return Object.class;
            }

            @Override
            public Type getParameterType(int index) {
                throw new UnsupportedOperationException("wildcard has no generic parameters");
            }

            @Override
            public String getSimpleName() {
                return "?";
            }

            @Override
            public Iterable<Class<?>> getTypeDeclarationDependencies() {
                return dependenciesOf(Iterables.concat(of(type.getLowerBounds()), of(type.getUpperBounds())));
            }

                        @Override
            public boolean isEqualType(java.lang.reflect.Type other) {
                return type.equals(other);
            }

        };
    }

}
