package com.codex.talkntrace;

/**
 * Created by ADMIN on 01-04-2017.
 */

public class UsersGroup {


    public String name;
    public String photourl;
    public String groupno;
    public String type;

    public String email;
    public String mob;
    public String no;



    public UsersGroup()
    {

    }

    public UsersGroup( String email, String mob,String name, String no, String photourl) {
        this.name = name;
        this.photourl = photourl;
        this.email = email;
        this.mob = mob;
        this.no = no;
    }

    public UsersGroup(String groupno, String name, String photourl, String type) {
        this.groupno = groupno;
        this.name = name;
        this.photourl = photourl;
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotourl() {
        return photourl;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
    }

    public String getGroupno() {
        return groupno;
    }

    public void setGroupno(String groupno) {
        this.groupno = groupno;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
