package example.generatePojo.spi;

import com.google.common.base.Supplier;
import example.generatePojo.spi.impl.PojoImplGenerationRun;
import example.generatePojo.spi.json.JSonGenerationRun;

/**
 * @author Maarten Van Puymbroeck
 */
public class Generators {

    protected Generators(){

    }

    public static final <ParentType> Supplier<PojoImplGenerationRun.Builder<ParentType>> pojoImpl(){
        return PojoImplGenerationRun.pojoImpl();
    }

    public static final <ParentType> Supplier<JSonGenerationRun.Builder<ParentType>> jsonExample(){
        return JSonGenerationRun.jsonExample();
    }

}
