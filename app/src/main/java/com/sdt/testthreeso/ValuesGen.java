package com.sdt.testthreeso;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * @ClassName ValuesGen
 * @Description TODO
 * @Author biweiping
 * @Date 2020/12/23 14:41
 * @Version 1.0
 */
public class ValuesGen {
    private final static String TAG = "ValuesGen";

    final static String XML_HEADER = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
    final static String TAG_START = "<resources>";
    final static String TAG_END = "</resources>";
    final static String TEMPLATE = "    <dimen name=\"size_%d\">%spx</dimen>";
    final static int STAND_DIMEN = 1920;
    final static String FILENAME = "dimens.xml";
    final static DecimalFormat decimalFormat = new DecimalFormat("0.0");//构造方法的字符格式这里如果小数不足2位,会以0补足.

    final static String[] RES = {
            "values-640x360",
            "values-720x405",
            "values-800x450",
            "values-960x540",
            "values-1000x560",
            "values-1120x630",
            "values-1152x648",
            "values-1280x720",
            "values-1600x900",
            "values-1920x1008",
            "values-1920x1080",
            "values-2560x1440",
            "values-3840x2160",
            "values-7680x4320",
    };

    final static int ARRAY[] = {640, 720, 800, 960, 1000, 1120, 1152, 1280, 1600, 1920, 1920, 2560, 3840, 7680};

    public static void genAllXml(Context context) throws IOException {
        int len = RES.length;
        for (int i = 0; i < len; i++) {
            String dir = RES[i];
            getXml(context, ARRAY[i], dir);
        }

    }

    public static void getXml(Context context, int target, String dir) throws IOException {
        File xmlDir = new File(context.getExternalCacheDir(), dir);
        if (!xmlDir.exists()) {
            xmlDir.mkdirs();
        }
        File file = new File(xmlDir, FILENAME);
        Log.d(TAG, "File:" + file.getAbsolutePath());
        FileWriter fileWriter = new FileWriter(file, false);
        fileWriter.write(XML_HEADER);
        fileWriter.write("\n");
        fileWriter.write(TAG_START);
        fileWriter.write("\n");
        float r = 1.0f * target / STAND_DIMEN;
        for (int i = 1; i <= STAND_DIMEN; i++) {
            String line = String.format(TEMPLATE, i, decimalFormat.format(i * r));
            Log.d(TAG, line);
            fileWriter.write(line);
            fileWriter.write("\n");
        }
        fileWriter.write(TAG_END);
        fileWriter.write("\n");
        fileWriter.flush();
        fileWriter.close();
    }
}