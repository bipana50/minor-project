package com.example.dell.firebaseintro.Model;

public class BookStore {

    private String bookStoreName;
    private String bookStorePhone;
    private String bookStoreID;
    private String userID;
    private String longitude;
    private String latitude;

    public BookStore(){

    }

    public BookStore(String bookStoreName, String bookStorePhone, String bookStoreID, String userID, String longitude, String latitude) {
        this.bookStoreName = bookStoreName;
        this.bookStorePhone = bookStorePhone;
        this.bookStoreID = bookStoreID;
        this.longitude = longitude;
        this.latitude = latitude;
        this.userID = userID;
    }


    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
    public String getBookStoreName() {
        return bookStoreName;
    }

    public void setBookStoreName(String bookStoreName) {
        this.bookStoreName = bookStoreName;
    }

    public String getBookStorePhone() {
        return bookStorePhone;
    }

    public void setBookStorePhone(String bookStorePhone) {
        this.bookStorePhone = bookStorePhone;
    }

    public String getBookStoreID() {
        return bookStoreID;
    }

    public void setBookStoreID(String bookStoreID) {
        this.bookStoreID = bookStoreID;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

}
