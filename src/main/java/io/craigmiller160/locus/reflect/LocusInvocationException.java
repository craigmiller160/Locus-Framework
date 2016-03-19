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

import io.craigmiller160.locus.LocusException;

/**
 * A subclass of LocusException intended for wrapping
 * and handling non-reflective exceptions that occur
 * while reflectively accessing a method. Essentially
 * it's a non-checked version of Java's InvocationTargetException.
 *
 * Created by craig on 3/12/16.
 */
public class LocusInvocationException extends LocusException{

    //TODO ensure that making this unchecked doesn't lead to potential problems, since it'll be wrapping checked exceptions

    public LocusInvocationException() {
    }

    public LocusInvocationException(String message) {
        super(message);
    }

    public LocusInvocationException(String message, Throwable cause) {
        super(message, cause);
    }

    public LocusInvocationException(Throwable cause) {
        super(cause);
    }
}
