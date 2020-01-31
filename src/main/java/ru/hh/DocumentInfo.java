package ru.hh;

import java.io.Serializable;
import java.util.ArrayList;

public class DocumentInfo implements Serializable {
    private int Count;
    private ArrayList<Integer> positions;

    public DocumentInfo(){
        this.positions = new ArrayList<>();
    }

    public void addPosition(int position){
        this.positions.add(position);
        this.Count ++;
    }

    public ArrayList<Integer> GetPositions(){
        return positions;
    }

    public int getCount(){
        return Count;
    }
}
