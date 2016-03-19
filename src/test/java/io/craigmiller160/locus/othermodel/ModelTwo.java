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

    private String firstField;
    private String fieldThree;
    private String fieldFour;

    public void setFirstField(String firstField){
        this.firstField = firstField;
    }

    public String getFirstField(){
        return firstField;
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
