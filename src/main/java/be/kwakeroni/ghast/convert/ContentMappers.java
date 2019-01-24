package be.kwakeroni.ghast.convert;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public final class ContentMappers {

    private ContentMappers() {
        throw new UnsupportedOperationException("Cannot instantiate");
    }

    public static <Type, OldForm, NewForm> Function<Content<Type, OldForm>, Content<Type, NewForm>>
    mappingForm(Function<OldForm, NewForm> transformation) {
        return content -> Content.of(content.type(), () -> transformation.apply(content.get()));
    }

    public static <Type, OldForm, NewForm> Function<Content<Type, OldForm>, Content<Type, NewForm>>
    mappingForm(BiFunction<Type, OldForm, NewForm> transformation) {
        return content -> Content.of(content.type(), () -> transformation.apply(content.type(), content.get()));
    }

    public static <Type, OldForm, NewForm, NewContentType extends Content<Type, NewForm>> Function<Content<Type, OldForm>, NewContentType>
    mappingForm(Function<OldForm, NewForm> transformation, BiFunction<Type, Supplier<NewForm>, NewContentType> factory) {
        return content -> factory.apply(content.type(), () -> transformation.apply(content.get()));
    }

    public static <Type, OldForm, NewForm, NewContentType extends Content<Type, NewForm>> Function<Content<Type, OldForm>, NewContentType>
    mappingForm(BiFunction<Type, OldForm, NewForm> transformation, BiFunction<Type, Supplier<NewForm>, NewContentType> factory) {
        return content -> factory.apply(content.type(), () -> transformation.apply(content.type(), content.get()));
    }

    public static <Type, Form> Function<Content<Type, Form>, Content<Type, Form>> mapping(UnaryOperator<Form> transformer) {
        return content -> Content.of(content.type(), () -> transformer.apply(content.get()));
    }

    public static <Type1, Type2, Form> Function<Content<Type1, Form>, Content<Type2, Form>> alias(Type2 type2) {
        return (Content<Type1, Form> content) -> Content.of(type2, content::get);
    }
}
