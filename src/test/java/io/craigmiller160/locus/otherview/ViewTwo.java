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
