package io.craigmiller160.locus.reflect;

import java.lang.reflect.InvocationTargetException;

/**
 * Special class for reflectively invoking methods.
 *
 * Created by craig on 3/12/16.
 */
public class LocusInvoke {

    public static Object invokeMethod(ObjectAndMethod oam, Object...params) throws LocusReflectiveException{
        Object result = null;
        if(MethodUtils.isValidInvocation(oam.getMethod(), params)){
            if(oam.isMethodVarArgs()){
                params = MethodUtils.convertParamsForVarArgsMethod(oam.getMethod(), params);
            }

            try{
                result = oam.getMethod().invoke(oam.getObject(), params);
            }
            catch(InvocationTargetException ex){
                //TODO logging?
                throw new LocusInvocationException("Method invoked threw exception", ex.getCause());
            }
            catch(ReflectiveOperationException ex){
                //TODO logging?
                throw new LocusReflectiveException("Unable to reflectively invoke method " + oam.getMethod().getName() +
                        " on " + oam.getObject().getClass().getName(), ex);
            }
        }
        else{
            throw new LocusReflectiveException("Parameters provided for method " + oam.getMethod().getName() + " do not match what is expected.");
        }

        return result;
    }

}
