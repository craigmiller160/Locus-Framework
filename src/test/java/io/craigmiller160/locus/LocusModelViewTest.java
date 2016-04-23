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

package io.craigmiller160.locus;

import io.craigmiller160.locus.concurrent.UIThreadExecutorFactory;
import io.craigmiller160.locus.sample.ModelOne;
import io.craigmiller160.locus.sample.ViewOne;
import io.craigmiller160.locus.sample.ViewThree;
import io.craigmiller160.locus.util.LocusStorage;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

    private static final Logger logger = LoggerFactory.getLogger(LocusModelViewTest.class);

    private LocusStorage storage;
    private LocusModel locusModel;
    private LocusView locusView;
    private ViewOne viewOne;
    private ViewThree viewThree;
    private ModelOne modelOne;
    private UIThreadExecutorFactory factory;

    private void setupViews(){
        Map<Class<?>,Object> views = TestUtils.setupViews(storage);
        viewOne = (ViewOne) views.get(ViewOne.class);
        viewThree = (ViewThree) views.get(ViewThree.class);
    }

    private void setupModels(){
        Map<Class<?>,Object> models = TestUtils.setupModels(storage);
        modelOne = (ModelOne) models.get(ModelOne.class);
    }

    private void setupLocus(){
        this.locusView = new LocusView(storage, factory);
        this.locusModel = new LocusModel(storage, locusView);
    }

    @Before
    public void initialize(){
        storage = TestUtils.setupStorage();
        factory = TestUtils.setupUIThreadExecutor(storage);
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

    /**
     * Test adding a value to a collection property in both
     * a model and a view.
     */
    @Test
    public void testAddModelAndView(){
        String value = "Value";
        locusModel.addValue("String", value);

        String mResult = modelOne.getString(0);
        String vResult = viewOne.getString(0);

        assertEquals("ModeOne has the wrong value in the first position in its String collection", value, mResult);
        assertEquals("ViewOne has the wrong value in the first position in its String collection", value, vResult);
    }

    /**
     * Test removing a value from a collection property in
     * both a model and a view.
     */
    @Test
    public void testRemoveModelAndView(){
        String value = "Value";
        modelOne.addString(value);
        viewOne.addString(value);

        locusModel.removeValue("String", value);

        boolean modelException = false;
        try{
            modelOne.getString(0);
        }
        catch(IndexOutOfBoundsException ex){
            modelException = true;
            logger.error("LocusModelViewTest testRemoveModelAndView() model exception stack trace", ex);
        }

        boolean viewException = false;
        try{
            viewOne.getString(0);
        }
        catch(IndexOutOfBoundsException ex){
            viewException = true;
            logger.error("LocusModelViewTest testRemoveModelAndView() model exception stack trace", ex);
        }

        assertTrue("ModelOne should've thrown an exception for the value not being in the collection", modelException);
        assertTrue("ViewOne should've thrown an exception for the value not being in the collection", viewException);
    }

}
