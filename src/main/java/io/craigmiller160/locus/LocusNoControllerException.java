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
 * <p>An extension of the LocusException for if a
 * controller requested is not available, either
 * because it doesn't exist or because the developer
 * didn't configure it properly.</p>
 *
 * @author craigmiller
 * @version 1.0
 */
public class LocusNoControllerException extends LocusException{

    public LocusNoControllerException() {
    }

    public LocusNoControllerException(String message) {
        super(message);
    }

    public LocusNoControllerException(String message, Throwable cause) {
        super(message, cause);
    }

    public LocusNoControllerException(Throwable cause) {
        super(cause);
    }
}
