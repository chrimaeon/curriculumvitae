cmake_minimum_required(VERSION 3.22.0)
project(CvNative
    LANGUAGES
        C
        CXX
)

add_library(${PROJECT_NAME}
    SHARED
        ${JNI_SRC_DIR}/xor.c
        ${JNI_SRC_DIR}/Name.cpp
        ${JNI_SRC_DIR}/CvNative.cpp
)

set_target_properties(${PROJECT_NAME}
        PROPERTIES
        CXX_STANDARD 11
        CXX_STANDARD_REQUIRED YES
        CXX_EXTENSIONS NO
        CXX_VISIBILITY_PRESET hidden
        C_VISIBILITY_PRESET hidden
)

target_include_directories(${PROJECT_NAME}
    PRIVATE
        ${JNI_GENERATED_DIRS}
)

target_link_libraries(${PROJECT_NAME}
    PRIVATE
        log
)
