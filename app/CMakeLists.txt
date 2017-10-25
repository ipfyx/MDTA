# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

set( Boost_DEBUG DEBUG )
set( Boost_USE_STATIC_LIBS ON )
set( Boost_USE_STATIC_RUNTIME ON )
set( Boost_USE_MULTITHREADED OFF )
set( Boost_NO_SYSTEM_PATHS ON )
set( BOOST_ALL_DYN_LINK OFF )
set(CMAKE_INSTALL_RPATH_USE_LINK_PATH TRUE)

set( BOOST_ROOT /home/manwefm/Ecole/2018/PFE/lib/boost )
set( BOOST_INCLUDEDIR ${BOOST_ROOT}/include/)
set( BOOST_LIBRARYDIR ${BOOST_ROOT}/lib/ )
set( Boost_Version 1.65.1 )

find_package( Boost REQUIRED ${Boost_Version} COMPONENTS system )
if( Boost_FOUND )
    target_include_directories( MyLib PUBLIC/PRIVATE ${Boost_INCLUDE_DIR} )
    link_directories( ${Boost_LIBRARY_DIR} )
endif()

add_library( # Sets the name of the library.
             native-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/native-lib.cpp )

add_library( system
             SHARED
             IMPORTED )

set_target_properties( # Specifies the target library.
                       system

                       # Specifies the parameter you want to define.
                       PROPERTIES IMPORTED_LOCATION

                       # Provides the path to the library you want to import.
                       libs/dynamic-libs/libboost_system.so)

add_library( filesystem
             SHARED
             IMPORTED )

set_target_properties( # Specifies the target library.
                       filesystem

                       # Specifies the parameter you want to define.
                       PROPERTIES IMPORTED_LOCATION

                       # Provides the path to the library you want to import.
                       libs/dynamic-libs/libboost_filesystem.so)

include_directories(libs/)
LINK_DIRECTORIES(libs/dynamic-libs)

add_library( # Sets the name of the library.
             simple-ls

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/simple-ls.cpp )

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

target_link_libraries( # Specifies the target library.
                       native-lib
                       simple-ls
                       ${Boost_LIBRARIES}

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )
