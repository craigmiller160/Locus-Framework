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

package io.craigmiller160.locus.util;

import io.craigmiller160.locus.TestUtils;
import io.craigmiller160.locus.concurrent.UIThreadExecutor;
import io.craigmiller160.locus.sample.ControllerOne;
import io.craigmiller160.locus.sample.ModelOne;
import io.craigmiller160.locus.sample.SampleUIThreadExecutor;
import io.craigmiller160.locus.sample.ViewOne;
import io.craigmiller160.utils.reflect.ClassAndMethod;
import io.craigmiller160.utils.reflect.ObjectAndMethod;
import org.junit.Before;
import org.junit.Test;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * A JUnit test class for the LocusStorage class.
 *
 * Created by craig on 4/6/16.
 */
public class LocusStorageTest {

    private LocusStorage storage;

    private static final String STRING_FIELD = "StringField";
    private static final String INT_FIELD = "IntField";
    private static final String STRING = "String";

    private static final String CONTROLLER_ONE_NAME = "ControllerOne";

    private ModelOne modelOne;
    private ViewOne viewOne;

    private ObjectAndMethod oam_setStringField_String;
    private ObjectAndMethod oam_setStringField_Int;
    private ObjectAndMethod oam_setIntField_Int;
    private ObjectAndMethod oam_getStringField;
    private ObjectAndMethod oam_getIntField;
    private ObjectAndMethod oam_addString_String;
    private ObjectAndMethod oam_removeString_String;

    private ClassAndMethod cam_setStringField_String;
    private ClassAndMethod cam_setIntField_Int;
    private ClassAndMethod cam_getStringField;
    private ClassAndMethod cam_getIntField;
    private ClassAndMethod cam_addString_String;
    private ClassAndMethod cam_removeString_String;

    /**
     * Setup the ObjectAndMethod fields.
     *
     * @throws Exception if unable to set up the fields.
     */
    private void setupModelFields() throws Exception{
        modelOne = new ModelOne();
        Method m1Setter1 = modelOne.getClass().getMethod("setStringField", String.class);
        oam_setStringField_String = new ObjectAndMethod(modelOne, m1Setter1);

        Method m1Setter2 = modelOne.getClass().getMethod("setStringField", int.class);
        oam_setStringField_Int = new ObjectAndMethod(modelOne, m1Setter2);

        Method m1Setter3 = modelOne.getClass().getMethod("setIntField", int.class);
        oam_setIntField_Int = new ObjectAndMethod(modelOne, m1Setter3);

        Method m1Getter1 = modelOne.getClass().getMethod("getStringField");
        oam_getStringField = new ObjectAndMethod(modelOne, m1Getter1);

        Method m1Getter2 = modelOne.getClass().getMethod("getIntField");
        oam_getIntField = new ObjectAndMethod(modelOne, m1Getter2);

        Method m1Adder1 = modelOne.getClass().getMethod("addString", String.class);
        oam_addString_String = new ObjectAndMethod(modelOne, m1Adder1);

        Method m1Remover1 = modelOne.getClass().getMethod("removeString", String.class);
        oam_removeString_String = new ObjectAndMethod(modelOne, m1Remover1);
    }

    /**
     * Setup the view fields.
     *
     * @throws Exception if unable to setup fields.
     */
    private void setupViewFields() throws Exception{
        Class<?> clazz = ViewOne.class;
        viewOne = new ViewOne();

        Method v1setter1 = clazz.getMethod("setStringField", String.class);
        cam_setStringField_String = new ClassAndMethod(clazz, v1setter1);

        Method v1setter3 = clazz.getMethod("setIntField", int.class);
        cam_setIntField_Int = new ClassAndMethod(clazz, v1setter3);

        Method v1getter1 = clazz.getMethod("getStringField");
        cam_getStringField = new ClassAndMethod(clazz, v1getter1);

        Method v1getter2 = clazz.getMethod("getIntField");
        cam_getIntField = new ClassAndMethod(clazz, v1getter2);

        Method v1adder1 = clazz.getMethod("addString", String.class);
        cam_addString_String = new ClassAndMethod(clazz, v1adder1);

        Method v1remover1 = clazz.getMethod("removeString", String.class);
        cam_removeString_String = new ClassAndMethod(clazz, v1remover1);
    }

    /**
     * Setup before each test.
     */
    @Before
    public void preTest() throws Exception{
        storage = TestUtils.setupStorage();
        setupModelFields();
        setupViewFields();
    }

    /**
     * Test getting all model property names. This should
     * return a set of the unique property names for the models
     * registered with the storage, ie having a getter & setter
     * for a property would still only count as one.
     */
    @Test
    public void testGetAllModelPropsNames(){
        TestUtils.setupModels(storage);
        Set<String> propNames = storage.getAllModelPropertyNames();

        assertNotNull("Model PropNames Set is null", propNames);
        assertEquals("Model PropNames Set is wrong size", 15, propNames.size());
    }

    /**
     * Test getting all view property names. This should
     * return a set of the unique property names for all
     * the views registered with the storage.
     */
    @Test
    public void testGetAllViewPropNames(){
        TestUtils.setupViews(storage);
        Set<String> propNames = storage.getAllViewPropNames();

        assertNotNull("View PropNames Set is null", propNames);
        assertEquals("View PropNames Set is wrong size", 14, propNames.size());
    }

    /**
     * Test getting all controller names.
     */
    @Test
    public void testGetAllControllerNames(){
        TestUtils.setupControllers(storage);
        Set<String> controllerNames = storage.getAllControllerNames();

        assertNotNull("ControllerNames Set is null", controllerNames);
        assertEquals("ControllerNames Set is wrong size", 2, controllerNames.size());
    }

    /**
     * Test setting and getting the UIThreadExecutor.
     */
    @Test
    public void testSetUiThreadExecutor(){
        storage.setUIThreadExecutorType(SampleUIThreadExecutor.class);

        Class<? extends UIThreadExecutor> clazz = storage.getUIThreadExecutorType();
        assertNotNull("UIThreadExecutor class type is null", clazz);
        assertEquals("UIThreadExecutor class type is wrong type", SampleUIThreadExecutor.class, clazz);
    }

    /**
     * Test adding a model prop setter
     */
    @Test
    public void testAddModelPropSetter(){
        storage.addModelPropSetter(STRING_FIELD, oam_setStringField_String);

        int modelPropSetterCount = storage.getModelPropSetterCount();
        assertEquals(String.format("Wrong number of model property setters"), 1, modelPropSetterCount);

        Collection<ObjectAndMethod> oams = storage.getSettersForModelProp(STRING_FIELD);
        assertNotNull(String.format("No setters for model property %s", STRING_FIELD), oams);
        assertEquals(String.format("Wrong number of setters for model property %s", STRING_FIELD), 1, oams.size());

        oams = storage.getAllModelPropSetters();
        assertNotNull("AllModelPropSetters collection is null", oams);
        assertEquals("AllModelPropSetters collection has wrong size", 1, oams.size());
    }

    /**
     * Test removing a model prop setter.
     */
    @Test
    public void testRemoveModelPropSetter(){
        storage.addModelPropSetter(STRING_FIELD, oam_setStringField_String);
        storage.addModelPropSetter(STRING_FIELD, oam_setStringField_Int);

        //Test the overall size before proceeding
        int modelPropSetterCount = storage.getModelPropSetterCount();
        assertEquals(String.format("Wrong number of model property setters pre-remove"), 2, modelPropSetterCount);

        storage.removeModelPropSetter(oam_setStringField_Int);

        modelPropSetterCount = storage.getModelPropSetterCount();
        assertEquals(String.format("Wrong number of model property setters"), 1, modelPropSetterCount);

        Collection<ObjectAndMethod> oams = storage.getSettersForModelProp(STRING_FIELD);
        assertNotNull(String.format("No setters for model property %s", STRING_FIELD), oams);
        assertEquals(String.format("Wrong number of setters for model property %s", STRING_FIELD), 1, oams.size());

        oams = storage.getAllModelPropSetters();
        assertNotNull("AllModelPropSetters collection is null", oams);
        assertEquals("AllModelPropSetters collection has wrong size", 1, oams.size());
    }

    /**
     * Test removing all setter methods for a model property.
     */
    @Test
    public void testRemoveAllSettersForModelProp(){
        storage.addModelPropSetter(STRING_FIELD, oam_setStringField_String);
        storage.addModelPropSetter(STRING_FIELD, oam_setStringField_Int);

        //Test the overall size before proceeding
        int modelPropSetterCount = storage.getModelPropSetterCount();
        assertEquals(String.format("Wrong number of model property setters pre-remove"), 2, modelPropSetterCount);

        storage.removeSettersForModelProp(STRING_FIELD);

        modelPropSetterCount = storage.getModelPropSetterCount();
        assertEquals(String.format("Wrong number of model property setters"), 0, modelPropSetterCount);

        Collection<ObjectAndMethod> oams = storage.getSettersForModelProp(STRING_FIELD);
        assertNull(String.format("Setters should not exist for model property %s", STRING_FIELD), oams);

        oams = storage.getAllModelPropSetters();
        assertNotNull("AllModelPropSetters collection is null", oams);
        assertEquals("AllModelPropSetters collection has wrong size", 0, oams.size());
    }

    /**
     * Test adding a new model prop getter method.
     */
    @Test
    public void testAddModelPropGetter(){
        storage.addModelPropGetter(STRING_FIELD, oam_getStringField);

        int modelPropGetterCount = storage.getModelPropGetterCount();
        assertEquals(String.format("Wrong number of model property getters"), 1, modelPropGetterCount);

        Collection<ObjectAndMethod> oams = storage.getGettersForModelProp(STRING_FIELD);
        assertNotNull(String.format("No getters for model property %s", STRING_FIELD), oams);
        assertEquals(String.format("Wrong number of getters for model property %s", STRING_FIELD), 1, oams.size());

        oams = storage.getAllModelPropGetters();
        assertNotNull("AllModelPropGetters collection is null", oams);
        assertEquals("AllModelPropGetters collection has wrong size", 1, oams.size());
    }

    /**
     * Test removing a model prop getter method.
     */
    @Test
    public void testRemoveModelPropGetter(){
        storage.addModelPropGetter(STRING_FIELD, oam_getStringField);
        storage.addModelPropGetter(STRING_FIELD, oam_getIntField);

        //Get a count of model prop getters before proceeding
        int modelPropGetterCount = storage.getModelPropGetterCount();
        assertEquals(String.format("Wrong number of model property getters pre-remove"), 2, modelPropGetterCount);

        storage.removeModelPropGetter(oam_getIntField);

        modelPropGetterCount = storage.getModelPropGetterCount();
        assertEquals(String.format("Wrong number of model property getters"), 1, modelPropGetterCount);

        Collection<ObjectAndMethod> oams = storage.getGettersForModelProp(STRING_FIELD);
        assertNotNull(String.format("No getters for model property %s", STRING_FIELD), oams);
        assertEquals(String.format("Wrong number of getters for model property %s", STRING_FIELD), 1, oams.size());

        oams = storage.getAllModelPropGetters();
        assertNotNull("AllModelPropGetters collection is null", oams);
        assertEquals("AllModelPropGetters collection has wrong size", 1, oams.size());
    }

    /**
     * Test removing all getters for a model property.
     */
    @Test
    public void testRemoveGettersForModelProp(){
        storage.addModelPropGetter(STRING_FIELD, oam_getStringField);
        storage.addModelPropGetter(STRING_FIELD, oam_getIntField);

        //Get a count of model prop getters before proceeding
        int modelPropGetterCount = storage.getModelPropGetterCount();
        assertEquals(String.format("Wrong number of model property getters pre-remove"), 2, modelPropGetterCount);

        storage.removeGettersForModelProp(STRING_FIELD);

        modelPropGetterCount = storage.getModelPropGetterCount();
        assertEquals(String.format("Wrong number of model property getters"), 0, modelPropGetterCount);

        Collection<ObjectAndMethod> oams = storage.getGettersForModelProp(STRING_FIELD);
        assertNull(String.format("There should be no getters for model property %s", STRING_FIELD), oams);

        oams = storage.getAllModelPropGetters();
        assertNotNull("AllModelPropGetters collection is null", oams);
        assertEquals("AllModelPropGetters collection has wrong size", 0, oams.size());
    }

    /**
     * Test adding a model prop adder method.
     */
    @Test
    public void testAddModelPropAdder(){
        storage.addModelPropAdder(STRING, oam_addString_String);

        int modelPropAdderCount = storage.getModelPropAdderCount();
        assertEquals(String.format("Wrong number of model property adders"), 1, modelPropAdderCount);

        Collection<ObjectAndMethod> oams = storage.getAddersForModelProp(STRING);
        assertNotNull(String.format("No adders for model property %s", STRING), oams);
        assertEquals(String.format("Wrong number of adders for model property %s", STRING), 1, oams.size());

        oams = storage.getAllModelPropAdders();
        assertNotNull("AllModelPropAdders collection is null", oams);
        assertEquals("AllModelPropAdders collection has wrong size", 1, oams.size());
    }

    /**
     * Test removeModelPropAdder.
     */
    @Test
    public void testRemoveModelPropAdder(){
        storage.addModelPropAdder(STRING, oam_addString_String);

        //Get a count of model prop getters before proceeding
        int modelPropAdderCount = storage.getModelPropAdderCount();
        assertEquals(String.format("Wrong number of model property adders pre-remove"), 1, modelPropAdderCount);

        storage.removeModelPropAdder(oam_addString_String);

        modelPropAdderCount = storage.getModelPropAdderCount();
        assertEquals(String.format("Wrong number of model property adders"), 0, modelPropAdderCount);

        Collection<ObjectAndMethod> oams = storage.getAddersForModelProp(STRING);
        assertNull(String.format("There should be no adders for model property %s", STRING), oams);

        oams = storage.getAllModelPropAdders();
        assertNotNull("AllModelPropAdders collection is null", oams);
        assertEquals("AllModelPropAdders collection has wrong size", 0, oams.size());
    }

    /**
     * Test removing all adders for a model property.
     */
    @Test
    public void testRemoveAddersForModelProp(){
        storage.addModelPropAdder(STRING, oam_addString_String);

        //Get a count of model prop adders before proceeding
        int modelPropAdderCount = storage.getModelPropAdderCount();
        assertEquals(String.format("Wrong number of model property adders pre-remove"), 1, modelPropAdderCount);

        storage.removeAddersForModelProp(STRING);

        modelPropAdderCount = storage.getModelPropAdderCount();
        assertEquals(String.format("Wrong number of model property adders"), 0, modelPropAdderCount);

        Collection<ObjectAndMethod> oams = storage.getAddersForModelProp(STRING);
        assertNull(String.format("There should be no adders for model property %s", STRING), oams);

        oams = storage.getAllModelPropAdders();
        assertNotNull("AllModelPropAdders collection is null", oams);
        assertEquals("AllModelPropAdders collection has wrong size", 0, oams.size());
    }

    /**
     * Test adding a new model prop remover method.
     */
    @Test
    public void testAddModelPropRemover(){
        storage.addModelPropRemover(STRING, oam_removeString_String);

        int modelPropRemoverCount = storage.getModelPropRemoverCount();
        assertEquals(String.format("Wrong number of model property removers"), 1, modelPropRemoverCount);

        Collection<ObjectAndMethod> oams = storage.getRemoversForModelProp(STRING);
        assertNotNull(String.format("No removers for model property %s", STRING), oams);
        assertEquals(String.format("Wrong number of removers for model property %s", STRING), 1, oams.size());

        oams = storage.getAllModelPropRemovers();
        assertNotNull("AllModelPropRemovers collection is null", oams);
        assertEquals("AllModelPropRemovers collection has wrong size", 1, oams.size());
    }

    /**
     * Test removing a model prop remover method.
     */
    @Test
    public void testRemoveModelPropRemover(){
        storage.addModelPropRemover(STRING, oam_removeString_String);

        //Get a count of model prop removers before proceeding
        int modelPropRemoverCount = storage.getModelPropRemoverCount();
        assertEquals(String.format("Wrong number of model property removers pre-remove"), 1, modelPropRemoverCount);

        storage.removeModelPropRemover(oam_removeString_String);

        modelPropRemoverCount = storage.getModelPropRemoverCount();
        assertEquals(String.format("Wrong number of model property removers"), 0, modelPropRemoverCount);

        Collection<ObjectAndMethod> oams = storage.getRemoversForModelProp(STRING);
        assertNull(String.format("There should be no removers for model property %s", STRING), oams);

        oams = storage.getAllModelPropRemovers();
        assertNotNull("AllModelPropRemovers collection is null", oams);
        assertEquals("AllModelPropRemovers collection has wrong size", 0, oams.size());
    }

    /**
     * Test removing all removers for a model property.
     */
    @Test
    public void testRemoveRemoversForModelProp(){
        storage.addModelPropRemover(STRING, oam_removeString_String);

        //Get a count of model prop removers before proceeding
        int modelPropRemoverCount = storage.getModelPropRemoverCount();
        assertEquals(String.format("Wrong number of model property removers pre-remove"), 1, modelPropRemoverCount);

        storage.removeRemoversForModelProp(STRING);

        modelPropRemoverCount = storage.getModelPropRemoverCount();
        assertEquals(String.format("Wrong number of model property removers"), 0, modelPropRemoverCount);

        Collection<ObjectAndMethod> oams = storage.getRemoversForModelProp(STRING);
        assertNull(String.format("There should be no removers for model property %s", STRING), oams);

        oams = storage.getAllModelPropRemovers();
        assertNotNull("AllModelPropRemovers collection is null", oams);
        assertEquals("AllModelPropRemovers collection has wrong size", 0, oams.size());
    }

    /**
     * Test adding a view property setter.
     */
    @Test
    public void testAddViewPropSetter(){
        storage.addViewPropSetter(STRING_FIELD, cam_setStringField_String);

        int viewPropSetterCount = storage.getViewPropSetterCount();
        assertEquals(String.format("Wrong number of view property setters"), 1, viewPropSetterCount);

        Collection<ClassAndMethod> cams = storage.getSettersForViewProp(STRING_FIELD);
        assertNotNull(String.format("No setters for view property %s", STRING_FIELD), cams);
        assertEquals(String.format("Wrong number of setters for view property %s", STRING_FIELD), 1, cams.size());

        cams = storage.getAllViewPropSetters();
        assertNotNull("AllViewPropSetters collection is null", cams);
        assertEquals("AllViewPropSetters collection has wrong size", 1, cams.size());
    }

    /**
     * Test removing a view property setter.
     */
    @Test
    public void testRemoveViewPropSetter(){
        storage.addViewPropSetter(STRING_FIELD, cam_setStringField_String);

        //Get a count of view prop setters before proceeding
        int viewPropSetterCount = storage.getViewPropSetterCount();
        assertEquals(String.format("Wrong number of view property setters pre-remove"), 1, viewPropSetterCount);

        storage.removeViewPropSetter(cam_setStringField_String);

        viewPropSetterCount = storage.getViewPropSetterCount();
        assertEquals(String.format("Wrong number of view property setters"), 0, viewPropSetterCount);

        Collection<ClassAndMethod> cams = storage.getSettersForViewProp(STRING_FIELD);
        assertNull(String.format("There should be no setters for view property %s", STRING_FIELD), cams);

        cams = storage.getAllViewPropSetters();
        assertNotNull("AllViewPropSetters collection is null", cams);
        assertEquals("AllViewPropSetters collection has wrong size", 0, cams.size());
    }

    /**
     * Test removing all setters for a view property.
     */
    @Test
    public void testRemoveSettersForViewProp(){
        storage.addViewPropSetter(STRING_FIELD, cam_setStringField_String);

        //Get a count of view prop setters before proceeding
        int viewPropSetterCount = storage.getViewPropSetterCount();
        assertEquals(String.format("Wrong number of view property setters pre-remove"), 1, viewPropSetterCount);

        storage.removeSettersForViewProp(STRING_FIELD);

        viewPropSetterCount = storage.getViewPropSetterCount();
        assertEquals(String.format("Wrong number of view property setters"), 0, viewPropSetterCount);

        Collection<ClassAndMethod> cams = storage.getSettersForViewProp(STRING_FIELD);
        assertNull(String.format("There should be no setters for view property %s", STRING_FIELD), cams);

        cams = storage.getAllViewPropSetters();
        assertNotNull("AllViewPropSetters collection is null", cams);
        assertEquals("AllViewPropSetters collection has wrong size", 0, cams.size());
    }

    /**
     * Test adding a view prop adder.
     */
    @Test
    public void testAddViewPropAdder(){
        storage.addViewPropAdder(STRING, cam_addString_String);

        int viewPropAdderCount = storage.getViewPropAdderCount();
        assertEquals(String.format("Wrong number of view property adders"), 1, viewPropAdderCount);

        Collection<ClassAndMethod> cams = storage.getAddersForViewProp(STRING);
        assertNotNull(String.format("No adders for view property %s", STRING), cams);
        assertEquals(String.format("Wrong number of adders for view property %s", STRING), 1, cams.size());

        cams = storage.getAllViewPropAdders();
        assertNotNull("AllViewPropAdders collection is null", cams);
        assertEquals("AllViewPropAdders collection has wrong size", 1, cams.size());
    }

    /**
     * Test removing a view property adder.
     */
    @Test
    public void testRemoveViewPropAdder(){
        storage.addViewPropAdder(STRING, cam_addString_String);

        //Get a count of view prop adders before proceeding
        int viewPropAdderCount = storage.getViewPropAdderCount();
        assertEquals(String.format("Wrong number of view property adders pre-remove"), 1, viewPropAdderCount);

        storage.removeViewPropAdder(cam_addString_String);

        viewPropAdderCount = storage.getViewPropAdderCount();
        assertEquals(String.format("Wrong number of view property adders"), 0, viewPropAdderCount);

        Collection<ClassAndMethod> cams = storage.getAddersForViewProp(STRING);
        assertNull(String.format("There should be no adders for view property %s", STRING), cams);

        cams = storage.getAllViewPropAdders();
        assertNotNull("AllViewPropAdders collection is null", cams);
        assertEquals("AllViewPropAdders collection has wrong size", 0, cams.size());
    }

    /**
     * Test removing all view adders for a property.
     */
    @Test
    public void testRemoveViewAddersForProp(){
        storage.addViewPropAdder(STRING, cam_addString_String);

        //Get a count of view prop adders before proceeding
        int viewPropAdderCount = storage.getViewPropAdderCount();
        assertEquals(String.format("Wrong number of view property adders pre-remove"), 1, viewPropAdderCount);

        storage.removeAddersForViewProp(STRING);

        viewPropAdderCount = storage.getViewPropAdderCount();
        assertEquals(String.format("Wrong number of view property adders"), 0, viewPropAdderCount);

        Collection<ClassAndMethod> cams = storage.getAddersForViewProp(STRING);
        assertNull(String.format("There should be no adders for view property %s", STRING), cams);

        cams = storage.getAllViewPropAdders();
        assertNotNull("AllViewPropAdders collection is null", cams);
        assertEquals("AllViewPropAdders collection has wrong size", 0, cams.size());
    }

    /**
     * Test adding a view prop remover.
     */
    @Test
    public void testAddViewPropRemover(){
        storage.addViewPropRemover(STRING, cam_removeString_String);

        int viewPropRemoverCount = storage.getViewPropRemoverCount();
        assertEquals(String.format("Wrong number of view property removers"), 1, viewPropRemoverCount);

        Collection<ClassAndMethod> cams = storage.getRemoversForViewProp(STRING);
        assertNotNull(String.format("No removers for view property %s", STRING), cams);
        assertEquals(String.format("Wrong number of removers for view property %s", STRING), 1, cams.size());

        cams = storage.getAllViewPropRemovers();
        assertNotNull("AllViewPropRemovers collection is null", cams);
        assertEquals("AllViewPropRemovers collection has wrong size", 1, cams.size());
    }

    /**
     * Test removing a view prop remover.
     */
    @Test
    public void testRemoveViewPropRemover(){
        storage.addViewPropRemover(STRING, cam_removeString_String);

        //Get a count of view prop adders before proceeding
        int viewPropRemoverCount = storage.getViewPropRemoverCount();
        assertEquals(String.format("Wrong number of view property removers pre-remove"), 1, viewPropRemoverCount);

        storage.removeViewPropRemover(cam_removeString_String);

        viewPropRemoverCount = storage.getViewPropRemoverCount();
        assertEquals(String.format("Wrong number of view property removers"), 0, viewPropRemoverCount);

        Collection<ClassAndMethod> cams = storage.getRemoversForViewProp(STRING);
        assertNull(String.format("There should be no removers for view property %s", STRING), cams);

        cams = storage.getAllViewPropRemovers();
        assertNotNull("AllViewPropRemovers collection is null", cams);
        assertEquals("AllViewPropRemovers collection has wrong size", 0, cams.size());
    }

    /**
     * Test removing all removers for view property.
     */
    @Test
    public void testRemoveViewRemoversForProp(){
        storage.addViewPropRemover(STRING, cam_removeString_String);

        //Get a count of view prop adders before proceeding
        int viewPropRemoverCount = storage.getViewPropRemoverCount();
        assertEquals(String.format("Wrong number of view property removers pre-remove"), 1, viewPropRemoverCount);

        storage.removeRemoversForViewProp(STRING);

        viewPropRemoverCount = storage.getViewPropRemoverCount();
        assertEquals(String.format("Wrong number of view property removers"), 0, viewPropRemoverCount);

        Collection<ClassAndMethod> cams = storage.getRemoversForViewProp(STRING);
        assertNull(String.format("There should be no removers for view property %s", STRING), cams);

        cams = storage.getAllViewPropRemovers();
        assertNotNull("AllViewPropRemovers collection is null", cams);
        assertEquals("AllViewPropRemovers collection has wrong size", 0, cams.size());
    }

    /**
     * Test adding a view instance
     */
    @Test
    public void testAddViewInstance(){
        storage.addViewInstance(viewOne);

        assertEquals("Wrong number of view instances", 1, storage.getViewInstanceCount());

        Collection<WeakReference<?>> views = storage.getViewInstancesForClass(ViewOne.class);
        assertNotNull("View instances collection is null", views);
        assertEquals("Wrong number of view instances in collection", 1, views.size());
        assertEquals("Wrong view instance retrieved", viewOne, views.iterator().next().get());
    }

    /**
     * Test removing a view instance.
     */
    @Test
    public void testRemoveViewInstance(){
        storage.addViewInstance(viewOne);

        //Test the initial size to ensure that the adding worked
        assertEquals("Wrong number of view instances pre-remove", 1, storage.getViewInstanceCount());

        storage.removeViewInstance(viewOne);

        assertEquals("Wrong number of view instances", 0, storage.getViewInstanceCount());

        Collection<WeakReference<?>> views = storage.getViewInstancesForClass(ViewOne.class);
        assertNull("View instances collection should be null", views);
    }

    /**
     * Test removing all view instances for a class.
     */
    @Test
    public void testRemoveViewInstancesForClass(){
        storage.addViewInstance(viewOne);

        //Test the initial size to ensure that the adding worked
        assertEquals("Wrong number of view instances pre-remove", 1, storage.getViewInstanceCount());

        storage.removeViewInstancesForClass(viewOne.getClass());

        assertEquals("Wrong number of view instances", 0, storage.getViewInstanceCount());

        Collection<WeakReference<?>> views = storage.getViewInstancesForClass(ViewOne.class);
        assertNull("View instances collection should be null", views);
    }

    /**
     * Test adding a controller class type
     */
    @Test
    public void testAddControllerType(){
        storage.addControllerType(CONTROLLER_ONE_NAME, ControllerOne.class);

        assertEquals("Wrong number of controller types", 1, storage.getControllerTypeCount());
        Class<?> clazz = storage.getControllerType(CONTROLLER_ONE_NAME);
        assertNotNull(String.format("%s class type is null", CONTROLLER_ONE_NAME), clazz);
        assertEquals(String.format("%s clazz type is incorrect", CONTROLLER_ONE_NAME), ControllerOne.class, clazz);
    }

    /**
     * Test removing a controller type.
     */
    @Test
    public void testRemoveControllerType(){
        boolean isSingleton = true;
        storage.addControllerType(CONTROLLER_ONE_NAME, ControllerOne.class);

        //Test the initial size to ensure the add worked
        assertEquals("Wrong number of controller types pre-remove", 1, storage.getControllerTypeCount());

        storage.removeControllerType(CONTROLLER_ONE_NAME);

        assertEquals("Wrong number of controller types", 0, storage.getControllerTypeCount());
    }

    /**
     * Test adding a controller callback, and ensuring that it
     * is cleared when all strong references are gone.
     */
    @Test
    public void testAddControllerCallback(){
        BigDecimal callback = new BigDecimal(33.3);
        ControllerOne controllerOne = new ControllerOne();

        storage.addControllerCallback(controllerOne, callback);

        Object result = storage.getControllerCallback(controllerOne);
        assertNotNull("Controller callback is null", result);
        assertEquals("Controller callback is wrong object", callback, result);

        //Clear strong references to test the WeakReference behavior.
        callback = null;
        result = null;
        Runtime.getRuntime().gc();

        result = storage.getControllerCallback(controllerOne);
        assertNull("Controller callback should be cleared as no strong references exist anymore", result);
    }

    /**
     * Test removing a controller callback.
     */
    @Test
    public void testRemoveControllerCallback(){
        BigDecimal callback = new BigDecimal(33.3);
        ControllerOne controllerOne = new ControllerOne();

        storage.addControllerCallback(controllerOne, callback);

        //Test that it exists in the first place before removing it
        Object result = storage.getControllerCallback(controllerOne);
        assertNotNull("Controller callback is null", result);

        storage.removeControllerCallback(controllerOne);

        result = storage.getControllerCallback(controllerOne);
        assertNull("Controller callback was not removed", result);
    }

}
