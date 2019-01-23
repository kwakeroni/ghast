package be.kwakeroni.ghast.convert;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public interface FormMapper<Type, OldContentType extends Content<Type, ?>, NewContentType extends Content<Type, ?>> {
    NewContentType map(OldContentType content);

    static <Type, OldForm, NewForm> FormMapper<Type, Content<Type, OldForm>, Content<Type, NewForm>>
    of(Function<OldForm, NewForm> transformation) {
        return content -> Content.of(content.type(), () -> transformation.apply(content.get()));
    }

    static <Type, OldForm, NewForm> FormMapper<Type, Content<Type, OldForm>, Content<Type, NewForm>>
    of(BiFunction<Type, OldForm, NewForm> transformation) {
        return content -> Content.of(content.type(), () -> transformation.apply(content.type(), content.get()));
    }

    static <Type, OldForm, NewForm, NewContentType extends Content<Type, NewForm>> FormMapper<Type, Content<Type, OldForm>, NewContentType>
    of(Function<OldForm, NewForm> transformation, BiFunction<Type, Supplier<NewForm>, NewContentType> factory) {
        return content -> factory.apply(content.type(), () -> transformation.apply(content.get()));
    }

    static <Type, OldForm, NewForm, NewContentType extends Content<Type, NewForm>> FormMapper<Type, Content<Type, OldForm>, NewContentType>
    of(BiFunction<Type, OldForm, NewForm> transformation, BiFunction<Type, Supplier<NewForm>, NewContentType> factory) {
        return content -> factory.apply(content.type(), () -> transformation.apply(content.type(), content.get()));
    }

}
