package com.teamtoast.toast.category;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @Column(name = "categoryId")
    @JsonProperty("categoryId")
    private int id;
    @Column(name = "categoryParent")
    @JsonProperty("categoryParent")
    private Integer parent;
    @Column(name = "categoryName")
    @JsonProperty("categoryName")
    private String name;
    @Column(name = "categoryImage")
    @JsonProperty("categoryImage")
    private String image;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getParent() {
        return parent;
    }

    public void setParent(Integer parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}