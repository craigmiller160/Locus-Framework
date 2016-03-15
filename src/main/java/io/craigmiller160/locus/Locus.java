package io.craigmiller160.locus;

import io.craigmiller160.locus.util.LocusStorage;

/**
 * The central class of the Locus Framework.
 * This class provides easy, static access to
 * manipulate system resources through its three
 * subclasses.
 *
 * Created by craig on 3/12/16.
 */
public class Locus {

    //TODO this needs to be made totally thread safe

    static final int GETTER = 101;
    static final int SETTER = 102;

    private static final LocusStorage storage = LocusStorage.getInstance();

    public static LocusModel model = new LocusModel();

    public static LocusController controller = new LocusController();

    public static LocusView view = new LocusView();

    public static void initialize(){
        //TODO critical method that needs to be finished
        //TODO this must validate that every model setter/getter is unique
        //TODO this must also validate that every view getter is unique
    }

}
