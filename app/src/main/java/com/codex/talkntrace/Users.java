package com.codex.talkntrace;

/**
 * Created by Rutviz Vyas on 20-03-2017.
 */

class Users {
    public String name;
    public Long time;
    public String lastmsg;
    public String msg;
    public String email;
    public String photourl;
    public String mob;
    public String no;
    public String type;
    public String groupno;
    public String status;

    public Users(){}

    public Users(String name, String msg, String email, String photourl, String mob, String no) {
        this.name = name;
        this.msg = msg;
        this.email = email;
        this.photourl = photourl;
        this.mob = mob;
        this.no = no;
    }

    public Users(String name, String email, String photourl, String no, String mob) {
        this.name = name;
        this.email = email;
        this.photourl = photourl;
        this.mob = mob;
        this.no = no;

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Users(String name, String photourl, String groupno) {
        this.name = name;
        this.photourl = photourl;
        this.groupno = groupno;

        //  this.time = time;
    }

    public Users(String name, String mob, String email, String no) {
        this.name = name;
        this.email = email;
        this.mob = mob;
        this.no = no;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getGroupno() {
        return groupno;
    }

    public void setGroupno(String groupno) {
        this.groupno = groupno;
    }

    public Users(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhotourl() {
        return photourl;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
    }

    public String getMob() {
        return mob;
    }

    public void setMob(String mob) {
        this.mob = mob;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getLastmsg() {
        return lastmsg;
    }

    public void setLastmsg(String lastmsg) {
        this.lastmsg = lastmsg;
    }
}
