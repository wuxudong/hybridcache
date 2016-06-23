package com.wandoujia.commons.cache.hybridcache.sample;

import java.io.Serializable;

/**
 * User: xudong
 * Date: 9/11/13
 * Time: 1:16 PM
 */
public class Pojo implements Serializable {
    private String name;
    private String blob;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBlob() {
        return blob;
    }

    public void setBlob(String blob) {
        this.blob = blob;
    }

    @Override
    public String toString() {
        return "Pojo{" +
                "name='" + name + '\'' +
                ", blob='" + blob + '\'' +
                '}';
    }
}
