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

/**
 * A special exception for if an error occurs while trying to
 * parse the configuration file.
 *
 * Created by craig on 3/15/16.
 */
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
