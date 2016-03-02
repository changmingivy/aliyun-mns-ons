//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.mobanker.tkj.cs.mns;

import java.io.Serializable;

public class Entity implements Serializable {
    private static final long serialVersionUID = -720807478055084231L;
    private String status;
    private String error;
    private String msg;
    private Object data;
    private String pageCount;

    public Entity() {
    }

    public Entity(String status) {
        this.status = status;
    }

    public Entity(String status, String error) {
        this.status = status;
        this.error = error;
    }

    public Entity(String status, Object data) {
        this.status = status;
        this.data = data;
    }

    public Entity(String status, Object data, String pageCount) {
        this.status = status;
        this.data = data;
        this.pageCount = pageCount;
    }

    public Entity(String status, String error, String msg, Object data) {
        this.status = status;
        this.error = error;
        this.msg = msg;
        this.data = data;
    }

    public String getStatus() {
        return this.status;
    }

    public Entity setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getError() {
        return this.error;
    }

    public Entity setError(String error) {
        this.error = error;
        return this;
    }

    public String getMsg() {
        return this.msg;
    }

    public Entity setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public Object getData() {
        return this.data;
    }

    public Entity setData(Object data) {
        this.data = data;
        return this;
    }

    public String getPageCount() {
        return this.pageCount;
    }

    public void setPageCount(String pageCount) {
        this.pageCount = pageCount;
    }

    public String toString() {
        return "ResponseEntity(super=" + super.toString() + ", status=" + this.getStatus() + ", error=" + this.getError() + ", msg=" + this.getMsg() + ", data=" + this.getData() + ", pageCount=" + this.getPageCount() + ")";
    }
}
