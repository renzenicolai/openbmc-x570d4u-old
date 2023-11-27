SUMMARY = "Common C++ functions"
DESCRIPTION = "Common C++ functions."
HOMEPAGE = "http://github.com/openbmc/stdplus"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"
DEPENDS += " \
  function2 \
  fmt \
  liburing \
  "
SRCREV = "a0875538b4d7cdb04dd478ec343c2b086a7ce098"
PV = "0.1+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/stdplus;branch=master;protocol=https"

S = "${WORKDIR}/git"

inherit meson pkgconfig

EXTRA_OEMESON = " \
        -Dexamples=false \
        -Dtests=disabled \
        -Dgtest=disabled \
        "

PACKAGES =+ "libstdplus libstdplus-dl libstdplus-io_uring"

FILES:libstdplus = "${libdir}/libstdplus.so.*"
FILES:libstdplus-dl = "${libdir}/libstdplus-dl.so.*"
FILES:libstdplus-io_uring = "${libdir}/libstdplus-io_uring.so.*"
