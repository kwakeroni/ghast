package example.generate.engine.state;

import example.generate.engine.Generator;
import example.generate.engine.GeneratorContext;
import example.generate.engine.GeneratorState;
import example.generate.engine.Template;

/**
 * @author Maarten Van Puymbroeck
 */
public class BaseState implements GeneratorState {

    private GeneratorContext<?> context;
    private Template template;

    public BaseState(Template template, GeneratorContext<?> context){
        this.template = template;
        this.context = context;
    }

    @Override
    public GeneratorState treatLine(Generator generator) {
        String outLine = context.process(template.next());

        if (outLine != null){
                generator.getResult().println(outLine);
        }

        return (template.hasNext())? this : null;
    }


}
