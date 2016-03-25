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

package io.craigmiller160.locus.othermodel;

import io.craigmiller160.locus.annotations.Model;

/**
 * A second model in a package not specified
 * in the configuration, so this class
 * should NOT be detected by the scanner
 * during the main scanner tests.
 *
 * However, this is also used for
 * othermodel tests, especially for
 * duplicate methods.
 *
 * Created by craig on 3/16/16.
 */
@Model
public class ModelTwo {

    private String stringField;
    private String fieldThree;
    private String fieldFour;

    public void setStringField(String stringField){
        this.stringField = stringField;
    }

    public String getStringField(){
        return stringField;
    }

    public String getFieldThree() {
        return fieldThree;
    }

    public void setFieldThree(String fieldThree) {
        this.fieldThree = fieldThree;
    }

    public String getFieldFour() {
        return fieldFour;
    }

    public void setFieldFour(String fieldFour) {
        this.fieldFour = fieldFour;
    }
}
