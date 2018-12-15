package bgu.spl.mics.application.passiveObjects;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;

/**
 * The whole purpose of this class to make it simple to edit how we are printing the serialized objects to the file
 * @param <T>
 */

public class Printer<T> {

    private String filename;
    private T obj;

    public Printer(String _filename, T _obj){
        filename = _filename;
        obj = _obj;
    }

    public void print() {
        synchronized (this) {
            try {
                FileOutputStream fileOutput =
                        new FileOutputStream(filename);
                ObjectOutputStream out = new ObjectOutputStream(fileOutput);
                out.writeObject(obj);
                out.close();
                fileOutput.close();
            } catch (IOException i) {
                i.printStackTrace();
            }
        }
    }
}
