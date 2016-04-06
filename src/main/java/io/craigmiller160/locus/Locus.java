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

import io.craigmiller160.locus.util.LocusStorage;

/**
 * The central class of the Locus Framework.
 * This class provides easy, static access to
 * manipulate system resources through its three
 * subclasses.
 *
 * Created by craig on 3/12/16.
 */
public class Locus {

    //TODO this needs to be made totally thread safe

    static final int GETTER = 101;
    static final int SETTER = 102;

    private static final LocusStorage storage = LocusStorage.getInstance();

    public static LocusModel model = new LocusModel();

    public static LocusController controller = new LocusController();

    public static LocusView view = new LocusView();

    public static LocusDebug debug = new LocusDebug();

    public static void initialize(){
        //TODO critical method that needs to be finished
        //TODO this must validate that every model setter/getter is unique
        //TODO this must also validate that every view getter is unique
    }

}
