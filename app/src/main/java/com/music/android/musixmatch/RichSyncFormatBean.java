package com.music.android.musixmatch;

import java.util.List;

/**
 * Created by CTer on 17/5/9.
 */

public class RichSyncFormatBean {


    /**
     * ts : 16.426
     * te : 18.261
     * l : [{"c":"I","o":0},{"c":"'","o":0.013},{"c":"m","o":0.025},{"c":" ","o":0.088},{"c":"t","o":0.114},{"c":"r","o":0.137},{"c":"y","o":0.163},{"c":"n","o":0.213},{"c":"a","o":0.276},{"c":" ","o":0.299},{"c":"p","o":0.311},{"c":"u","o":0.348},{"c":"t","o":0.386},{"c":" ","o":0.399},{"c":"y","o":0.399},{"c":"o","o":0.424},{"c":"u","o":0.45},{"c":" ","o":0.489},{"c":"i","o":0.501},{"c":"n","o":0.512},{"c":" ","o":0.549},{"c":"t","o":0.562},{"c":"h","o":0.587},{"c":"e","o":0.626},{"c":" ","o":0.688},{"c":"w","o":0.7},{"c":"o","o":0.813},{"c":"r","o":0.888},{"c":"s","o":0.911},{"c":"t","o":0.963},{"c":" ","o":1.026},{"c":"m","o":1.076},{"c":"o","o":1.212},{"c":"o","o":1.288},{"c":"d","o":1.386},{"c":",","o":1.489},{"c":" ","o":1.512},{"c":"a","o":1.563},{"c":"h","o":1.65}]
     * x : I'm tryna put you in the worst mood, ah
     */

    private double ts;
    private double te;
    private String x;
    private List<LBean> l;

    public double getTs() {
        return ts;
    }

    public void setTs(double ts) {
        this.ts = ts;
    }

    public double getTe() {
        return te;
    }

    public void setTe(double te) {
        this.te = te;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public List<LBean> getL() {
        return l;
    }

    public void setL(List<LBean> l) {
        this.l = l;
    }

    public static class LBean {
        /**
         * c : I
         * o : 0
         */

        private String c;
        private int o;

        public String getC() {
            return c;
        }

        public void setC(String c) {
            this.c = c;
        }

        public int getO() {
            return o;
        }

        public void setO(int o) {
            this.o = o;
        }
    }
}
