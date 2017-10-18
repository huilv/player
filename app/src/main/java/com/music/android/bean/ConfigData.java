package com.music.android.bean;

/**
 * Created by CTer on 17/4/17.
 */

public class ConfigData {

    /**
     * code : 0
     * msg :
     * data : {"file_ver":"201701231800","admob_id":"ca-app-pub-3940256099942544/1033173712","search_back":false,"enter_collection":false,"play_btn":false,"play_list_back":false,"list_play_btn":false}
     */

    private int code;
    private String msg;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        public String last_version;
    }
}
