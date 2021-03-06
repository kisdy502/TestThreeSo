//
// Created by Administrator on 2020/7/27.
//

#include <iostream>
#include <fstream>
#include <string>

#include <jni.h>
#include <Android/log.h>
#include "com_sdt_testthreeso_FunCaller.h"

#include <Android/log.h>

#define  LOG    "JNILOG"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG,__VA_ARGS__)
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG,__VA_ARGS__)
#define  LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG,__VA_ARGS__)
#define  LOGF(...)  __android_log_print(ANDROID_LOG_FATAL,LOG,__VA_ARGS__)

using namespace std;


jstring CStr2Jstring(JNIEnv *env, const char *pat);

extern "C"
JNIEXPORT jint
JNICALL
Java_com_sdt_testthreeso_FunCaller_autoIncreasing(JNIEnv *env, jclass clazz, jint data) {
    // TODO: implement autoIncreasing()
    data++;
    LOGI("autoIncreasing:%d", data);
    return data;
}

extern "C"
JNIEXPORT jstring
JNICALL
Java_com_sdt_testthreeso_FunCaller_encryptString(JNIEnv *env, jclass clazz, jstring src) {
    char *data = const_cast<char *>(env->GetStringUTFChars(src, 0));
    LOGI("autoIncreasing:%s", data);
    env->ReleaseStringUTFChars(src, data);
    return src;
}

extern "C"
JNIEXPORT jbyteArray
JNICALL
Java_com_sdt_testthreeso_FunCaller_encryptBytes(JNIEnv *env, jclass clazz, jbyteArray src,
                                                jint length) {
    jbyte *data = env->GetByteArrayElements(src, 0);
    int i;
    for (i = 0; i < length; i++) {
        LOGI("encryptBytes:%d", data[i]);
    }
    env->ReleaseByteArrayElements(src, data, 0);
    return src;
}

extern "C"
JNIEXPORT jcharArray
JNICALL
Java_com_sdt_testthreeso_FunCaller_encryptChars(JNIEnv *env, jclass clazz, jcharArray src,
                                                jint length) {
    jchar *data = env->GetCharArrayElements(src, NULL);
    LOGI("encryptChars:%s\n", data);
    int i;
    for (i = 0; i < length; i++) {
        LOGI("encryptChars:%c", data[i]);
    }
    return src;
}


extern "C"
JNIEXPORT jshort
JNICALL
Java_com_sdt_testthreeso_FunCaller_calcBit(JNIEnv *env, jclass clazz, jshort bit) {
    // TODO: implement calcBit()
    LOGD("calcBit:%d", bit);
    return bit + 128;
}

extern "C"
JNIEXPORT jlong
JNICALL
Java_com_sdt_testthreeso_FunCaller_calcMumber(JNIEnv *env, jclass clazz, jlong number) {
    LOGD("calcMumber:%ld", number);
    return number * 13;
}

extern "C"
JNIEXPORT jfloat
JNICALL
Java_com_sdt_testthreeso_FunCaller_calcHeight(JNIEnv *env, jclass clazz, jfloat height) {
    LOGD("calcHeight:%f", height);
    return height * 1.1;
}

extern "C"
JNIEXPORT jdouble
JNICALL
Java_com_sdt_testthreeso_FunCaller_calcDistance(JNIEnv *env, jclass clazz, jdouble distance) {
    LOGD("calcHeight:%f", distance);
    return distance * 5.5;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_sdt_testthreeso_FunCaller_writeFile(JNIEnv *env, jclass clazz, jstring path,
                                             jstring text) {
    const char *p_path = (char *) env->GetStringUTFChars(path, 0);
    const char *content = (char *) env->GetStringUTFChars(text, 0);

    ofstream outfile;
    outfile.open(p_path, ios::out);
    if (outfile.is_open()) {
        outfile << content << endl;
        outfile.close();
    } else {
        LOGE("oen file failed:%s", p_path);
    }

    env->ReleaseStringUTFChars(path, p_path);
    env->ReleaseStringUTFChars(text, content);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_sdt_testthreeso_FunCaller_readText(JNIEnv *env, jclass clazz, jstring path) {
    const char *p_path = (char *) env->GetStringUTFChars(path, 0);
    ifstream infile;
    infile.open(p_path, ios::in);
    if (infile.is_open()) {

        infile.seekg(0, std::ios::end);
        size_t size = infile.tellg(); //????????????
        infile.seekg(0, ios::beg);
        LOGD("file size:%d", size);

        string text;
        string line;
        while (getline(infile, line)) {
            text.append(line);
        }
        jstring content = CStr2Jstring(env, text.c_str());
        infile.close();
        env->ReleaseStringUTFChars(path, p_path);
        return content;
    } else {
        LOGE("oen file failed:%s", p_path);
    }
}

/**
 * C???????????????jstring????????????
 */
jstring CStr2Jstring(JNIEnv *env, const char *pat) {
    jclass strClass = env->FindClass("java/lang/String");
    jmethodID mID = env->GetMethodID(strClass, "<init>", "([BLjava/lang/String;)V");
    jbyteArray bytes = env->NewByteArray((jsize) strlen(pat));
    env->SetByteArrayRegion(bytes, 0, (jsize) strlen(pat), (jbyte *) pat); //???char* ?????????byte??????
    jstring encoding = env->NewStringUTF("GB2312");
    return (jstring) env->NewObject(strClass, mID, bytes, encoding);
}




/*** JNI?????????????????????
 ******************
 ******************
 */

//native ????????????
jint get_random_num() {
    return rand() % 12;
}

/*????????????????????????????????????JNINativeMethod ?????????????????????
???????????????????????????????????????????????????????????????
?????????
1.java??????native???????????????????????????
2.????????????????????????????????????????????????????????????
3.C/C++???????????????????????????????????????
*/
static JNINativeMethod getMethods[] = {
        {"getRandomNum", "()I", (void *) get_random_num},
};


//?????????????????????RegisterNatives??????????????????????????????
static int registerNativeMethods(JNIEnv *env, const char *className, JNINativeMethod *getMethods,
                                 int methodsNum) {
    jclass clazz;
    //????????????native????????????
    clazz = env->FindClass(className);
    if (clazz == NULL) {
        return JNI_FALSE;
    }
    //???????????? ?????????java??? ??????????????????????????? ?????????????????????
    if (env->RegisterNatives(clazz, getMethods, methodsNum) < 0) {
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

static int registerNatives(JNIEnv *env) {
    //???????????????????????????FindClass ???????????????????????????
    const char *className = "com/sdt/testthreeso/FunCaller";
    return registerNativeMethods(env, className, getMethods,
                                 sizeof(getMethods) / sizeof(getMethods[0]));
}

//????????????
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = NULL;
    //??????JNIEnv
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }
    assert(env != NULL);
    //???????????? registerNatives ->registerNativeMethods ->env->RegisterNatives
    if (!registerNatives(env)) {
        return -1;
    }
    //??????jni ?????????
    return JNI_VERSION_1_6;
}
