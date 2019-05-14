package com.github.lizlop.galleryApplication;

public class ColorHelper {
    public static double getDifference(LABColor color1, LABColor color2) {
        return getDifference(color1.getL(), color1.getA(), color1.getB(), color2.getL(), color2.getA(), color2.getB());
    }

    public static double getDifference(double L1, double a1, double b1, double L2, double a2, double b2){
        double averageC = (Math.sqrt(a1*a1+b1*b1)+Math.sqrt(a2*a2+b2*b2))/2;
        double G = 0.5*(1-Math.sqrt(Math.pow(averageC,7)/(Math.pow(averageC,7)+Math.pow(25,7))));

        double a1Hatch = (1+G)*a1; double a2Hatch = (1+G)*a2;
        double h1Hatch, h2Hatch;
        if (b1==0 && a1Hatch==0) h1Hatch = 0;
        else h1Hatch = Math.atan2(b1,a1Hatch)*180/Math.PI;
        if (h1Hatch<0) h1Hatch = 360+h1Hatch;
        if (b2==0 && a2Hatch==0) h2Hatch = 0;
        else h2Hatch = Math.atan2(b2,a2Hatch)*180/Math.PI;
        if (h2Hatch<0) h2Hatch = 360+h2Hatch;

        double C1Hatch = Math.sqrt(Math.pow(a1Hatch,2)+Math.pow(b1,2));
        double C2Hatch = Math.sqrt(Math.pow(a2Hatch,2)+Math.pow(b2,2));
        double averageCHatch = (C1Hatch+C2Hatch)/2;
        double averageLHatch = (L1+L2)/2;
        double averagehHatch;
        if (C1Hatch*C2Hatch==0) averagehHatch=h1Hatch+h2Hatch;
        else if (Math.abs(h1Hatch-h2Hatch)<=180) averagehHatch=(h1Hatch+h2Hatch)/2;
        else if (h1Hatch+h2Hatch<360) averagehHatch=(h1Hatch+h2Hatch+360)/2;
        else averagehHatch=(h1Hatch+h2Hatch-360)/2;

        double deltahHatch;
        if (C1Hatch*C2Hatch==0) deltahHatch=0;
        else if (Math.abs(h2Hatch-h1Hatch)<=180) deltahHatch=Math.abs(h2Hatch-h1Hatch);
        else deltahHatch=-Math.abs(h2Hatch-h1Hatch)+360;

        double deltaCHatch = Math.abs(C2Hatch-C1Hatch);
        double deltaLHatch = Math.abs(L2-L1);
        double deltaHHatch = 2*Math.sqrt(C1Hatch*C2Hatch)*Math.sin((deltahHatch/2)*Math.PI/180);

        double T = 1-0.17*Math.cos((averagehHatch-30)*Math.PI/180)+0.24*Math.cos((2*averagehHatch)*Math.PI/180)+0.32*Math.cos((3*averagehHatch+6)*Math.PI/180)-0.20*Math.cos((4*averagehHatch-63)*Math.PI/180);
        double deltaTeta = 30*Math.exp(-Math.pow((averagehHatch-275)/25,2));
        double Rc = 2*Math.sqrt(Math.pow(averageCHatch,7)/(Math.pow(averageCHatch,7)+Math.pow(25,7)));
        double Sl = 1+0.015*Math.pow(averageLHatch-50,2)/Math.sqrt(20+Math.pow(averageLHatch-50,2));
        double Sc = 1+0.045*averageCHatch;
        double Sh = 1+0.015*averageCHatch*T;
        double Rt = -Math.sin((2*deltaTeta)*Math.PI/180)*Rc;
        return Math.sqrt(Math.pow(deltaLHatch/(Sl),2)+Math.pow(deltaCHatch/(Sc),2)+Math.pow(deltaHHatch/(Sh),2)+Rt*(deltaCHatch/(Sc))*(deltaHHatch/(Sh)));
    }

    public static double getDifferenceWithoutL(LABColor c1, LABColor c2) {
        return getDifferenceWithoutL(c1.getA(),c1.getB(),c2.getA(),c2.getB());
    }
    public static double getDifferenceWithoutL(double a1, double b1, double a2, double b2) {
        return Math.sqrt(Math.pow(a2-a1,2)+Math.pow(b2-b1,2));
    }

    public static int labToRgb(double[] lab){
        return fromLinearRgb(xyzToLinearRgb(labToXyz(lab)));
    }
    public static int labToRgb(LABColor lab) {
        return labToRgb(new double[]{lab.getL(), lab.getA(), lab.getB()});
    }

    public static double[] labToXyz(double[] lab) {
        double[] whitePoint = new double[]{0.9505,1.0000,1.0890};
        double fy = (lab[0]+16)/116;
        double fx = fy + lab[1]/500;
        double fz = fy - lab[2]/200;
        return new double[]{reverseF(fx, whitePoint[0]), reverseF(fy,whitePoint[1]), reverseF(fz, whitePoint[2])};
    }
    static double reverseF(double f, double n){
        if (f > 6/29) return n*Math.pow(f,3);
        else return (f-16/116)*3*Math.pow(6/29, 2)*n;
    }

    public static double[] xyzToLinearRgb(double[] xyz){
        return new double[]{
                3.2406*xyz[0]-1.5372*xyz[1]-0.4986*xyz[2],
                -0.9689*xyz[0]+1.8758*xyz[1]+0.0415*xyz[2],
                0.0557*xyz[0]-0.2040*xyz[1]+1.0570*xyz[2]
        };
    }

    public static int fromLinearRgb(double[] linearRgb){
        double r = getGamut(linearRgb[0]);
        double g = getGamut(linearRgb[1]);
        double b = getGamut(linearRgb[2]);
        int sRGB = 0xff000000;
        sRGB = sRGB|((normalizeRGB(r)&0xff)<<16);
        sRGB = sRGB|((normalizeRGB(g)&0xff)<<8);
        sRGB = sRGB|(normalizeRGB(b)&0xff);
        return sRGB;
    }
    static int normalizeRGB(double color){
        if (color*255>255) return 255;
        else if (color*255<0) return 0;
        else return (int)(color*255);
    }
    static double getGamut(double color){
        if (color>0.0031308) return (1+0.055)*Math.pow(color, 1/2.4) - 0.055;
        else return 12.92*color;
    }

    public static LABColor rgbToLab(int RGB){
        return xyzToLab(linearRgbToXyz(toLinearRGB(RGB)));
    }

    public static LABColor xyzToLab(double X, double Y, double Z){
        double[] whitePoint = new double[]{0.9505,1.0000,1.0890};
        double L = 116*f(Y/whitePoint[1])-16;
        double a = 500*(f(X/whitePoint[0])-f(Y/whitePoint[1]));
        double b = 200*(f(Y/whitePoint[1])-f(Z/whitePoint[2]));
        return new LABColor(L,a,b);
    }
    public static LABColor xyzToLab(double[] XYZ){
        return xyzToLab(XYZ[0], XYZ[1], XYZ[2]);
    }
    static double f(double t){
        if (t>Math.pow(6.0/29,3)) return Math.pow(t,1.0/3);
        else return 1.0/3*t*Math.pow(29.0/6,2)+4.0/29;
    }

    public static double[] linearRgbToXyz(double R, double G, double B) {
        double X = 0.4124*R+0.3576*G+0.1805*B;
        double Y = 0.2126*R+0.7152*G+0.0722*B;
        double Z = 0.0193*R+0.1192*G+0.9505*B;
        return new double[]{X,Y,Z};
    }
    public static double[] linearRgbToXyz(double[] RGB) {
        return linearRgbToXyz(RGB[0], RGB[1], RGB[2]);
    }

    public static double[] toLinearRGB(int sRGB){
        double R = (sRGB >> 16) & 0xff;
        double G = (sRGB >>  8) & 0xff;
        double B = (sRGB) & 0xff;
        return toLinearRGB(R/255,G/255,B/255);
    }
    public static double[] toLinearRGB(double sRed, double sGreen, double sBlue){
        double linearRed = getReverseGamut(sRed);
        double linearGreen = getReverseGamut(sGreen);
        double linearBlue = getReverseGamut(sBlue);
        return new double[]{linearRed, linearGreen, linearBlue};
    }

    static double getReverseGamut(double color){
        if (color<=0.04045) return color/12.92;
        else return Math.pow(((color+0.055)/1.055),2.4);
    }
}
