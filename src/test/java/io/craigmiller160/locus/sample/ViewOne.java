package io.craigmiller160.locus.sample;

import io.craigmiller160.locus.annotations.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by craigmiller on 3/19/16.
 */
@View
public class ViewOne {

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
