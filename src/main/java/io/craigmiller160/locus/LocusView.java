package io.craigmiller160.locus;

import io.craigmiller160.locus.util.LocusStorage;

/**
 * Created by craig on 3/12/16.
 */
public class LocusView {

    //TODO There won't be any manual view property setting, but getting is necessary

    //TODO will primitive types work with the reflection, or will wrapper types be needed?
    //TODO consider if declaring the exceptions in the throws clause is really necessary, since it is a runtime exception

    private static final LocusStorage storage = LocusStorage.getInstance();

    LocusView(){}

    public void registerView(Object view){
        //TODO this is how the view instances are locked in
    }

    public void setInt(String propName, int value) throws LocusException{
        //TODO
    }

    public void setFloat(String propName, float value) throws LocusException{
        //TODO
    }

    public void setDouble(String propName, double value) throws LocusException{
        //TODO
    }

    public void setShort(String propName, short value) throws LocusException{
        //TODO
    }

    public void setByte(String propName, byte value) throws LocusException{
        //TODO
    }

    public void setLong(String propName, long value) throws LocusException{
        //TODO
    }

    public void setBoolean(String propName, boolean value) throws LocusException{
        //TODO
    }

    public void setString(String propName, String value) throws LocusException{
        //TODO
    }

    //TODO consider having convenience methods for interacting with collections & arrays

    public void setObject(String propName, Object value) throws LocusException{
        //TODO
    }

    public <T> void setValue(String propName, T value) throws LocusException{
        //TODO
    }

    //TODO what about multiple areas in a view displaying a value with the same name?

    public int getInt(String propName) throws LocusException{
        //TODO
        return 0;
    }

    public float getFloat(String propName) throws LocusException{
        //TODO
        return 0f;
    }

    public double getDouble(String propName) throws LocusException{
        //TODO
        return 0.0;
    }

    public short getShort(String propName) throws LocusException{
        //TODO
        return 0;
    }

    public byte getByte(String propName) throws LocusException{
        //TODO
        return 0;
    }

    public long getLong(String propName) throws LocusException{
        //TODO
        return 0;
    }

    public boolean getBoolean(String propName) throws LocusException{
        //TODO
        return false;
    }

    public String getString(String propName) throws LocusException{
        //TODO
        return "";
    }

    public Object getObject(String propName) throws LocusException{
        //TODO
        return null;
    }

    public <T> T getValue(String propName, Class<T> valueType) throws LocusException{
        //TODO
        return null;
    }

}
