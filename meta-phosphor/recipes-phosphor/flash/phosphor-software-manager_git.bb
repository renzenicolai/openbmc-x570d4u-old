SUMMARY = "Phosphor Software Management"
DESCRIPTION = "Phosphor Software Manager provides a set of system software \
management daemons. It is suitable for use on a wide variety of OpenBMC \
platforms."
DEPENDS += " \
    openssl \
    phosphor-dbus-interfaces \
    phosphor-logging \
    ${PYTHON_PN}-sdbus++-native \
    sdbusplus \
"
PACKAGECONFIG[verify_signature] = "-Dverify-signature=enabled, -Dverify-signature=disabled"
PACKAGECONFIG[sync_bmc_files] = "-Dsync-bmc-files=enabled, -Dsync-bmc-files=disabled"
PACKAGECONFIG[usb_code_update] = "-Dusb-code-update=enabled, -Dusb-code-update=disabled, cli11"
PACKAGECONFIG[side_switch_on_boot] = "-Dside-switch-on-boot=enabled, -Dside-switch-on-boot=disabled, cli11"
PACKAGECONFIG[ubifs_layout] = "-Dbmc-layout=ubi"
PACKAGECONFIG[mmc_layout] = "-Dbmc-layout=mmc"
PACKAGECONFIG[flash_bios] = "-Dhost-bios-upgrade=enabled, -Dhost-bios-upgrade=disabled"
PACKAGECONFIG[static-dual-image] = "-Dbmc-static-dual-image=enabled, -Dbmc-static-dual-image=disabled"
PV = "1.0+git${SRCPV}"
PR = "r1"

SOFTWARE_MGR_PACKAGES = " \
    ${PN}-version \
    ${PN}-download-mgr \
    ${PN}-updater \
    ${PN}-updater-ubi \
    ${PN}-updater-mmc \
    ${PN}-sync \
    ${PN}-usb \
    ${PN}-side-switch \
"
# Set SYSTEMD_PACKAGES to empty because we do not want ${PN} and DBUS_PACKAGES
# handles the rest.
SYSTEMD_PACKAGES = ""
SYSTEMD_SERVICE:${PN}-updater += " \
    force-reboot.service \
    obmc-flash-bmc-setenv@.service \
    reboot-guard-disable.service \
    reboot-guard-enable.service \
    usr-local.mount \
"
SYSTEMD_SERVICE:${PN}-updater += "${@bb.utils.contains('PACKAGECONFIG', 'flash_bios', 'obmc-flash-host-bios@.service', '', d)}"
SYSTEMD_SERVICE:${PN}-usb += "${@bb.utils.contains('PACKAGECONFIG', 'usb_code_update', 'usb-code-update@.service', '', d)}"
SYSTEMD_SERVICE:${PN}-side-switch += "${@bb.utils.contains('PACKAGECONFIG', 'side_switch_on_boot', 'phosphor-bmc-side-switch.service', '', d)}"
SYSTEMD_SERVICE:${PN}-updater += "${@bb.utils.contains('PACKAGECONFIG', 'static-dual-image', 'obmc-flash-bmc-alt@.service', '', d)}"
SYSTEMD_SERVICE:${PN}-updater += "${@bb.utils.contains('PACKAGECONFIG', 'static-dual-image', 'obmc-flash-bmc-static-mount-alt.service', '', d)}"
SYSTEMD_SERVICE:${PN}-updater += "${@bb.utils.contains('PACKAGECONFIG', 'static-dual-image', 'obmc-flash-bmc-prepare-for-sync.service', '', d)}"
S = "${WORKDIR}/git"

inherit meson pkgconfig
inherit obmc-phosphor-dbus-service
inherit python3native
inherit ${@bb.utils.contains('DISTRO_FEATURES', 'obmc-ubi-fs', 'phosphor-software-manager-ubi-fs', '', d)}
inherit ${@bb.utils.contains('DISTRO_FEATURES', 'phosphor-mmc', 'phosphor-software-manager-mmc', '', d)}

EXTRA_OEMESON:append = " -Dtests=disabled"

do_install:append() {
    install -d ${D}/usr/local
}

# The repo installs several scripts that depends on bash
RDEPENDS:${PN} += " bash"
RDEPENDS:${PN}-updater += " \
    bash \
    virtual-obmc-image-manager \
    ${@bb.utils.contains('PACKAGECONFIG', 'verify_signature', 'phosphor-image-signing', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'mmc_layout', 'e2fsprogs-e2fsck', '', d)} \
"

RPROVIDES:${PN}-version += " \
    virtual-obmc-image-manager \
"

FILES:${PN}-version += "${bindir}/phosphor-version-software-manager ${exec_prefix}/lib/tmpfiles.d/software.conf"
FILES:${PN}-download-mgr += "${bindir}/phosphor-download-manager"
FILES:${PN}-updater += " \
    ${bindir}/phosphor-image-updater \
    ${bindir}/obmc-flash-bmc \
    /usr/local \
    "
FILES:${PN}-sync += " \
    ${bindir}/phosphor-sync-software-manager \
    ${sysconfdir}/synclist \
    "
FILES:${PN}-usb += "\
    ${base_libdir}/udev/rules.d/70-bmc-usb.rules \
    ${bindir}/phosphor-usb-code-update \
    "
FILES:${PN}-side-switch += "\
    ${bindir}/phosphor-bmc-side-switch \
    "

require ${BPN}.inc

ALLOW_EMPTY:${PN} = "1"

PACKAGE_BEFORE_PN += "${SOFTWARE_MGR_PACKAGES}"
DBUS_PACKAGES = "${SOFTWARE_MGR_PACKAGES}"
DBUS_SERVICE:${PN}-version += "xyz.openbmc_project.Software.Version.service"
DBUS_SERVICE:${PN}-download-mgr += "xyz.openbmc_project.Software.Download.service"
DBUS_SERVICE:${PN}-updater += "xyz.openbmc_project.Software.BMC.Updater.service"
DBUS_SERVICE:${PN}-sync += "xyz.openbmc_project.Software.Sync.service"

pkg_postinst:${PN}-side-switch() {
    if ${@bb.utils.contains('PACKAGECONFIG', 'side_switch_on_boot', 'true', 'false', d)} ; then
        mkdir -p $D$systemd_system_unitdir/obmc-host-startmin@0.target.wants
        LINK="$D$systemd_system_unitdir/obmc-host-startmin@0.target.wants/phosphor-bmc-side-switch.service"
        TARGET="../phosphor-bmc-side-switch.service"
        ln -s $TARGET $LINK
    fi
}
pkg_prerm:${PN}-side-switch() {
    if ${@bb.utils.contains('PACKAGECONFIG', 'side_switch_on_boot', 'true', 'false', d)} ; then
        LINK="$D$systemd_system_unitdir/obmc-host-startmin@0.target.wants/phosphor-bmc-side-switch.service"
        rm $LINK
    fi
}
