find_package(OpenCV 2.3.1 REQUIRED)

find_library(PORT NAMES port PATHS /usr/local/gostai/lib)

find_library(URBI NAMES urbi PATHS /usr/local/gostai/lib)

find_library(BOOST_SYSTEM NAMES boost_system-gcc44-mt-1_38 PATHS /usr/local/gostai/lib)

include_directories(
  ${OpenCV_INCLUDE_DIR}
  /usr/local/gostai/include/urbi/
  /usr/local/gostai/include/
  ../../include/
)

set(SOURCE_CAPTURING
  servers/ReetiServer_Capturing.cpp
)

set(SOURCE_FACETRACKING
  servers/ReetiServer_FaceTracking.cpp
)

set(SOURCE_MIRRORING
  servers/ReetiServer_Mirroring.cpp
)

set(SOURCE_HAPPY
  modules/happy.cpp
)

set(SOURCE_SAD
  modules/sad.cpp
)

set(SOURCE_BIGNOD
  modules/bignod.cpp
)

set(SOURCE_SMALLNOD
  modules/smallnod.cpp
)

set(SOURCE_DELAY
  modules/delay.cpp
)

set(SOURCE_LOOKAWAY
  modules/lookaway.cpp
)

set(SOURCE_TALK
  modules/talk.cpp
)

add_executable(ReetiServer_Capturing ${SOURCE_CAPTURING})

add_executable(ReetiServer_FaceTracking ${SOURCE_FACETRACKING})

add_executable(ReetiServer_Mirroring ${SOURCE_MIRRORING})

add_library(happy SHARED ${SOURCE_HAPPY})

add_library(sad SHARED ${SOURCE_SAD})

add_library(bignod SHARED ${SOURCE_BIGNOD})

add_library(smallnod SHARED ${SOURCE_SMALLNOD})

add_library(delay SHARED ${SOURCE_DELAY})

add_library(lookaway SHARED ${SOURCE_LOOKAWAY})

add_library(talk SHARED ${SOURCE_TALK})

target_link_libraries(ReetiServer_Capturing
  ${URBI}
  ${PORT}
  ${BOOST_SYSTEM}
  ${OpenCV_LIBS}
)

target_link_libraries(ReetiServer_FaceTracking
  ${URBI}
  ${PORT}
  ${BOOST_SYSTEM}
)

target_link_libraries(ReetiServer_Mirroring
  ${URBI}
  ${PORT}
  ${BOOST_SYSTEM}
)

FILE(COPY /home/reeti/Always-On/always/core/Reeti/conf/. DESTINATION /home/reeti/reetiPrograms/load)

















