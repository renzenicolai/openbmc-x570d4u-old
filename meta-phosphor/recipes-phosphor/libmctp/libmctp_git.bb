SUMMARY = "MCTP stack"
DESCRIPTION = "MCTP library implementing the MCTP base specification"
HOMEPAGE = "https://github.com/openbmc/libmctp"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0d30807bb7a4f16d36e96b78f9ed8fae"
DEPENDS += "autoconf-archive-native \
            systemd \
           "
SRCREV = "8003c71053b3e3597019d2b0a1cf1e1fa4df2d14"
PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)} pcap"
PACKAGECONFIG[systemd] = "--with-systemdsystemunitdir=${systemd_system_unitdir}, \
                          --without-systemdsystemunitdir,systemd"
PACKAGECONFIG[pcap] = "--enable-capture,--disable-capture,libpcap,"
PV = "1.0+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/libmctp;branch=master;protocol=https \
           file://default"

SYSTEMD_SERVICE:${PN} = "mctp-demux.service \
                         mctp-demux.socket \
                        "
S = "${WORKDIR}/git"

inherit systemd
inherit autotools pkgconfig

do_install:append() {
        install -d ${D}${sysconfdir}/default
        install -m 0644 ${WORKDIR}/default ${D}${sysconfdir}/default/mctp
}

CONFFILES:${PN} = "${sysconfdir}/default/mctp"
