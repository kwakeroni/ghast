package example.generate.engine.plugin;

import example.generate.engine.GeneratorContext;
import example.generate.engine.GeneratorPlugin;
import example.generate.engine.Line;

/**
 * @author Maarten Van Puymbroeck
 */
public class ReplaceVariablePlugin implements GeneratorPlugin {

    public <Parameter extends Enum<Parameter>> Line process(GeneratorContext<Parameter> context, Line line){
        if (line.getString() == null){
            return line;
        }

        String result = line.getString();
        for (Parameter parameter : context.getParameters()){
            if (context.isDefined(parameter)){
                result = result.replaceAll("\\$\\{" + parameter.name() + "\\}", context.get(parameter));
            }
        }
        if (result.contains("${")) throw new IllegalStateException("Could not replace variable at " + line.getNumber() + ":" + result.indexOf("${") + " (" + result + ")");
        return new Line(result, line.getNumber());
    }

}
