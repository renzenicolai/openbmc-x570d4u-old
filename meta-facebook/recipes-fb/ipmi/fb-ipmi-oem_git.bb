SUMMARY = "Facebook OEM IPMI commands"
DESCRIPTION = "Facebook OEM IPMI commands"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9e69ba356fa59848ffd865152a3ccc13"

SRC_URI = "git://github.com/openbmc/fb-ipmi-oem;branch=master;protocol=https"
SRCREV = "8713427cf5f7059a1f434395ad7830bff54868c8"

S = "${WORKDIR}/git"
PV = "0.1+git${SRCPV}"

DEPENDS = "boost phosphor-ipmi-host phosphor-logging systemd "

inherit meson pkgconfig obmc-phosphor-ipmiprovider-symlink

PACKAGECONFIG ??= ""
PACKAGECONFIG:fb-compute-multihost ??= "bic"

PACKAGECONFIG[bic] = "-Dbic=enabled,-Dbic=disabled"

EXTRA_OEMESON="\
    -Dtests=disabled \
    -Dmachine='${MACHINE}' \
    -Dhost-instances='${OBMC_HOST_INSTANCES}' \
    "

LIBRARY_NAMES = "libzfboemcmds.so"

HOSTIPMI_PROVIDER_LIBRARY += "${LIBRARY_NAMES}"
NETIPMI_PROVIDER_LIBRARY += "${LIBRARY_NAMES}"

FILES:${PN}:append = " ${datadir}/lcd-debug/*.json"

FILES:${PN}:append = " ${libdir}/ipmid-providers/lib*${SOLIBS}"
FILES:${PN}:append = " ${libdir}/host-ipmid/lib*${SOLIBS}"
FILES:${PN}:append = " ${libdir}/net-ipmid/lib*${SOLIBS}"
FILES:${PN}-dev:append = " ${libdir}/ipmid-providers/lib*${SOLIBSDEV}"

do_install:append(){
   install -d ${D}${includedir}/fb-ipmi-oem
   install -m 0644 -D ${S}/include/*.hpp ${D}${includedir}/fb-ipmi-oem
}
