/*
 * Copyright 2016 Craig Miller
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.craigmiller160.locus.util;

import io.craigmiller160.locus.sample.ModelOne;
import io.craigmiller160.locus.sample.ViewOne;
import io.craigmiller160.utils.reflect.ClassAndMethod;
import io.craigmiller160.utils.reflect.ObjectAndMethod;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * A JUnit test class for the LocusStorage class.
 *
 * Created by craig on 4/6/16.
 */
public class LocusStorageTest {

    private LocusStorage storage;

    public void setupStorage(){
        storage = new LocusStorage();

        ModelOne modelOne = new ModelOne();
        Method[] methods = modelOne.getClass().getDeclaredMethods();
        for(Method m : methods){
            if(m.getName().startsWith("set")){
                String propName = m.getName().substring(3);
                ObjectAndMethod oam = new ObjectAndMethod(modelOne, m);
                storage.addModelPropSetter(propName, oam);
            }
            else if(m.getName().startsWith("get") || m.getName().startsWith("is")){
                String propName = m.getName().startsWith("get") ? m.getName().substring(3) : m.getName().substring(2);
                ObjectAndMethod oam = new ObjectAndMethod(modelOne, m);
                storage.addModelPropGetter(propName, oam);
            }
        }

        ViewOne viewOne = new ViewOne();
        methods = viewOne.getClass().getDeclaredMethods();
        for(Method m : methods){
            if(m.getName().startsWith("set")){
                String propName = m.getName().substring(3);
                ClassAndMethod cam = new ClassAndMethod(ViewOne.class, m);
                storage.addViewPropSetter(propName, cam);
            }
        }
    }

    /**
     * Setup before each test.
     */
    @Before
    public void preTest(){
        setupStorage();
    }

    /**
     * Test getting all model property names. This should
     * return a set of the unique property names for the models
     * registered with the storage, ie having a getter & setter
     * for a property would still only count as one.
     */
    @Test
    public void testGetAllModelPropsNames(){
        Set<String> propNames = storage.getAllModelPropertyNames();

        assertNotNull("Model PropNames Set is null", propNames);
        assertEquals("Model PropNames Set is wrong size", 14, propNames.size());
    }

}
