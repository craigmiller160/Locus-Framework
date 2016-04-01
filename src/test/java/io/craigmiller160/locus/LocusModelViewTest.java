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

package io.craigmiller160.locus;

import io.craigmiller160.locus.reflect.ClassAndMethod;
import io.craigmiller160.locus.sample.ModelOne;
import io.craigmiller160.locus.sample.ViewOne;
import io.craigmiller160.locus.sample.ViewThree;
import io.craigmiller160.locus.util.LocusStorage;
import org.junit.Before;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

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

    private void setupStorage(){
        try{
            Constructor<LocusStorage> constructor = LocusStorage.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            storage = constructor.newInstance();
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
            Method m3 = viewOne.getClass().getMethod("setTwoFields", String.class, String.class);
            Method m4 = viewOne.getClass().getMethod("setObjectField", Object.class);

            ClassAndMethod cam1 = new ClassAndMethod(viewOne.getClass(), m1);
            ClassAndMethod cam2 = new ClassAndMethod(viewThree.getClass(), m2);
            ClassAndMethod cam3 = new ClassAndMethod(viewOne.getClass(), m3);
            ClassAndMethod cam4 = new ClassAndMethod(viewOne.getClass(), m4);

            storage.addViewPropSetter("FirstField", cam1);
            storage.addViewPropSetter("FirstField", cam2);
            storage.addViewPropSetter("TwoFields", cam3);
            storage.addViewPropSetter("ObjectField", cam4);

            storage.addViewInstance(viewOne.getClass(), viewOne);
            storage.addViewInstance(viewThree.getClass(), viewThree);
        }
        catch(Exception ex){
            throw new RuntimeException("Fatal exception while trying to setup views for test", ex);
        }
    }

    private void setupModels(){
        modelOne = new ModelOne();
    }

    private void setupLocus(){
        this.locusView = new LocusView(storage);
    }

    @Before
    public void initialize(){
        setupStorage();
        setupViews();
        setupModels();
        setupLocus();
    }

}
