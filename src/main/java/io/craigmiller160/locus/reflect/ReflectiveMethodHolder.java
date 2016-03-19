/*
 * Copyright 2016 Craig Miller
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
