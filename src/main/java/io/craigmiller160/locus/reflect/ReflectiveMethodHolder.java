package io.craigmiller160.locus.reflect;

import java.lang.reflect.Method;

/**
 * Created by craigmiller on 3/19/16.
 */
public abstract class ReflectiveMethodHolder<T> {

    private final T source;
    private final Method method;
    private final Class<?>[] paramTypes;

    protected ReflectiveMethodHolder(T source, Method method){
        this.source = source;
        this.method = method;
        this.paramTypes = method.getParameterTypes();
    }

    public T getSource(){
        return source;
    }

    public Method getMethod(){
        return method;
    }

    public Class<?>[] getMethodParamTypes(){
        return paramTypes;
    }

    public int getMethodParamCount(){
        return paramTypes.length;
    }

    public boolean isMethodVarArgs(){
        return method.isVarArgs();
    }

    public abstract Class<?> getSourceType();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReflectiveMethodHolder<?> that = (ReflectiveMethodHolder<?>) o;

        if (source != null ? !source.equals(that.source) : that.source != null) return false;
        return method != null ? method.equals(that.method) : that.method == null;

    }

    @Override
    public int hashCode() {
        int result = source != null ? source.hashCode() : 0;
        result = 31 * result + (method != null ? method.hashCode() : 0);
        return result;
    }
}
