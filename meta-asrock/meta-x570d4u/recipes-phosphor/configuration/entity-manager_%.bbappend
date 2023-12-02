FILESEXTRAPATHS:append := ":${THISDIR}/${PN}"
SRC_URI:append = " file://x570d4u.json \
		"
do_install:append() {
     install -d ${D}/usr/share/entity-manager/configurations
     install -m 0444 ${WORKDIR}/*.json ${D}/usr/share/entity-manager/configurations
}
