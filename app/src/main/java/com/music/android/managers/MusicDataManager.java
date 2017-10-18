package com.music.android.managers;

import java.util.ArrayList;

/**
 * Created by hui.lv on 2017/3/13.
 */

public class MusicDataManager {
    private static  ArrayList<String> list;
    private static  MusicDataManager manager;
    private MusicDataManager(){
        list=new ArrayList<>();
    }

    public static MusicDataManager getInstance(){
        if(manager==null){
            synchronized (MusicDataManager.class){
                manager=new MusicDataManager();
            }
        }
        return  manager;
    }

    public  void setMusics(ArrayList<String> musics) {
        list.clear();
        list.addAll(musics);
    }

    public  ArrayList<String> getMusics() {
       return list;
    }

}
