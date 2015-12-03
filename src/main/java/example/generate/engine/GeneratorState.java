package example.generate.engine;

/**
 * @author Maarten Van Puymbroeck
 */
public interface GeneratorState {

    GeneratorState treatLine(Generator generator);

}
