package com.in.talkey.helper;


public class Response<T>{

    private T data;
    private String message;
    private String  status;

    public Response() {
    }

    public Response(T data, String message, String status) {
        this.data = data;
        this.message = message;
        this.status = status;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
