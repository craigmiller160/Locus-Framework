package io.craigmiller160.locus.reflect;

import java.lang.reflect.Method;

/**
 * A special container class for a Method and a single
 * instance of the Class it is from. This is a helper
 * class for parsing methods for a reflective invocation.
 * It includes several methods to provide information
 * about the Method, and has the Object as well so,
 * after the parsing is done, the method can be quickly
 * invoked on its owning object.
 *
 * Created by Craig on 2/14/2016.
 */
public class ObjectAndMethod extends ReflectiveMethodHolder<Object>{

    public ObjectAndMethod(Object obj, Method m){
        super(obj, m);
    }

    @Override
    public String toString(){
        String className = getSource().getClass().getName();
        String methodName = getMethod().getName();
        String[] paramTypeNames = getParamTypeNames();

        StringBuilder builder = new StringBuilder()
                .append(className)
                .append(".")
                .append(methodName)
                .append("(");

        for(int i = 0; i < paramTypeNames.length; i++){
            builder.append(paramTypeNames[i]);
            if(i < paramTypeNames.length - 1){
                builder.append(",");
            }
        }
        builder.append(")");

        return builder.toString();
    }

    private String[] getParamTypeNames(){
        String[] paramTypeNames = new String[getMethodParamTypes().length];
        for(int i = 0; i < paramTypeNames.length; i++){
            paramTypeNames[i] = getMethodParamTypes()[i].getName();
        }
        return paramTypeNames;
    }

}
