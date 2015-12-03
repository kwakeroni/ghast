package example.generatePojo;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import org.apache.poi.ss.formula.functions.T;


import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Maarten Van Puymbroeck
 */
public class Util {

    public static String removePrefix(String value, String... prefixes) {
        String prefix = getPrefix(value, prefixes);
        return value.substring(prefix.length());
    }

    public static String getPrefix(String value, String... prefixes) {
        for (String prefix : prefixes) {
            if (value.startsWith(prefix)) {
                return prefix;
            }
        }
        throw new IllegalArgumentException("Could not recognize prefix for " + value);
    }

    public static Iterable<Class<?>> getDirectInterfaces(Class<?> child) {
        return Arrays.asList(child.getInterfaces());
    }

    public static Iterable<Class<?>> getRecursiveInterfaces(final Class<?> child) {
        return Iterables.concat(getRecursiveInterfaces(Collections.<Class<?>>singleton(child)));
    }

    public static Iterable<Iterable<Class<?>>> getRecursiveInterfaces(final Iterable<Class<?>> interfaces) {
        return Iterables.transform(interfaces, directInterfaces());
    }

    public static Function<Class<?>, Iterable<Class<?>>> directInterfaces() {
        return new Function<Class<?>, Iterable<Class<?>>>() {
            @Override
            public Iterable<Class<?>> apply(Class<?> input) {
                return getDirectInterfaces(input);
            }
        };
    }

    public static Iterable<Method> getDirectMethods(Class<?> clazz) {
        return Arrays.asList(clazz.getMethods());
    }

    public static Function<Class<?>, Iterable<Method>> directMethods() {
        return new Function<Class<?>, Iterable<Method>>() {
            @Override
            public Iterable<Method> apply(Class<?> input) {
                return getDirectMethods(input);
            }
        };
    }

    public static <T> Iterable<T> fromNullable(T t){
        return (t == null)? Collections.<T> emptySet() : Collections.<T> singleton(t);
    }

    public static <T> Iterable<T> concat(T t, Iterable<T> iterable){
        return Iterables.concat(Collections.singleton(t), iterable);
    }

    public static <T> Iterable<T> concat(T t, Function<T, Iterable<T>> function){
        return concat(t, function.apply(t));
    }

    public static <T> Iterable<T> recurse(T t, Function<T, Iterable<T>> function){
        List<T> elements = new ArrayList<>();
        elements.add(t);
        List<T> recursive = recurse(elements, function);
        elements.addAll(recursive);
        return elements;
    }

    private static <T> List<T> recurse(Iterable<T> iterable, Function<T, Iterable<T>> function){
        List<T> newElements = new ArrayList<>();
        for (T t : iterable){
            addAll(newElements, function.apply(t));
        }

        if (newElements.size() == 0){
            return newElements;
        } else {
            List<T> recursive = recurse(newElements, function);
            newElements.addAll(recursive);
            return newElements;
        }
    }

    private static <T> void addAll(List<T> accu, Iterable<T> iterable){
        for (T t : iterable){
            accu.add(t);
        }
    }


    public static <T> Iterable<T> concat(T t, Iterable<T> iterable, T... ts){
        return Iterables.concat(Collections.singleton(t), iterable, Arrays.asList(ts));
    }

    public static <T> Iterable<T> concat(T t, Iterable<T>... iterables){
        return concat(t, Iterables.concat(iterables));
    }

    public static <T> Iterable<T> concat(Iterable<T> iterable, T... ts){
        return Iterables.concat(iterable, Arrays.asList(ts));
    }

    public static <T> Iterable<T> distinct(final Iterable<T> iterable){
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return new DistinctIterator<>(iterable.iterator());
            }
        };
    }

        public static <T> Iterable<T> distinct(final Iterable<T> iterable, final BiPredicate<T> equals){
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return new DistinctIterator<>(iterable.iterator(), equals);
            }
        };
    }

    private static class DistinctIterator<T> extends CachingIterator<T> {

        private final BiPredicate<T> equals;
        private final Set<T> encountered;

        public DistinctIterator(Iterator<T> source) {
            this(source, null);
        }
        public DistinctIterator(Iterator<T> source, BiPredicate<T> equals) {
            super(source, false);
            this.encountered = new HashSet<>();
            this.equals = equals;
            advance();
        }
        @Override
        protected void advance() {
            super.advance();
            if (hasNext && encountered(next)){
                this.advance();
            }
            encountered.add(next);
        }
        private boolean encountered(T element){
            if (equals == null){
                return encountered.contains(element);
            } else {
                for (T t : encountered){
                    if (equals.evaluate(element, t)){
                        return true;
                    }
                }
                return false;
            }
        }
    }

    private static class CachingIterator<T> implements Iterator<T> {
        final Iterator<T> source;
        boolean hasNext = false;
        T next;

        public CachingIterator(Iterator<T> source){
            this(source, true);
        }

        protected CachingIterator(Iterator<T> source, boolean doAdvance){
            this.source = source;
            if (doAdvance) advance();
        }

        protected void advance(){
            if (this.source.hasNext()){
                hasNext = true;
                next = this.source.next();
            } else {
                hasNext = false;
                next = null;
            }
        }

        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public T next() {
            T result = next;
            advance();
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public static Function<String, String> messageFormat(final String pattern){
        return new Function<String, String>() {
            @Override
            public String apply(String input) {
                return MessageFormat.format(pattern, input);
            }
        };
    }

    public static interface BiPredicate<T> {
        public boolean evaluate(T t1, T t2);
    }
}
