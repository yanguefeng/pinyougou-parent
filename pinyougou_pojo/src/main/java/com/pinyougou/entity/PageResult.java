package com.pinyougou.entity;

import java.io.Serializable;
import java.util.List;


/**
 *分页实体类
 */
public class PageResult implements Serializable {


    private Long total;//总记录数
    private List rows;//每页显示的记录数

    public PageResult(Long total, List rows) {
        this.total = total;
        this.rows = rows;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List getRows() {
        return rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }
}
