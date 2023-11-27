# Install EDK2 Base Tools in native sysroot. Currently the BaseTools are not
# built, they are just copied to native sysroot. This is sufficient for
# generating UEFI capsules as it only depends on some python scripts. Other
# tools need to be built first before adding to sysroot.

SUMMARY = "EDK2 Base Tools"
LICENSE = "BSD-2-Clause-Patent"

# EDK2
SRC_URI = "git://github.com/tianocore/edk2.git;branch=master;protocol=https"
LIC_FILES_CHKSUM = "file://License.txt;md5=2b415520383f7964e96700ae12b4570a"

SRCREV = "819cfc6b42a68790a23509e4fcc58ceb70e1965e"

S = "${WORKDIR}/git"

inherit native

RDEPENDS:${PN} += "python3-core"

do_install () {
    mkdir -p ${D}${bindir}/edk2-BaseTools
    cp -r ${WORKDIR}/git/BaseTools/* ${D}${bindir}/edk2-BaseTools/
}
