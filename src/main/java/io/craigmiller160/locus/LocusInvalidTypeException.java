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

package io.craigmiller160.locus;

/**
 * <p>An extension of LocusException for if object types
 * don't match up. This is primarily for the convenience
 * methods offered by this framework, with its wide variety
 * of types. Internal casting is frequently done, and if the
 * type found doesn't match the type expected, this exception
 * is thrown.</p>
 *
 * @author craigmiller
 * @version 1.0
 */
public class LocusInvalidTypeException extends LocusException {

    public LocusInvalidTypeException() {
    }

    public LocusInvalidTypeException(String message) {
        super(message);
    }

    public LocusInvalidTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public LocusInvalidTypeException(Throwable cause) {
        super(cause);
    }
}
