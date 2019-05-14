package com.github.lizlop.galleryApplication;

import android.util.SparseIntArray;

public class LABColor {
    private int lab;
    private double a;
    private double b;
    private SparseIntArray l;

    LABColor(double l, double a, double b){
        this.a = a;
        this.b = b;
        this.l = new SparseIntArray();
        this.l.put((int) l, 1);
    }

    public double getL() {
        return l.keyAt(l.size()/2);
    }
    public double getA() {
        return a;
    }
    public double getB() {
        return b;
    }
    public void setL(double l){
        int count = this.l.get((int) l);
        this.l.put((int) l, count<0?1:count+1);
    }
    public int getLab() {
        double a = Math.round(this.a) < 0 ? 127 - Math.round(this.a) : Math.round(this.a);
        double b = Math.round(this.b) < 0 ? 127 - Math.round(this.b) : Math.round(this.b);
        this.lab = (l.keyAt(l.size()/2)&0xff)<<16 | (((int)(a))&0xff)<<8 | (((int)(b))&0xff);
        return lab;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return  true;
        return false;
    }
}
