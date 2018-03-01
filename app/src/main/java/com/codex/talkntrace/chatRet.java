package com.codex.talkntrace;

/**
 * Created by Rutviz Vyas on 21-03-2017.
 */

public class chatRet {

    public String sender;
    public String msg;
    public Long time;
    public String type;

/*
    public chatRet(String sender, String msg, Long time) {
        this.sender = sender;
        this.msg = msg;
        this.time = time;
        this.type = "0";
    }*/

    public chatRet(String msg, String sender, Long time, String type) {
        this.sender = sender;
        this.msg = msg;
        this.time = time;
        this.type = type;
    }

    public chatRet() {
        type="0";
        time=0L;
        sender="rutvizvyas27@gmail.com";
    }

    public chatRet(String msg, String type) {
        this.msg = msg;
        sender="rutvizvyas27@gmail.com";
        time=0L;
        this.type = type;

    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
