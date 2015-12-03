package example.generate.engine;

import example.generate.engine.state.BaseState;

import java.io.BufferedReader;
import java.io.PrintWriter;

/**
 * @author Maarten Van Puymbroeck
 */
public class Generator {

    private BufferedReader templateReader;
    private PrintWriter result;

    private Generator(BufferedReader templateReader, PrintWriter result){
        this.templateReader = templateReader;
        this.result = result;
    }


    private void run(GeneratorContext<?> context){

        Template template = new ReaderTemplate(this.templateReader);

        //Stack<GeneratorState> states = Stack<GeneratorState>();
        //states.push(new BaseState());

        //while(! states.isEmpty()){
            GeneratorState current = new BaseState(template, context);
            while (current != null){
                current = current.treatLine(this);
            }
        // }
    }

    public PrintWriter getResult(){
        return this.result;
    }

    public static void run(BufferedReader templateReader, PrintWriter result, GeneratorContext<?> context){
        new Generator(templateReader, result).run(context);
        result.flush();
    }

}
