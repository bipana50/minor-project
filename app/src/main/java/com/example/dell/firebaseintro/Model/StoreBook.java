package com.example.dell.firebaseintro.Model;

public class StoreBook {

    private String name;
    private String id;
    private String storeID;
    private String author;
    private String genre;
    private String pieces;
    private String cost;
    private String latitude;
    private String longitude;

    public StoreBook(){}

    public StoreBook(String latitude,String longitude,String name, String id, String storeID, String author, String genre, String pieces, String cost) {
        this.name = name;
        this.id = id;
        this.storeID = storeID;
        this.author = author;
        this.genre = genre;
        this.pieces = pieces;
        this.cost = cost;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStoreID() {
        return storeID;
    }

    public void setStoreID(String storeID) {
        this.storeID = storeID;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getPieces() {
        return pieces;
    }

    public void setPieces(String pieces) {
        this.pieces = pieces;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }



}
