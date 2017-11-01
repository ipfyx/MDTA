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
    p = fs::system_complete("/data/app/fr.mdta.mdta-2/");

    std::string hello = p.string();
    unsigned long file_count = 0;
    unsigned long dir_count = 0;
    unsigned long other_count = 0;
    unsigned long err_count = 0;

    if (!fs::exists(p))
    {
        hello = "\nNot found: ";
    }

    if (fs::is_directory(p))
    {
        hello += "\nIn directory: " + p.string() + "\n\n";
        fs::directory_iterator end_iter;
        for (fs::directory_iterator dir_itr(p);
             dir_itr != end_iter;
             ++dir_itr)
        {
            try
            {
                if (fs::is_directory(dir_itr->status()))
                {
                    ++dir_count;
                    hello += dir_itr->path().filename().string() + " [directory]\n";
                }
                else if (fs::is_regular_file(dir_itr->status()))
                {
                    ++file_count;
                    hello += dir_itr->path().filename().string() + "\n";
                }
                else
                {
                    ++other_count;
                    hello += dir_itr->path().filename().string() + " [other]\n";
                }

            }
            catch (const std::exception & ex)
            {
                ++err_count;
                hello += dir_itr->path().filename().string();
            }
        }
        hello += "\n" + std::to_string(file_count) + " files\n"
                  + std::to_string(dir_count) + " directories\n"
                  + std::to_string(other_count) + " others\n"
                  + std::to_string(err_count) + " errors\n";
    }
    else // must be a file
    {
        hello += "\nFound: " + p.string() + "\n";
    }
    return env->NewStringUTF(hello.c_str());
}
