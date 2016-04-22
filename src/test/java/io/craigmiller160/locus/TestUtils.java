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

import io.craigmiller160.locus.concurrent.NoUIThreadExecutor;
import io.craigmiller160.locus.concurrent.UIThreadExecutorFactory;
import io.craigmiller160.locus.othercontroller.ControllerTwo;
import io.craigmiller160.locus.sample.ControllerOne;
import io.craigmiller160.locus.sample.ModelOne;
import io.craigmiller160.locus.sample.ViewOne;
import io.craigmiller160.locus.sample.ViewThree;
import io.craigmiller160.locus.util.LocusStorage;
import io.craigmiller160.utils.reflect.ClassAndMethod;
import io.craigmiller160.utils.reflect.ObjectAndMethod;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * A class with some common methods and code
 * used to configure many of the tests here.
 *
 * Created by craigmiller on 4/21/16.
 */
public class TestUtils {

    public static final String CONTROLLER_ONE_NAME = "ControllerOne";
    public static final String CONTROLLER_TWO_NAME = "ControllerTwo";

    public static LocusStorage setupStorage(){
        LocusStorage storage = null;
        try{
            Constructor<LocusStorage> constructor = LocusStorage.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            storage = constructor.newInstance();
            storage.setUIThreadExecutorType(NoUIThreadExecutor.class);
        }
        catch(Exception ex){
            throw new RuntimeException("Fatal exception while trying to construct LocusStorage for test", ex);
        }

        return storage;
    }

    public static UIThreadExecutorFactory setupUIThreadExecutor(LocusStorage storage){
        UIThreadExecutorFactory factory = null;
        try{
            Constructor<UIThreadExecutorFactory> constructor = UIThreadExecutorFactory.class.getDeclaredConstructor(LocusStorage.class);
            constructor.setAccessible(true);
            factory = constructor.newInstance(storage);
        }
        catch(Exception ex){
            throw new RuntimeException("Fatal exception while trying to construct LocusStorage for test", ex);
        }

        return factory;
    }

    public static Map<Class<?>, Object> setupModels(LocusStorage storage){
        ModelOne modelOne = new ModelOne();
        Method[] methods = ModelOne.class.getDeclaredMethods();
        for(Method m : methods){
            if(m.getName().startsWith("set")){
                String propName = m.getName().substring(3);
                storage.addModelPropSetter(propName, new ObjectAndMethod(modelOne, m));
            }
            else if(m.getName().startsWith("get")){
                String propName = m.getName().substring(3);
                storage.addModelPropGetter(propName, new ObjectAndMethod(modelOne, m));
            }
            else if(m.getName().startsWith("is")){
                String propName = m.getName().substring(2);
                storage.addModelPropGetter(propName, new ObjectAndMethod(modelOne, m));
            }
        }

        Map<Class<?>,Object> models = new HashMap<>();
        models.put(modelOne.getClass(), modelOne);

        return models;
    }

    public static Map<Class<?>,Object> setupViews(LocusStorage storage){
        ViewOne viewOne = new ViewOne();
        ViewThree viewThree = new ViewThree();

        Method[] v1methods = ViewOne.class.getDeclaredMethods();
        Method[] v3methods = ViewThree.class.getDeclaredMethods();

        for(Method m : v1methods){
            if(m.getName().startsWith("set")){
                String propName = m.getName().substring(3);
                storage.addViewPropSetter(propName, new ClassAndMethod(ViewOne.class, m));
            }
        }

        for(Method m : v3methods){
            if(m.getName().startsWith("set")){
                String propName = m.getName().substring(3);
                storage.addViewPropSetter(propName, new ClassAndMethod(ViewThree.class, m));
            }
        }

        storage.addViewInstance(viewOne.getClass(), viewOne);
        storage.addViewInstance(viewThree.getClass(), viewThree);

        Map<Class<?>,Object> views = new HashMap<>();
        views.put(viewOne.getClass(), viewOne);
        views.put(viewThree.getClass(), viewThree);

        return views;
    }

    public static void setupControllers(LocusStorage storage){
        storage.addControllerType("ControllerOne", ControllerOne.class, false);
        storage.addControllerType("ControllerTwo", ControllerTwo.class, true);
    }



}
