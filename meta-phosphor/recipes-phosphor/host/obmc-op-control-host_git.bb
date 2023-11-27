SUMMARY = "org.openbmc.control.Host implementation for OpenPOWER"
DESCRIPTION = "A host control implementation suitable for OpenPOWER systems."
PROVIDES += "virtual/obmc-host-ctl"
PV = "1.0+git${SRCPV}"
PR = "r1"

SKELETON_DIR = "op-hostctl"
SYSTEMD_SERVICE:${PN} = " \
        op-start-host@.service \
        "
START_TMPL = "op-start-host@.service"
START_TGTFMT = "obmc-host-startmin@{1}.target"
START_INSTFMT = "op-start-host@{0}.service"
START_FMT = "../${START_TMPL}:${START_TGTFMT}.requires/${START_INSTFMT}"
SYSTEMD_LINK:${PN} += "${@compose_list_zip(d, 'START_FMT', 'OBMC_HOST_INSTANCES', 'OBMC_CHASSIS_INSTANCES')}"

inherit skeleton-gdbus
inherit obmc-phosphor-dbus-service
inherit pkgconfig

RPROVIDES:${PN} += "virtual-obmc-host-ctl"

DBUS_SERVICE:${PN} += "org.openbmc.control.Host@.service"
OBMC_CONTROL_INST = "org.openbmc.control.Host@{0}.service"
OBMC_CONTROL_SVC = "org.openbmc.control.Host@.service"
OBMC_CONTROL_FMT = "../${OBMC_CONTROL_SVC}:multi-user.target.wants/${OBMC_CONTROL_INST}"
SYSTEMD_LINK:${PN} += "${@compose_list(d, 'OBMC_CONTROL_FMT', 'OBMC_HOST_INSTANCES')}"
