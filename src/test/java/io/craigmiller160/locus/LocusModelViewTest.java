/*
 * Copyright 2016 Craig Miller
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.craigmiller160.locus;

import io.craigmiller160.locus.concurrent.NoUIThreadExecutor;
import io.craigmiller160.locus.concurrent.UIThreadExecutorFactory;
import io.craigmiller160.locus.sample.ModelOne;
import io.craigmiller160.locus.sample.ViewOne;
import io.craigmiller160.locus.sample.ViewThree;
import io.craigmiller160.locus.util.LocusStorage;
import io.craigmiller160.utils.reflect.ClassAndMethod;
import io.craigmiller160.utils.reflect.ObjectAndMethod;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * A special test class to test the combined
 * functionality of LocusModel & LocusView.
 * LocusModel should be able to remotely update
 * properties in LocusView every time it updates
 * a model.
 *
 * Please note that if any of these tests are failing,
 * it is entirely possible that other tests are failing
 * as well in the individual LocusModelTest & LocusViewTest
 * classes. Those tests will need to be resolved first
 * before being able to ensure that these work.
 *
 * Created by craig on 4/1/16.
 */
public class LocusModelViewTest {

    private LocusStorage storage;
    private LocusModel locusModel;
    private LocusView locusView;
    private ViewOne viewOne;
    private ViewThree viewThree;
    private ModelOne modelOne;
    private UIThreadExecutorFactory factory;

    private void setupStorage(){
        try{
            Constructor<LocusStorage> constructor = LocusStorage.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            storage = constructor.newInstance();
            storage.setUIThreadExecutorType(NoUIThreadExecutor.class);
        }
        catch(Exception ex){
            throw new RuntimeException("Fatal exception while trying to construct LocusStorage for test", ex);
        }
    }

    private void setupUIThreadExecutor(){
        try{
            Constructor<UIThreadExecutorFactory> constructor = UIThreadExecutorFactory.class.getDeclaredConstructor(LocusStorage.class);
            constructor.setAccessible(true);
            factory = constructor.newInstance(storage);
        }
        catch(Exception ex){
            throw new RuntimeException("Fatal exception while trying to construct LocusStorage for test", ex);
        }
    }

    private void setupViews(){
        try{
            viewOne = new ViewOne();
            viewThree = new ViewThree();

            Method m1 = viewOne.getClass().getMethod("setStringField", String.class);
            Method m2 = viewThree.getClass().getMethod("setStringField", String.class);
            Method m4 = viewOne.getClass().getMethod("setObjectField", Object.class);

            ClassAndMethod cam1 = new ClassAndMethod(viewOne.getClass(), m1);
            ClassAndMethod cam2 = new ClassAndMethod(viewThree.getClass(), m2);
            ClassAndMethod cam4 = new ClassAndMethod(viewOne.getClass(), m4);

            storage.addViewPropSetter("StringField", cam1);
            storage.addViewPropSetter("StringField", cam2);
            storage.addViewPropSetter("ObjectField", cam4);

            storage.addViewInstance(viewOne.getClass(), viewOne);
            storage.addViewInstance(viewThree.getClass(), viewThree);
        }
        catch(Exception ex){
            throw new RuntimeException("Fatal exception while trying to setup views for test", ex);
        }
    }

    private void setupModels(){
        try{
            modelOne = new ModelOne();

            Method m1 = modelOne.getClass().getMethod("setStringField", String.class);
            Method m2 = modelOne.getClass().getMethod("setObjectField", Object.class);

            ObjectAndMethod oam1 = new ObjectAndMethod(modelOne, m1);
            ObjectAndMethod oam2 = new ObjectAndMethod(modelOne, m2);

            storage.addModelPropSetter("StringField", oam1);
            storage.addModelPropSetter("ObjectField", oam2);
        }
        catch(Exception ex){
            throw new RuntimeException("Fatal exception while trying to setup models for test", ex);
        }
    }

    private void setupLocus(){
        this.locusView = new LocusView(storage, factory);
        this.locusModel = new LocusModel(storage, locusView);
    }

    @Before
    public void initialize(){
        setupStorage();
        setupUIThreadExecutor();
        setupViews();
        setupModels();
        setupLocus();
    }

    /**
     * Test setting a property in a model and the
     * corresponding view.
     */
    @Test
    public void testSetModelAndView(){
        BigDecimal value = new BigDecimal("3.33333");
        locusModel.setValue("ObjectField", value);

        assertNotNull("ModelOne ObjectField is null", modelOne.getObjectField());
        assertNotNull("ViewOne ObjectField is null", viewOne.getObjectField());

        assertEquals("ModelOne ObjectField wrong value", value, modelOne.getObjectField());
        assertEquals("ViewOne ObjectField wrong value", value, viewOne.getObjectField());
    }

    /**
     * Test setting a model property in multiple views.
     */
    @Test
    public void testSetModelAndMultipleViews(){
        String value = "Value";
        locusModel.setValue("StringField", value);

        assertNotNull("ModelOne StringField is null", modelOne.getStringField());
        assertNotNull("ViewOne StringField is null", viewOne.getStringField());
        assertNotNull("ViewThree StringField is null", viewThree.getStringField());

        assertEquals("ModelOne StringField wrong value", value, modelOne.getStringField());
        assertEquals("ViewOne StringField wrong value", value, viewOne.getStringField());
        assertEquals("ViewThree StringField wrong value", value, viewThree.getStringField());
    }

}
