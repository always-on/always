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

add_executable(ReetiServer_Capturing ${SOURCE_CAPTURING})

target_link_libraries(ReetiServer_Capturing
  ${URBI}
  ${PORT}
  ${BOOST_SYSTEM}
  ${OpenCV_LIBS}
)

FILE(COPY /home/reeti/Always-On/always/core/Reeti/conf/. DESTINATION /home/reeti/reetiPrograms/load)
FILE(COPY /home/reeti/Always-On/always/core/Reeti/src/functions/. DESTINATION /home/reeti/reetiPrograms/functions)
FILE(COPY /home/reeti/Always-On/always/core/Reeti/CapturingRestarter/RestartReetiServerCapturingService DESTINATION /etc/init.d/)
















