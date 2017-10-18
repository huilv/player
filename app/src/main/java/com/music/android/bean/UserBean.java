package com.music.android.bean;


/**
 * Created by liuyun on 17/3/6.
 */

public class UserBean extends CommonUserBean {
    public String avatar_url;
    public int id;
    public String kind;
    public String permalink_url;
    public String uri;
    public String permalink;
    public String last_modified;

    @Override
    public String toString() {
        return "UserBean{" +
                "avatar_url='" + avatar_url + '\'' +
                ", id=" + id +
                ", kind='" + kind + '\'' +
                ", permalink_url='" + permalink_url + '\'' +
                ", uri='" + uri + '\'' +
                ", permalink='" + permalink + '\'' +
                ", last_modified='" + last_modified + '\'' +
                '}';
    }
}
