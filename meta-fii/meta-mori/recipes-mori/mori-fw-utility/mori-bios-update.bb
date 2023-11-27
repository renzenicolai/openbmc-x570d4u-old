SUMMARY = "Phosphor OpenBMC Mori BIOS Firmware Upgrade Command"
DESCRIPTION = "Phosphor OpenBMC Mori BIOS Firmware Upgrade Comman Daemon"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
DEPENDS:append = " systemd phosphor-ipmi-flash"
PROVIDES:append = " virtual/bios-update"
PR = "r1"

SRC_URI = " \
    file://phosphor-ipmi-flash-bios-update.service \
    file://config-bios.json \
"

SYSTEMD_SERVICE:${PN} = "phosphor-ipmi-flash-bios-update.service"

inherit systemd obmc-phosphor-systemd

do_install () {
    install -d ${D}${datadir}/phosphor-ipmi-flash
    install -m 0644 ${WORKDIR}/config-bios.json \
        ${D}${datadir}/phosphor-ipmi-flash
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/phosphor-ipmi-flash-bios-update.service \
        ${D}${systemd_system_unitdir}
}

RDEPENDS:${PN}:append = " libsystemd mori-fw"

RPROVIDES:${PN}:append = " virtual/bios-update"

FILES:${PN}:append = " ${datadir}/phosphor-ipmi-flash/config-bios.json"
