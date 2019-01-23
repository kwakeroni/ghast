package be.kwakeroni.ghast.convert.form;

import be.kwakeroni.ghast.convert.Content;
import be.kwakeroni.ghast.convert.TypeMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

public interface ByteArrayContent<Type> extends Content<Type, byte[]> {

    default <NewType> ByteArrayContent<NewType> mapTo(TypeMapper<Type, NewType, byte[]> mapping) {
        return mapping.map(this, ByteArrayContent::wrap);
    }

    static Path toFile(String fileName, byte[] bytes) {
        return toFile(Paths.get(fileName), bytes);
    }

    static Path toFile(Path fileName, byte[] bytes) {
        try {
            Files.write(fileName, bytes);
            return fileName;
        } catch (IOException exc) {
            throw new UncheckedIOException(exc);
        }
    }

    default FileContent<Type> toFile() {
        try {
            return toFile(Files.createTempFile("ghast", ".tmp"));
        } catch (IOException exc) {
            throw new UncheckedIOException(exc);
        }
    }

    default FileContent<Type> toFile(String fileName) {
        return FileContent.of(type(), () -> toFile(fileName, this.get()));
    }

    default FileContent<Type> toFile(Path fileName) {
        return FileContent.of(type(), () -> toFile(fileName, this.get()));
    }

    static <Type> ByteArrayContent<Type> wrap(Content<Type, byte[]> content) {
        if (content instanceof ByteArrayContent) {
            return (ByteArrayContent<Type>) content;
        }
        return ByteArrayContent.of(content.type(), content::get);
    }

    static <Type> ByteArrayContent<Type> of(Type type, byte[] source) {
        return of(type, () -> source);
    }

    static <Type> ByteArrayContent<Type> of(Type type, Supplier<? extends byte[]> source) {
        return new ByteArrayContent<Type>() {
            @Override
            public Type type() {
                return type;
            }

            @Override
            public byte[] get() {
                return source.get();
            }
        };
    }
}
