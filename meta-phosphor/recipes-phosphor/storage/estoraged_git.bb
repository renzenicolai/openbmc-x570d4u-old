SUMMARY = "Encrypted storage management daemon"
DESCRIPTION = "Provides a D-Bus interface to manage an encrypted storage device"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
DEPENDS += "systemd"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "cryptsetup"
DEPENDS += "openssl"
DEPENDS += "phosphor-logging"
DEPENDS += "sdbusplus"
DEPENDS += "stdplus"
SRCREV = "ff1b64f0e891de0d3cab831f6b90b52bb61de4b1"
PV = "0.1+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/estoraged.git;branch=master;protocol=https"

S = "${WORKDIR}/git"
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} += "xyz.openbmc_project.eStoraged.service"

inherit meson pkgconfig systemd

EXTRA_OEMESON = "-Dtests=disabled"

RDEPENDS:${PN} += "e2fsprogs"
