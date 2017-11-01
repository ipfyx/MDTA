#include <jni.h>
#include <string>
#include "boost/filesystem/operations.hpp"
#include "boost/filesystem/path.hpp"
#include "boost/progress.hpp"
#include <iostream>

namespace fs = boost::filesystem;

extern "C"
JNIEXPORT jstring JNICALL
Java_fr_mdta_mdta_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    fs::path p(fs::current_path());
    //std::string hello = "Hello from C++";
    std::string hello = p.string();
    //printf(p.string());
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_fr_mdta_mdta_FilesScanner_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    fs::path p(fs::current_path());
    //std::string hello = "Hello from C++";
    std::string hello = p.string();
    //printf(p.string());
    return env->NewStringUTF(hello.c_str());
}
