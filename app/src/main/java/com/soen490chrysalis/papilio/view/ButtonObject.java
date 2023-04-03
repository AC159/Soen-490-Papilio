package com.soen490chrysalis.papilio.view;

public class ButtonObject {
    private int id;
    private String name;
    private String url;
    private String category;
    private String createdAt;
    private String updatedAt;

    public ButtonObject(int id, String name, String url, String category, String createdAt, String updatedAt) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.category = category;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getCategory() {
        return category;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}