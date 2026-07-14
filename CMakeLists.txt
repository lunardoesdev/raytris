cmake_minimum_required(VERSION 3.14 FATAL_ERROR)

project(raytris LANGUAGES C CXX)


if(ANDROID)
    set(PLATFORM "Android")
    set(GRAPHICS "GRAPHICS_API_OPENGL_ES2")
    add_definitions(-DPLATFORM_ANDROID)

message(STATUS "ANDROID_NDK: ${ANDROID_NDK}")
message(STATUS "CMAKE_TOOLCHAIN_FILE: ${CMAKE_TOOLCHAIN_FILE}")
message(STATUS "ANDROID_ABI: ${ANDROID_ABI}")
endif()


include(cmake/CPM.cmake)

CPMAddPackage("gh:raysan5/raylib#6.0")
CPMAddPackage(
  NAME lua
  GITHUB_REPOSITORY lua/lua
  VERSION 5.5.0
  DOWNLOAD_ONLY YES
)

if(lua_ADDED)
  # lua has no CMakeLists, so we create our own target
  file(GLOB lua_sources ${lua_SOURCE_DIR}/*.c)
  list(REMOVE_ITEM lua_sources "${lua_SOURCE_DIR}/lua.c" "${lua_SOURCE_DIR}/luac.c")
  add_library(lua STATIC ${lua_sources})
  target_include_directories(lua SYSTEM PUBLIC $<BUILD_INTERFACE:${lua_SOURCE_DIR}>)
endif()

CPMAddPackage(
  NAME sol2
  GITHUB_REPOSITORY ThePhD/sol2
  VERSION 3.3.0
)


set(SOURCES
  src/main/c/rayexample.cpp
)

if(ANDROID)
    add_library(${PROJECT_NAME} SHARED ${SOURCES} ${ANDROID_NDK}/sources/android/native_app_glue/android_native_app_glue.c)
    target_include_directories(${PROJECT_NAME} PRIVATE ${ANDROID_NDK}/sources/android/native_app_glue)

target_link_libraries(${PROJECT_NAME}
                      android
                      log
                      EGL
                      GLESv2
                      OpenSLES)
else()
    add_executable(${PROJECT_NAME} ${SOURCES})
endif()
target_link_libraries(${PROJECT_NAME} lua sol2 raylib)

