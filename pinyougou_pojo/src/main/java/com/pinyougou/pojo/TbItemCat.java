package com.pinyougou.pojo;

import java.io.Serializable;
import java.util.Map;

public class TbItemCat implements Serializable {
    private Long id;

    private Long parentId;

    private String name;

    private Long typeId;

    private Map<String,String> map;

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public TbItemCat() {
    }

    public TbItemCat(Long parentId, String name, Long typeId) {
        this.parentId = parentId;
        this.name = name;
        this.typeId = typeId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }
}