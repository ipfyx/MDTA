#include <jni.h>
#include <string>

//This is useless for our app but we keep it
//to make sure that loadCppLibrary is detected by DexScanner

extern "C"
JNIEXPORT jstring JNICALL
Java_fr_mdta_mdta_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
