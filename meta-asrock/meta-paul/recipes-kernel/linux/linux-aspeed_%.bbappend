FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI:append:paul = " \
    file://aspeed-bmc-asrock-paul.dts \
"

do_patch:append() {
  for DTB in "${KERNEL_DEVICETREE}"; do
      DT=`basename ${DTB} .dtb`
      if [ -r "${WORKDIR}/${DT}.dts" ]; then
          cp ${WORKDIR}/${DT}.dts \
              ${STAGING_KERNEL_DIR}/arch/${ARCH}/boot/dts/aspeed
      fi
  done 
}
