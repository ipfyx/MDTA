# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.6)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

# https://raw.githubusercontent.com/jomof/ndk-stl/master/ndk-stl-config.cmake

set( Boost_DEBUG DEBUG )
set( Boost_DETAILED_FAILURE_MSG TRUE)
set( Boost_USE_STATIC_LIBS ON )
set( Boost_USE_STATIC_RUNTIME ON )
set( Boost_USE_MULTITHREADED OFF )
set( Boost_NO_SYSTEM_PATHS ON )
set( BOOST_ALL_DYN_LINK OFF )
set(CMAKE_INSTALL_RPATH_USE_LINK_PATH TRUE)

set(Boost_INCLUDE_DIR src/main/jniLibs)
set( Boost_LIBRARY_DIR_RELEASE src/main/jniLibs/armeabi )
set( Boost_Version 1.65.1 )

add_library( system
             STATIC
             IMPORTED )

set_target_properties( # Specifies the target library.
                       system

                       # Specifies the parameter you want to define.
                       PROPERTIES IMPORTED_LOCATION

                       # Provides the path to the library you want to import.
                       src/main/jniLibs/armeabi/libboost_system.a)

add_library( filesystem
             STATIC
             IMPORTED )

set_target_properties( # Specifies the target library.
                       filesystem

                       # Specifies the parameter you want to define.
                       PROPERTIES IMPORTED_LOCATION

                       # Provides the path to the library you want to import.
                       src/main/jniLibs/armeabi/libboost_filesystem.a)


find_package( Boost 1.65.1 REQUIRED COMPONENTS system filesystem )
if( Boost_FOUND )
    add_library(simple-ls STATIC src/main/cpp/simple-ls.cpp)
    target_link_libraries(simple-ls system filesystem)
endif()

# Necessary for boost headers
include_directories(src/main/jniLibs/)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

add_library( # Sets the name of the library.
             native-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/native-lib.cpp )

target_link_libraries( # Specifies the target library.
                       native-lib

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )
