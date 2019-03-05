package com.github.lizlop.galleryApplication;

import java.util.Comparator;

import static com.github.lizlop.galleryApplication.ColorHelper.*;

public class LABColor {
    private double l;
    private double a;
    private double b;
    private int count=0;
    static private double dif=2.3;

    LABColor(double l, double a, double b){
        this.l = l;
        this.a = a;
        this.b = b;
        setCount(1);
    }

    public static void setDif(double dif) {
        LABColor.dif = dif;
    }
    public static double getDif() {
        return dif;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
    public void increaseCount(int count){
        this.count+=count;
    }
    public void setL(double l) {
        this.l = l;
    }
    public double getL() {
        return l;
    }
    public void setA(double a) {
        this.a = a;
    }
    public double getA() {
        return a;
    }
    public void setB(double b) {
        this.b = b;
    }
    public double getB() {
        return b;
    }

    public static class SortColors implements Comparator<LABColor>{

        @Override
        public int compare(LABColor o1, LABColor o2) {
            if(o1.count==o2.count){
            if (o1.a==o1.b) {
                if (o1.b==o2.b) return o1.l>o2.l?1:-1;
                else return o1.b>o2.b?1:-1;
            }
            else return o1.a>o2.a?1:-1;}
            else return o2.count-o1.count;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass()==getClass()) {
            LABColor c = (LABColor)obj;
            if ((this.a==c.a)&&(this.b==c.b)&&(this.l==c.l)) return true;
            else return getDifference(this, c) < getDif();
        }
        else return false;
    }
}
