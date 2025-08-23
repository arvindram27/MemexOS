@echo off
"C:\\Users\\Dhanalakshmi\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\cmake.exe" ^
  "-HC:\\Users\\Dhanalakshmi\\MemexOS\\app\\src\\main\\cpp" ^
  "-DCMAKE_SYSTEM_NAME=Android" ^
  "-DCMAKE_EXPORT_COMPILE_COMMANDS=ON" ^
  "-DCMAKE_SYSTEM_VERSION=24" ^
  "-DANDROID_PLATFORM=android-24" ^
  "-DANDROID_ABI=x86_64" ^
  "-DCMAKE_ANDROID_ARCH_ABI=x86_64" ^
  "-DANDROID_NDK=C:\\Users\\Dhanalakshmi\\AppData\\Local\\Android\\Sdk\\ndk\\25.2.9519653" ^
  "-DCMAKE_ANDROID_NDK=C:\\Users\\Dhanalakshmi\\AppData\\Local\\Android\\Sdk\\ndk\\25.2.9519653" ^
  "-DCMAKE_TOOLCHAIN_FILE=C:\\Users\\Dhanalakshmi\\AppData\\Local\\Android\\Sdk\\ndk\\25.2.9519653\\build\\cmake\\android.toolchain.cmake" ^
  "-DCMAKE_MAKE_PROGRAM=C:\\Users\\Dhanalakshmi\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\ninja.exe" ^
  "-DCMAKE_CXX_FLAGS=-std=c++17 -O3 -fexceptions" ^
  "-DCMAKE_LIBRARY_OUTPUT_DIRECTORY=C:\\Users\\Dhanalakshmi\\MemexOS\\app\\build\\intermediates\\cxx\\Debug\\5b2j5i2b\\obj\\x86_64" ^
  "-DCMAKE_RUNTIME_OUTPUT_DIRECTORY=C:\\Users\\Dhanalakshmi\\MemexOS\\app\\build\\intermediates\\cxx\\Debug\\5b2j5i2b\\obj\\x86_64" ^
  "-DCMAKE_BUILD_TYPE=Debug" ^
  "-BC:\\Users\\Dhanalakshmi\\MemexOS\\app\\.cxx\\Debug\\5b2j5i2b\\x86_64" ^
  -GNinja ^
  "-DANDROID_STL=c++_shared" ^
  "-DWHISPER_BUILD_TESTS=OFF" ^
  "-DWHISPER_BUILD_EXAMPLES=OFF"
