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

package io.craigmiller160.locus.util;

import io.craigmiller160.locus.LocusException;

import javax.annotation.concurrent.ThreadSafe;

/**
 * <p>A special exception for if an error occurs while trying to
 * parse the configuration file.</p>
 *
 * <p><b>THREAD SAFETY:</b> This class has no mutable
 * state and is therefore completely thread-safe.</p>
 *
 * @author craigmiller
 * @version 1.0
 */
@ThreadSafe
public class LocusParsingException extends LocusException {

    public LocusParsingException() {
    }

    public LocusParsingException(String message) {
        super(message);
    }

    public LocusParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public LocusParsingException(Throwable cause) {
        super(cause);
    }
}
