LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"
DEPENDS = " \
    openssl \
    zlib \
    boost \
    libpam \
    sdbusplus \
    gtest \
    nlohmann-json \
    libtinyxml2 \
    nghttp2 \
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'gtest', '', d)} \
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'gmock', '', d)} \
"
SRCREV = "3e7a8da60d70f4c42ae8ce0a3ecb0709194eb831"
PV = "1.0+git${SRCPV}"

SRC_URI = "git://github.com/openbmc/bmcweb.git;branch=master;protocol=https"
SRC_URI += " \
    file://run-ptest \
"

S = "${WORKDIR}/git"
SYSTEMD_SERVICE:${PN} += "bmcweb.service bmcweb.socket"

inherit systemd
inherit useradd
inherit pkgconfig meson ptest

PACKAGECONFIG ??= ""
PACKAGECONFIG[insecure-redfish-expand]="-Dinsecure-enable-redfish-query=enabled"

EXTRA_OEMESON = " \
    --buildtype=minsize \
    -Dtests=${@bb.utils.contains('PTEST_ENABLED', '1', 'enabled', 'disabled', d)} \
"

do_install_ptest() {
        install -d ${D}${PTEST_PATH}/test
        cp -rf ${B}/*_test ${D}${PTEST_PATH}/test/
}

RDEPENDS:${PN} += " \
    jsnbd \
    phosphor-objmgr \
"

FILES:${PN} += "${datadir}/** "

USERADD_PACKAGES = "${PN}"
# add a user called httpd for the server to assume
USERADD_PARAM:${PN} = "-r -s /sbin/nologin bmcweb"

GROUPADD_PARAM:${PN} = "web; redfish; hostconsole"
FULL_OPTIMIZATION:append = " -Os"
