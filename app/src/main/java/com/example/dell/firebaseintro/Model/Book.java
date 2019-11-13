package com.example.dell.firebaseintro.Model;

public class Book {

    private String name;
    private String author;
    private String genre;
    private String price;
    private String id;
    private String bookid;
    private String condition;
    private String image;
    private String fname;
    private String phone;

    //TODO: add ISBN (optional)

    public Book()
    {

    }

    public Book(String name,String author, String genre, String condition, String price, String id, String image,String fname, String phone) {
        this.name = name;
        this.author = author;
        this.genre = genre;
        this.price = price;
        this.id = id;
        this.condition = condition;
        this.fname = fname;
        this.phone = phone;
        this.image= image;
    }




    public String getBookid() {
        return bookid;
    }

    public void setBookid(String bookid) {
        this.bookid = bookid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
