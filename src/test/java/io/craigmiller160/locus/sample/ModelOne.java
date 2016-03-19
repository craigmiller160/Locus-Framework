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
}
