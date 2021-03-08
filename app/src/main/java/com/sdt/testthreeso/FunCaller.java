package com.sdt.testthreeso;

public class FunCaller {

    static {
        System.loadLibrary("funcaller");
    }

    public native static int autoIncreasing(int data);

    public native static String encryptString(String src);

    public native static byte[] encryptBytes(byte[] src,int length);

    public native static char[] encryptChars(char[] src,int length);

    public native static short calcBit(short bit);

    public native static long calcMumber(long number);

    public native static float calcHeight(float height);

    public native static double calcDistance(double distance);

    public native static void writeFile(String path,String text);

    public native static String readText(String path);

    public native static int getRandomNum();
}
