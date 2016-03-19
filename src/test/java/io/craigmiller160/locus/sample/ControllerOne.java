package io.craigmiller160.locus.sample;

import io.craigmiller160.locus.annotations.Controller;

/**
 * Created by craigmiller on 3/19/16.
 */
@Controller(name="ControllerOne")
public class ControllerOne {

    @Override
    public String toString(){
        return "ControllerOne is here";
    }

}
