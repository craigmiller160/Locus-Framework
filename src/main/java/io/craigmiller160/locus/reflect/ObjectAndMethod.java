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
public class ObjectAndMethod {

    private final Object obj;
    private final Method m;
    private final Class<?>[] paramTypes;

    public ObjectAndMethod(Object obj, Method m){
        this.obj = obj;
        this.m = m;
        this.paramTypes = m.getParameterTypes();
    }

    public Object getObject(){
        return obj;
    }

    public Method getMethod(){
        return m;
    }

    public Class<?>[] getParamTypes(){
        return paramTypes;
    }

    public int getParamCount(){
        return paramTypes.length;
    }

    public boolean isVarArgs(){
        return m.isVarArgs();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ObjectAndMethod that = (ObjectAndMethod) o;

        if (obj != null ? !obj.equals(that.obj) : that.obj != null) return false;
        return !(m != null ? !m.equals(that.m) : that.m != null);

    }

    @Override
    public int hashCode() {
        int result = obj != null ? obj.hashCode() : 0;
        result = 31 * result + (m != null ? m.hashCode() : 0);
        return result;
    }

    @Override
    public String toString(){
        String className = obj.getClass().getName();
        String methodName = m.getName();
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
        String[] paramTypeNames = new String[paramTypes.length];
        for(int i = 0; i < paramTypeNames.length; i++){
            paramTypeNames[i] = paramTypes[i].getName();
        }
        return paramTypeNames;
    }

}
