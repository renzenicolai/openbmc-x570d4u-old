SUMMARY = "SCP and MCP Firmware"
DESCRIPTION = "Firmware for SCP and MCP software reference implementation"
HOMEPAGE = "https://github.com/ARM-software/SCP-firmware"

LICENSE = "BSD-3-Clause & Apache-2.0"
LIC_FILES_CHKSUM = "file://license.md;beginline=5;md5=9db9e3d2fb8d9300a6c3d15101b19731 \
                    file://contrib/cmsis/git/LICENSE.txt;md5=e3fc50a88d0a364313df4b21ef20c29e"

SRC_URI_SCP_FIRMWARE ?= "gitsm://github.com/ARM-software/SCP-firmware.git;protocol=https"
SRC_URI = "${SRC_URI_SCP_FIRMWARE};branch=${SRCBRANCH} \
           file://0001-OPTEE-Private-Includes.patch \
          "

SRCBRANCH = "master"
SRCREV  = "cc4c9e017348d92054f74026ee1beb081403c168"

PROVIDES += "virtual/control-processor-firmware"

CMAKE_BUILD_TYPE    ?= "RelWithDebInfo"
SCP_PLATFORM        ?= "${MACHINE}"
SCP_LOG_LEVEL       ?= "WARN"
SCP_PLATFORM_FEATURE_SET ?= "0"

INHIBIT_DEFAULT_DEPS = "1"
DEPENDS = "gcc-arm-none-eabi-native \
           cmake-native \
           ninja-native \
          "

# For now we only build with GCC, so stop meta-clang trying to get involved
TOOLCHAIN = "gcc"

# remove once arm-none-eabi-gcc updates to 13 or newer like poky
DEBUG_PREFIX_MAP:remove = "-fcanon-prefix-map"

inherit deploy

B = "${WORKDIR}/build"
S = "${WORKDIR}/git"

# Allow platform specific copying of only scp or both scp & mcp, default to both
FW_TARGETS ?= "scp mcp"
FW_INSTALL ?= "ramfw romfw"

PACKAGE_ARCH = "${MACHINE_ARCH}"
COMPATIBLE_MACHINE ?= "invalid"

export CFLAGS = "${DEBUG_PREFIX_MAP}"
export ASMFLAGS = "${DEBUG_PREFIX_MAP}"

LDFLAGS[unexport] = "1"

EXTRA_OECMAKE = "-D CMAKE_BUILD_TYPE=${CMAKE_BUILD_TYPE} \
                 -D SCP_LOG_LEVEL=${SCP_LOG_LEVEL} \
                 -D SCP_PLATFORM_FEATURE_SET=${SCP_PLATFORM_FEATURE_SET} \
                 -D DISABLE_CPPCHECK=1 \
                 -D SCP_TOOLCHAIN=GNU \
                "

do_configure() {
    for FW in ${FW_TARGETS}; do
        for TYPE in ${FW_INSTALL}; do
            bbnote Configuring ${SCP_PLATFORM}/${FW}_${TYPE}...
            cmake -GNinja ${EXTRA_OECMAKE} -S ${S} -B "${B}/${TYPE}/${FW}" -D SCP_FIRMWARE_SOURCE_DIR:PATH="${SCP_PLATFORM}/${FW}_${TYPE}"
        done
    done
}

do_configure[cleandirs] += "${B}"

do_compile() {
    for FW in ${FW_TARGETS}; do
        for TYPE in ${FW_INSTALL}; do
            bbnote Building ${SCP_PLATFORM}/${FW}_${TYPE}...
            VERBOSE=1 cmake --build ${B}/${TYPE}/${FW} --target all
        done
    done
}

do_install() {
    install -d ${D}/firmware
    for TYPE in ${FW_INSTALL}; do
        for FW in ${FW_TARGETS}; do
           if [ "$TYPE" = "romfw" ]; then
               if [ "$FW" = "scp" ]; then
                   install -D "${B}/${TYPE}/${FW}/bin/${SCP_PLATFORM}-bl1.bin" "${D}/firmware/${FW}_${TYPE}.bin"
                   install -D "${B}/${TYPE}/${FW}/bin/${SCP_PLATFORM}-bl1.elf" "${D}/firmware/${FW}_${TYPE}.elf"
               else
                   install -D "${B}/${TYPE}/${FW}/bin/${SCP_PLATFORM}-${FW}-bl1.bin" "${D}/firmware/${FW}_${TYPE}.bin"
                   install -D "${B}/${TYPE}/${FW}/bin/${SCP_PLATFORM}-${FW}-bl1.elf" "${D}/firmware/${FW}_${TYPE}.elf"
               fi
           elif [ "$TYPE" = "ramfw" ]; then
               if [ "$FW" = "scp" ]; then
                   install -D "${B}/${TYPE}/${FW}/bin/${SCP_PLATFORM}-bl2.bin" "${D}/firmware/${FW}_${TYPE}.bin"
                   install -D "${B}/${TYPE}/${FW}/bin/${SCP_PLATFORM}-bl2.elf" "${D}/firmware/${FW}_${TYPE}.elf"
               else
                   install -D "${B}/${TYPE}/${FW}/bin/${SCP_PLATFORM}-${FW}-bl2.bin" "${D}/firmware/${FW}_${TYPE}.bin"
                   install -D "${B}/${TYPE}/${FW}/bin/${SCP_PLATFORM}-${FW}-bl2.elf" "${D}/firmware/${FW}_${TYPE}.elf"
               fi
           fi
       done
    done
}

FILES:${PN} = "/firmware"
SYSROOT_DIRS += "/firmware"

FILES:${PN}-dbg += "/firmware/*.elf"
# These binaries are specifically for 32-bit arm
INSANE_SKIP:${PN}-dbg += "arch"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_PACKAGE_STRIP = "1"

do_deploy() {
    # Copy the images to deploy directory
    cp -rf ${D}/firmware/* ${DEPLOYDIR}/
}
addtask deploy after do_install
