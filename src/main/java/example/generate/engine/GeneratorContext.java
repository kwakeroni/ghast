package example.generate.engine;

import example.generate.engine.plugin.CommentPlugin;
import example.generate.engine.plugin.ReplaceVariablePlugin;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * @author Maarten Van Puymbroeck
 */
public class GeneratorContext<Parameter extends Enum<Parameter>> {

        private final Class<Parameter> parameterType;
        private final Map<Parameter, String> arguments;
        private final List<GeneratorPlugin> plugins;

        public GeneratorContext(Class<Parameter> parameterType){
            this.parameterType = parameterType;
            this.arguments = new EnumMap<Parameter, String>(parameterType);
            this.plugins = Arrays.<GeneratorPlugin> asList(new CommentPlugin(), new ReplaceVariablePlugin());
        }


        public void set(Parameter parameter, String argument){
            this.arguments.put(parameter, argument);
        }

        public boolean isDefined(Parameter parameter){
            return this.arguments.containsKey(parameter);
        }

        public String get(Parameter parameter){
            return this.arguments.get(parameter);
        }

        public void unset(Parameter parameter){
            this.arguments.remove(parameter);
        }

    public Collection<Parameter> getParameters(){
        return Arrays.asList(parameterType.getEnumConstants());
    }

    public String process(Line line){
        Line processed = line;
        for (GeneratorPlugin plugin : plugins){
            processed = plugin.process(this, processed);
        }
        return processed.getString();
    }
}
