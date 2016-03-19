package io.craigmiller160.locus.reflect;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Created by craigmiller on 3/19/16.
 */
public class ObjectCreatorTest {

    @Test
    public void testInstantiateObject(){
        StringBuilder builder = ObjectCreator.instantiateClass(StringBuilder.class);

        assertNotNull(builder);
    }

}
