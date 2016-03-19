package io.craigmiller160.locus.reflect;

import java.lang.reflect.Method;

/**
 * Created by craig on 3/15/16.
 */
public class ClassAndMethod extends ReflectiveMethodHolder<Class<?>> {

    public ClassAndMethod(Class<?> obj, Method m) {
        super(obj, m);
    }

    public boolean isAssignableFrom(Class<?> clazz){
        return getSource().isAssignableFrom(clazz);
    }

    @Override
    public Class<?> getSourceType(){
        return getSource();
    }

    @Override
    public String toString(){
        String className = getSource().getName();
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
