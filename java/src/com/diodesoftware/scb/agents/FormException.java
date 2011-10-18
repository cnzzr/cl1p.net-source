package com.diodesoftware.scb.agents;

public class FormException extends Exception{
    private String msg;

    public FormException(String msg){
        this.msg = msg;
    }


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
