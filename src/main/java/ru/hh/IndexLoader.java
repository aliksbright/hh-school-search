package ru.hh;

import java.io.*;

public class IndexLoader {
    private String path;
    public IndexLoader(String path){
        this.path = path;
    }
    public void saveIndex(Index index) {
        OutputStream ops = null;
        ObjectOutputStream objOps = null;
        try
        {
            ops = new FileOutputStream(path);
            objOps = new ObjectOutputStream(ops);
            objOps.writeObject(index);
            objOps.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try{
                if(objOps != null) objOps.close();
            } catch (Exception ex){

            }
        }
    }

    public Index loadIndex() {
        try(var file = new FileInputStream(path);
            var in = new ObjectInputStream(file))
        {
            // Method for deserialization of object
            var index = (Index)in.readObject();
            return index;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
