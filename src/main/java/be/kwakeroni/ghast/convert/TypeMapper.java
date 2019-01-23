package be.kwakeroni.ghast.convert;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public interface TypeMapper<OldType, NewType, Form> {
    public Content<NewType, Form> map(Content<OldType, Form> content);

    default <NewContentType extends Content<NewType, Form>> NewContentType map(Content<OldType, Form> content, Function<? super Content<NewType, Form>, ? extends NewContentType> wrapper) {
        return wrapper.apply(map(content));
    }

//    public static <OldType, NewType, Form> TypeMapper<OldType, NewType, Form> of(UnaryOperator<Form> transformer) {
//        return content -> Content.of(content.type(), () -> transformer.apply(content.get()));
//    }

    public static <Type1, Type2, Form> TypeMapper<Type1, Type2, Form> alias(Class<Type2> type2) {
        return (Content<Type1, Form> content) -> (Content<Type2, Form>) content;
    }

}
