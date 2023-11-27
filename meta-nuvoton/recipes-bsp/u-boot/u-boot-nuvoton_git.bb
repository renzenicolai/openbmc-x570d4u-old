DESCRIPTION = "U-boot for Nuvoton NPCM7xx/NPCM8xx Baseboard Management Controller"

require u-boot-common-nuvoton.inc
require u-boot-nuvoton.inc

PROVIDES += "u-boot"

DEPENDS += "dtc-native"

SRC_URI:append:df-phosphor-mmc = " file://u-boot-emmc.cfg"
