SUMMARY = "Phosphor post code manager"
DESCRIPTION = "Phosphor post Code Manager monitors post code posted on dbus \
interface /xyz/openbmc_project/state/boot/raw by snoopd daemon and save them \
in a file under /var/lib for history."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"
SRCREV = "608a392189d48ff0f0b94d2f21d9dd7a99323872"
PV = "1.0+git${SRCPV}"

SRC_URI = "git://github.com/openbmc/phosphor-post-code-manager.git;branch=master;protocol=https"

S = "${WORKDIR}/git"

inherit meson pkgconfig systemd

DEPENDS += " \
    sdbusplus \
    phosphor-dbus-interfaces \
    phosphor-logging \
    libcereal \
    "
FILES:${PN}  += "${systemd_system_unitdir}/xyz.openbmc_project.State.Boot.PostCode@.service"
FILES:${PN}  += "${systemd_system_unitdir}/xyz.openbmc_project.State.Boot.PostCode.service"

pkg_postinst:${PN}:append() {
    mkdir -p $D$systemd_system_unitdir/multi-user.target.wants
    for i in ${OBMC_HOST_INSTANCES};
    do
        LINK="$D$systemd_system_unitdir/multi-user.target.wants/xyz.openbmc_project.State.Boot.PostCode@${i}.service"
        TARGET="..//xyz.openbmc_project.State.Boot.PostCode@.service"
        ln -s $TARGET $LINK
    done
}

pkg_prerm:${PN}:append() {
    for i in ${OBMC_HOST_INSTANCES};
    do
        LINK="$D$systemd_system_unitdir/multi-user.target.wants/xyz.openbmc_project.State.Boot.PostCode@${i}.service"
        rm $LINK
    done
}

