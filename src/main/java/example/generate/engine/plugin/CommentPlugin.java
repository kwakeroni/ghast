package example.generate.engine.plugin;

import example.generate.engine.GeneratorContext;
import example.generate.engine.GeneratorPlugin;
import example.generate.engine.Line;

/**
 * @author Maarten Van Puymbroeck
 */
public class CommentPlugin implements GeneratorPlugin {
    @Override
    public <Parameter extends Enum<Parameter>> Line process(GeneratorContext<Parameter> context, Line line) {
        if (line.getString() == null){
            return line;
        }

        int index = line.getString().indexOf('#');
        if (index == 0){
            return new Line(null, line.getNumber());
        } else if (index > 0){
            return new Line(line.getString().substring(index), line.getNumber());
        } else {
            return line;
        }
    }
}
