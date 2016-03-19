package io.craigmiller160.locus.sample2;

import io.craigmiller160.locus.annotations.Model;

/**
 * A second model in a package not specified
 * in the configuration, so this class
 * should NOT be detected by the scanner.
 *
 * Created by craig on 3/16/16.
 */
@Model
public class ModelTwo {

    private String fieldThree;
    private String fieldFour;

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
