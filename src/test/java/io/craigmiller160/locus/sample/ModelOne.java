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

package io.craigmiller160.locus.sample;

import io.craigmiller160.locus.annotations.Model;

/**
 * A sample Model class to use for testing
 * this framework.
 *
 * Created by craig on 3/16/16.
 */
@Model
public class ModelOne {

    private String firstField;
    private String secondField;

    public String getFirstField() {
        return firstField;
    }

    public void setFirstField(String firstField) {
        this.firstField = firstField;
    }

    public String getSecondField() {
        return secondField;
    }

    public void setSecondField(String secondField) {
        this.secondField = secondField;
    }

    public void setFields(String...fields){
        if(fields.length > 0){
            firstField = fields[0];
        }

        if(fields.length > 1){
            secondField = fields[1];
        }
    }

    public String getField(int fieldNumber){
        String result = null;
        switch(fieldNumber){
            case 1:
                result = firstField;
                break;
            case 2:
                result = secondField;
                break;
        }

        return result;
    }
}
