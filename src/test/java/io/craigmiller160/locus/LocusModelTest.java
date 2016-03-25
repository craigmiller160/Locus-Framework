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

import io.craigmiller160.locus.reflect.ObjectAndMethod;
import io.craigmiller160.locus.sample.ModelOne;
import io.craigmiller160.locus.util.LocusStorage;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * A JUnit test class for the LocusModel class.
 *
 * Created by craig on 3/24/16.
 */
public class LocusModelTest {

    private static LocusStorage storage;
    private static LocusModel locusModel;
    private static ModelOne modelOne;

    static{
        try{
            Constructor<LocusStorage> constructor = LocusStorage.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            storage = constructor.newInstance();

            modelOne = new ModelOne();
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
            }

            locusModel = new LocusModel(storage);
        }
        catch(Exception ex){
            throw new RuntimeException("Fatal exception at initialization", ex);
        }
    }

    /**
     * Test setting a String.
     */
    @Test
    public void testSetString(){
        locusModel.setString("StringField", "Value");
        assertEquals("Invalid StringField value", modelOne.getStringField(), "Value");
    }

    /**
     * Test setting an int.
     */
    @Test
    public void testSetInt(){
        locusModel.setInt("IntField", 101);
        assertEquals("Invalid IntField value", modelOne.getIntField(), 101);
    }

    /**
     * Test setting a float.
     */
    @Test
    public void testSetFloat(){
        locusModel.setFloat("FloatField", 1.1f);
        assertTrue("Invalid FloatField value", modelOne.getFloatField() == 1.1f);
    }

    /**
     * Test setting a double.
     */
    @Test
    public void testSetDouble(){
        locusModel.setDouble("DoubleField", 1.1d);
        assertTrue("Invalid DoubleField value", modelOne.getDoubleField() == 1.1d);
    }

    /**
     * Test setting a short.
     */
    @Test
    public void testSetShort(){
        locusModel.setShort("ShortField", (short)1);
        assertTrue("Invalid ShortField value", modelOne.getShortField() == (short) 1);
    }

    /**
     * Test setting a byte.
     */
    @Test
    public void testSetByte(){
        locusModel.setByte("ByteField", (byte) 11);
        assertTrue("Invalid ByteField value", modelOne.getByteField() == (byte) 11);
    }

    /**
     * Test setting a long.
     */
    @Test
    public void testSetLong(){
        locusModel.setLong("LongField", 1234567890L);
        assertTrue("Invalid LongField value", modelOne.getLongField() == 1234567890L);
    }

    /**
     * Test setting a boolean.
     */
    @Test
    public void testSetBoolean(){
        locusModel.setBoolean("BooleanField", true);
        assertTrue("Invalid BooleanField value", modelOne.getBooleanField());
    }

    /**
     * Test setting a character.
     */
    @Test
    public void testSetCharacter(){
        locusModel.setCharacter("CharField", 'a');
        assertEquals("Invalid CharField value", modelOne.getCharField(), 'a');
    }

    /**
     * Test setting an object.
     */
    @Test
    public void testSetObject(){
        locusModel.setObject("ObjectField", new Object());
        assertNotNull("Invalid ObjectField value", modelOne.getObjectField());
    }

    /**
     * Test setting a value using setObject(...),
     * but with a different object type to test
     * inheritance.
     */
    @Test
    public void testSetWithInheritance(){
        BigDecimal bigDecimal = new BigDecimal(5.12345);
        locusModel.setObject("ObjectField", bigDecimal);
        assertEquals("Invalid ObjectField value", modelOne.getObjectField(), bigDecimal);
    }

    /**
     * Test setValue(...) with the generic type.
     */
    @Test
    public void testGenericSet(){
        BigDecimal bigDecimal = new BigDecimal(5.12345);
        locusModel.setValue("ObjectField", bigDecimal);
        assertEquals("Invalid ObjectField value", modelOne.getObjectField(), bigDecimal);
    }

    /**
     * Test getting an int value.
     */
    @Test
    public void testGetInt(){
        modelOne.setIntField(123);
        assertTrue("Invalid IntField value", locusModel.getInt("IntField") == 123);
    }

    /**
     * Test getting a float value.
     */
    @Test
    public void testGetFloat(){
        modelOne.setFloatField(22.2f);
        assertTrue("Invalid FloatField value", locusModel.getFloat("FloatField") == 22.2f);
    }

    /**
     * Test getting a double value.
     */
    @Test
    public void testGetDouble(){
        modelOne.setDoubleField(33.3d);
        assertTrue("Invalid DoubleField value", locusModel.getDouble("DoubleField") == 33.3d);
    }

    /**
     * Test getting a short value.
     */
    @Test
    public void testGetShort(){
        modelOne.setShortField((short) 5);
        assertTrue("Invalid ShortField value", locusModel.getShort("ShortField") == (short) 5);
    }

    /**
     * Test getting a byte value.
     */
    @Test
    public void testGetByte(){
        modelOne.setByteField((byte)456);
        assertTrue("Invalid ByteField value", locusModel.getByte("ByteField") == (byte) 456);
    }

    /**
     * Test getting a long value.
     */
    @Test
    public void testGetLong(){
        modelOne.setLongField(9876543210L);
        assertTrue("Invalid LongField value", locusModel.getLong("LongField") == 9876543210L);
    }

    /**
     * Test getting a boolean value.
     */
    @Test
    public void testGetBoolean(){
        //If testSetBoolean runs before this, then the field will already be true
        //If nothing has run, it'll be false by default
        if(!modelOne.getBooleanField()){
            modelOne.setBooleanField(true);
        }

        assertTrue("Invalid BooleanField value", locusModel.getBoolean("BooleanField"));
    }

    /**
     * Test getting a String value.
     */
    @Test
    public void testGetString(){
        modelOne.setStringField("AnotherValue");
        assertEquals("Invalid StringField value", locusModel.getString("StringField"), "AnotherValue");
    }

    /**
     * Test getting a Character value.
     */
    @Test
    public void testGetCharacter(){
        modelOne.setCharField('z');
        assertTrue("Invalid CharField value", locusModel.getCharacter("CharField") == 'z');
    }

    /**
     * Test getting an Object value.
     */
    @Test
    public void setGetObject(){
        //If testSetObject has run before this, then the field won't be null.
        //If nothing has run, it'll be null by default
        if(modelOne.getObjectField() == null){
            modelOne.setObjectField(new Object());
        }

        assertNotNull("Invalid ObjectField value", locusModel.getObject("ObjectField"));
    }

    /**
     * Test getting an Object, where the value returned
     * is a subclass of object.
     */
    @Test
    public void testGetWithInheritance(){
        BigDecimal bigDecimal = new BigDecimal(567890.1234);
        modelOne.setObjectField(bigDecimal);
        assertEquals("Invalid ObjectField value", locusModel.getObject("ObjectField"), bigDecimal);
    }

    /**
     * Test getting a value with the generic method.
     */
    @Test
    public void testGenericGet(){
        BigDecimal bigDecimal = new BigDecimal(543210.6789);
        modelOne.setObjectField(bigDecimal);
        assertEquals("Invalid class type returned",
                locusModel.getValue("ObjectField", BigDecimal.class).getClass(),
                BigDecimal.class);
        assertEquals("Invalid ObjectField value", locusModel.getValue("ObjectField", BigDecimal.class), bigDecimal);
    }

}
