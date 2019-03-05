package com.github.lizlop.galleryApplication;

public class ColorHelper {
    public static double getDifference(LABColor c1, LABColor c2) {
        return getDifference(c1.getL(),c1.getA(),c1.getB(),c2.getL(),c2.getA(),c2.getB());
    }
    public static double getDifference(double L1, double a1, double b1, double L2, double a2, double b2) {
        return Math.sqrt(Math.pow(a2-a1,2)+Math.pow(L2-L1,2)+Math.pow(b2-b1,2));
    }
    public static double getDifference(int sRGB1, int sRGB2){
        return getDifference(getLab(getXYZ(getLinearRGB(sRGB1))),getLab(getXYZ(getLinearRGB(sRGB2))));
    }

    public static LABColor getLab(double X, double Y, double Z){
        double[] whitePoint = new double[]{0.9505,1.0000,1.0890};
        double L = 116*f(Y/whitePoint[1])-16;
        double a = 500*(f(X/whitePoint[0])-f(Y/whitePoint[1]));
        double b = 200*(f(Y/whitePoint[1])-f(Z/whitePoint[2]));
        return new LABColor(L,a,b);
    }
    public static LABColor getLab(double[] XYZ){
        return getLab(XYZ[0], XYZ[1], XYZ[2]);
    }
    static double f(double t){
        if (t>Math.pow(6.0/29,3)) return Math.pow(t,1.0/3);
        else return 1.0/3*t*Math.pow(29.0/6,2)+4.0/29;
    }

    public static double[] getXYZ(double R, double G, double B) {
        double X = 0.4124*R+0.3576*G+0.1805*B;
        double Y = 0.2126*R+0.7152*G+0.0722*B;
        double Z = 0.0193*R+0.1192*G+0.9505*B;
        return new double[]{X,Y,Z};
    }
    public static double[] getXYZ(double[] RGB) {
        return getXYZ(RGB[0], RGB[1], RGB[2]);
    }

    public static double[] getLinearRGB(int sRGB){
        double R = (sRGB >> 16) & 0xff;
        double G = (sRGB >>  8) & 0xff;
        double B = (sRGB) & 0xff;
        return getLinearRGB(R/255,G/255,B/255);
    }
    public static double[] getLinearRGB(double sRed, double sGreen, double sBlue){
        double linearRed = Math.pow(sRed, getReverseGamut(sRed));
        double linearGreen = Math.pow(sGreen, getReverseGamut(sGreen));
        double linearBlue = Math.pow(sBlue, getReverseGamut(sBlue));
        return new double[]{linearRed, linearGreen, linearBlue};
    }

    static double getReverseGamut(double color){
        if (color<0.04045) return color/12.92;
        else return Math.pow(((color+0.055)/1.055),2.4);
    }
}
