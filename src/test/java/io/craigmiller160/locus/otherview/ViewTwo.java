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

package io.craigmiller160.locus.otherview;

import io.craigmiller160.locus.annotations.View;

/**
 * A second view in a separate package. Used
 * primarily for testing the validation
 * logic, since it has a method that
 * should register as a duplicate with
 * ViewOne.
 *
 * Created by craigmiller on 3/19/16.
 */
@View
public class ViewTwo {

    private String firstField;

    public String getFirstField() {
        return firstField;
    }

    public void setFirstField(String firstField) {
        this.firstField = firstField;
    }
}
