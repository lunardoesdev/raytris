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

# ---------------------------------------------------------------------------
# Install & Packaging (CPack)
# ---------------------------------------------------------------------------
if(NOT ANDROID)
    include(GNUInstallDirs)

    # Binary -> <prefix>/bin, assets -> <prefix>/share/raytris/assets
    install(TARGETS ${PROJECT_NAME} RUNTIME DESTINATION ${CMAKE_INSTALL_BINDIR})
    install(DIRECTORY assets/ DESTINATION ${CMAKE_INSTALL_DATADIR}/${PROJECT_NAME}/assets)

    set(CPACK_PACKAGE_NAME    "${PROJECT_NAME}")
    set(CPACK_PACKAGE_VERSION "1.1")
    set(CPACK_PACKAGE_VENDOR  "retrolunar")
    # set(CPACK_PACKAGE_CONTACT "you@example.com")
    set(CPACK_PACKAGE_DESCRIPTION_SUMMARY "raytris - a simple raylib game")

    # Linux packages
    set(CPACK_GENERATOR "DEB;RPM;TGZ")
    set(CPACK_DEBIAN_PACKAGE_MAINTAINER "retrolunar")
    set(CPACK_DEBIAN_PACKAGE_DEPENDS "libgl1, libx11-6, libxi6, libxrandr2, libxinerama1, libxcursor1, libasound2")
    set(CPACK_RPM_PACKAGE_REQUIRES "libGL, libX11, libXi, libXrandr, libXinerama, libXcursor, alsa-lib")

    # Windows NSIS installer
    set(CPACK_NSIS_MODIFY_PATH  ON)
    set(CPACK_NSIS_DISPLAY_NAME "raytris")
    set(CPACK_PACKAGE_EXECUTABLES "raytris;raytris")

    include(CPack)
endif()
