cmake_minimum_required(VERSION 3.14 FATAL_ERROR)

project(raytris)

if(ANDROID)
    set(PLATFORM "Android")
    set(GRAPHICS "GRAPHICS_API_OPENGL_ES2")
    add_definitions(-DPLATFORM_ANDROID)
endif()


set(SOURCES
  src/main/c/rayexample.c
)

if(ANDROID)
    add_library(${PROJECT_NAME} SHARED ${SOURCES})
    target_link_libraries(${PROJECT_NAME} raylib)
else()
    add_executable(${PROJECT_NAME} ${SOURCES})
    target_link_libraries(${PROJECT_NAME} raylib)
endif()

include(cmake/CPM.cmake)

CPMAddPackage("gh:raysan5/raylib#6.0")
target_link_libraries(${PROJECT_NAME} raylib)

