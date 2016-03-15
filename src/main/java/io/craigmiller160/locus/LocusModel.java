package io.craigmiller160.locus;

import io.craigmiller160.locus.reflect.LocusInvoke;
import io.craigmiller160.locus.reflect.LocusReflectiveException;
import io.craigmiller160.locus.reflect.ObjectAndMethod;
import io.craigmiller160.locus.util.LocusStorage;

/**
 * Created by craig on 3/12/16.
 */
public class LocusModel {

    private static final LocusStorage storage = LocusStorage.getInstance();

    LocusModel(){}

    public void setInt(String propName, int... value) throws LocusException{
        setValue(propName, value);
    }

    public void setFloat(String propName, float value) throws LocusException{
        setValue(propName, value);
    }

    public void setDouble(String propName, double value) throws LocusException{
        setValue(propName, value);
    }

    public void setShort(String propName, short value) throws LocusException{
        setValue(propName, value);
    }

    public void setByte(String propName, byte value) throws LocusException{
        setValue(propName, value);
    }

    public void setLong(String propName, long value) throws LocusException{
        setValue(propName, value);
    }

    public void setBoolean(String propName, boolean value) throws LocusException{
        setValue(propName, value);
    }

    public void setString(String propName, String value) throws LocusException{
        setValue(propName, value);
    }

    //TODO consider having convenience methods for interacting with collections & arrays

    public void setObject(String propName, Object value) throws LocusException{
        setValue(propName, value);
    }

    public <T> void setValue(String propName, T value) throws LocusException{
        ObjectAndMethod oam = getMethod(propName, Locus.SETTER);
        LocusInvoke.invokeMethod(oam, value);
    }

    public int getInt(String propName) throws LocusException{
        return getValue(propName, Integer.class);
    }

    public float getFloat(String propName) throws LocusException{
        return getValue(propName, Float.class);
    }

    public double getDouble(String propName) throws LocusException{
        return getValue(propName, Double.class);
    }

    public short getShort(String propName) throws LocusException{
        return getValue(propName, Short.class);
    }

    public byte getByte(String propName) throws LocusException{
        return getValue(propName, Byte.class);
    }

    public long getLong(String propName) throws LocusException{
        return getValue(propName, Long.class);
    }

    public boolean getBoolean(String propName) throws LocusException{
        return getValue(propName, Boolean.class);
    }

    public String getString(String propName) throws LocusException{
        return getValue(propName, String.class);
    }

    public Object getObject(String propName) throws LocusException{
        return LocusInvoke.invokeMethod(getMethod(propName, Locus.GETTER));
    }

    public <T> T getValue(String propName, Class<T> valueType) throws LocusException{
        Object result = getObject(propName);
        if(!(valueType.isAssignableFrom(result.getClass()))){
            throw new LocusInvalidTypeException("Return value for getting \"" + propName +
                    "\" doesn't match expected type. Expected: " + valueType.getName() +
                    " Actual: " + result.getClass().getName());
        }
        return (T) result;
    }

    private ObjectAndMethod getMethod(String propName, int methodType){
        ObjectAndMethod oam = null;
        String typeName = "";
        if(methodType == Locus.GETTER){
            oam = storage.getModelPropGetter(propName);
            typeName = "getter";
        }
        else{
            oam = storage.getModelPropSetter(propName);
            typeName = "setter";
        }

        if(oam == null){
            throw new LocusReflectiveException("No model " + typeName + " found matching the property name \"" + propName + "\"");
        }
        return oam;
    }
}
