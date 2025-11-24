package com.example.myisamm.model;

public class MaterialItem {
    private String id;
    private String name;
    private String type;
    private String storagePath;
    private String url;

    public MaterialItem() {
        // Default constructor for Firebase
    }


    public MaterialItem(String id, String name, String type, String storagePath, String url) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.storagePath = storagePath;
        this.url = url;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getStoragePath() { return storagePath; }
    public void setStoragePath(String storagePath) { this.storagePath = storagePath; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}