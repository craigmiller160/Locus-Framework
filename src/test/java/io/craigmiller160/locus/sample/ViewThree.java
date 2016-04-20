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

import io.craigmiller160.locus.annotations.LView;

/**
 * Another sample view object for use in
 * testing the Locus framework.
 *
 * Created by craig on 3/28/16.
 */
@LView
public class ViewThree {

    private String firstField;
    private String stringField;

    public void setFirstField(String firstField){
        this.firstField = firstField;
    }

    public String getViewThreeFirstField(){
        return firstField;
    }

    public void setStringField(String stringField){
        this.stringField = stringField;
    }

    public String getStringField(){
        return stringField;
    }

}
