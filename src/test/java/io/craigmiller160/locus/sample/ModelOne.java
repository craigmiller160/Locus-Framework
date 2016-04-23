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

package io.craigmiller160.locus.sample;

import io.craigmiller160.locus.annotations.LModel;

import java.util.ArrayList;
import java.util.List;


/**
 * A sample Model class to use for testing
 * this framework.
 *
 * Created by craig on 3/16/16.
 */
@LModel
public class ModelOne {

    public static final int STRING_FIELD = 1;
    public static final int INT_FIELD = 2;
    public static final int DOUBLE_FIELD = 3;

    private String stringField;
    private int intField;
    private float floatField;
    private double doubleField;
    private short shortField;
    private byte byteField;
    private long longField;
    private boolean booleanField;
    private char charField;
    private Object objectField;

    private List<String> strings = new ArrayList<>();

    public String getStringField() {
        return stringField;
    }

    public void setStringField(String stringField) {
        this.stringField = stringField;
    }

    public void setStringField(int stringField){
        this.stringField = "" + stringField;
    }

    public int getIntField() {
        return intField;
    }

    public void setIntField(int intField) {
        this.intField = intField;
    }

    public void setFields(Object...fields){
        if(fields.length > 0){
            stringField = (String) fields[0];
        }

        if(fields.length > 1){
            intField = (Integer) fields[1];
        }
    }

    public float getFloatField() {
        return floatField;
    }

    public void setFloatField(float floatField) {
        this.floatField = floatField;
    }

    public double getDoubleField() {
        return doubleField;
    }

    public void setDoubleField(double doubleField) {
        this.doubleField = doubleField;
    }

    public short getShortField() {
        return shortField;
    }

    public void setShortField(short shortField) {
        this.shortField = shortField;
    }

    public byte getByteField() {
        return byteField;
    }

    public void setByteField(byte byteField) {
        this.byteField = byteField;
    }

    public long getLongField() {
        return longField;
    }

    public void setLongField(long longField) {
        this.longField = longField;
    }

    //Testing that is... methods get picked up
    public boolean isBooleanField() {
        return booleanField;
    }

    public void setBooleanField(boolean booleanField) {
        this.booleanField = booleanField;
    }

    public char getCharField() {
        return charField;
    }

    public void setCharField(char charField) {
        this.charField = charField;
    }

    public Object getField(int fieldNumber){
        Object result = null;
        switch(fieldNumber){
            case 1:
                result = stringField;
                break;
            case 2:
                result = intField;
                break;
        }

        return result;
    }

    public void setThreeFields(String stringField, int intField, double doubleField){
        this.stringField = stringField;
        this.intField = intField;
        this.doubleField = doubleField;
    }

    public Object[] getMultipleFields(int...fields){
        Object[] result = new Object[fields.length];
        for(int i = 0; i < fields.length; i++){
            switch(fields[i]){
                case STRING_FIELD:
                    result[i] = stringField;
                    break;
                case INT_FIELD:
                    result[i] = intField;
                    break;
                case DOUBLE_FIELD:
                    result[i] = doubleField;
                    break;
            }
        }

        return result;
    }

    public Object getObjectField() {
        return objectField;
    }

    public void setObjectField(Object objectField) {
        this.objectField = objectField;
    }

    public void addString(String s){
        strings.add(s);
    }

    public void removeString(String s){
        strings.remove(s);
    }

    public String getString(int i){
        return strings.get(i);
    }
}
