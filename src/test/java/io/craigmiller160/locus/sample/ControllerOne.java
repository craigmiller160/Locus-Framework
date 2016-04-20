/*
 * Copyright 2016 Craig Miller
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.craigmiller160.locus.sample;

import io.craigmiller160.locus.annotations.LController;

/**
 * A sample class with a @LController annotation,
 * used for testing the Locus Framework.
 *
 * Created by craigmiller on 3/19/16.
 */
@LController(name="ControllerOne")
public class ControllerOne {

    private String id;

    public void setId(String id){
        this.id = id;
    }

    public String getId(){
        return id;
    }

    @Override
    public String toString(){
        return "ControllerOne is here";
    }

}
