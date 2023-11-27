SUMMARY = "SNMP Manager Configuration"
DESCRIPTION = "SNMP Manager Configuration."
HOMEPAGE = "http://github.com/openbmc/phosphor-snmp"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"
DEPENDS += "systemd"
DEPENDS += "sdbusplus ${PYTHON_PN}-sdbus++-native"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "phosphor-logging"
DEPENDS += "net-snmp"
SRCREV = "717fdc440a3f1c8ac0a538b0a641d1dcd23a32e1"
PV = "0.1+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/phosphor-snmp;branch=master;protocol=https"

S = "${WORKDIR}/git"

inherit meson pkgconfig
inherit python3native
inherit obmc-phosphor-dbus-service

EXTRA_OEMESON = " \
    -Dtests=disabled \
    "

DBUS_SERVICE:${PN} += "xyz.openbmc_project.Network.SNMP.service"
