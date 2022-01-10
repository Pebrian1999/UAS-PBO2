package model;

import db.koneksi;

public class Penerbit {
    private int id;
    private String penerbit;
    
    public Penerbit(){
        
    }
    
    public Penerbit(int id, String penerbit){
        this.id = id;
        this.penerbit = penerbit;
    }

    public Penerbit(int aInt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public int getId(){
        return id;
    }
    
    public void setId(int id){
        this.id = id;
    }
    
    public String getPenerbit(){
        return penerbit;
    } 
    
    public void setPenerbit(String penerbit){
        this.penerbit = penerbit;
    }
}
