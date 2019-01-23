package be.kwakeroni.ghast.convert.diagram;

import be.kwakeroni.ghast.convert.Content;
import be.kwakeroni.ghast.convert.form.FileContent;
import be.kwakeroni.ghast.convert.form.Generator;
import be.kwakeroni.ghast.convert.image.EMF;
import be.kwakeroni.ghast.convert.image.PNG;
import be.kwakeroni.ghast.convert.image.SVG;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

import java.nio.file.Path;
import java.util.function.Function;

public final class PUML {

    public static final PUML type = new PUML();

    private PUML() {

    }

    public static Function<Content<PUML, byte[]>, Generator<PNG>> png() {
        return Generator.transformer(PNG.type, PUMLImpl.imageWriter(FileFormat.PNG));
    }

    public static Function<Content<PUML, byte[]>, Generator<SVG>> svg() {
        return Generator.transformer(SVG.type, PUMLImpl.imageWriter(FileFormat.SVG));
    }

    public static Function<Content<PUML, byte[]>, FileContent<EMF>> emf() {
        return content ->
                content.mapTo(PUML.svg())
                .toFile()
                .mapTo(svgToEmf());
    }

    public static Function<Content<SVG, Path>, FileContent<EMF>> svgToEmf() {
        return FileContent.transformer(EMF.type, PUMLImpl::convertSvgFileToEmf);
    }

}
