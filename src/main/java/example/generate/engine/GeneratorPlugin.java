package example.generate.engine;

/**
 * @author Maarten Van Puymbroeck
 */
public interface GeneratorPlugin {

    <Parameter extends Enum<Parameter>> Line process(GeneratorContext<Parameter> context, Line line);

}
