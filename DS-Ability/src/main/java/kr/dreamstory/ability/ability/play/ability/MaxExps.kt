package kr.dreamstory.ability.ability.play.ability

object MaxExps {

    const val maxLevel = 100
    private val maxExps: Array<Long> = Array(maxLevel + 1) {0}

    init {
        maxExps[0] = 15
        maxExps[1] = 34
        maxExps[2] = 57
        maxExps[3] = 92
        maxExps[4] = 135
        maxExps[5] = 372
        maxExps[6] = 560
        maxExps[7] = 840
        maxExps[8] = 1242
        maxExps[9] = 1242
        maxExps[10] = 1242
        maxExps[11] = 1242
        maxExps[12] = 1242
        maxExps[13] = 1242
        maxExps[14] = 1490
        maxExps[15] = 1788
        maxExps[16] = 2145
        maxExps[17] = 2574
        maxExps[18] = 3088
        maxExps[19] = 3705
        maxExps[20] = 4446
        maxExps[21] = 5335
        maxExps[22] = 6402
        maxExps[23] = 7682
        maxExps[24] = 9218
        maxExps[25] = 11061
        maxExps[26] = 13273
        maxExps[27] = 15927
        maxExps[28] = 19112
        maxExps[29] = 19112
        maxExps[30] = 19112
        maxExps[31] = 19112
        maxExps[32] = 19112
        maxExps[33] = 19112
        maxExps[34] = 22934
        maxExps[35] = 27520
        maxExps[36] = 33024
        maxExps[37] = 39628
        maxExps[38] = 47553
        maxExps[39] = 51357
        maxExps[40] = 55465
        maxExps[41] = 59902
        maxExps[42] = 64694
        maxExps[43] = 69869
        maxExps[44] = 75458
        maxExps[45] = 81494
        maxExps[46] = 88013
        maxExps[47] = 95054
        maxExps[48] = 102658
        maxExps[49] = 110870
        maxExps[50] = 119739
        maxExps[51] = 129318
        maxExps[52] = 139663
        maxExps[53] = 150836
        maxExps[54] = 162902
        maxExps[55] = 175934
        maxExps[56] = 190008
        maxExps[57] = 205208
        maxExps[58] = 221624
        maxExps[59] = 221624
        maxExps[60] = 221624
        maxExps[61] = 221624
        maxExps[62] = 221624
        maxExps[63] = 221624
        maxExps[64] = 238245
        maxExps[65] = 256113
        maxExps[66] = 275321
        maxExps[67] = 295970
        maxExps[68] = 318167
        maxExps[69] = 342029
        maxExps[70] = 367681
        maxExps[71] = 395257
        maxExps[72] = 424901
        maxExps[73] = 456768
        maxExps[74] = 488741
        maxExps[75] = 522952
        maxExps[76] = 559558
        maxExps[77] = 598727
        maxExps[78] = 640637
        maxExps[79] = 685481
        maxExps[80] = 733464
        maxExps[81] = 784806
        maxExps[82] = 839742
        maxExps[83] = 898523
        maxExps[84] = 961419
        maxExps[85] = 1028718
        maxExps[86] = 1100728
        maxExps[87] = 1177778
        maxExps[88] = 1260222
        maxExps[89] = 1342136
        maxExps[90] = 1429374
        maxExps[91] = 1522283
        maxExps[92] = 1621231
        maxExps[93] = 1726611
        maxExps[94] = 1838840
        maxExps[95] = 1958364
        maxExps[96] = 2085657
        maxExps[97] = 2221224
        maxExps[98] = 2365603
        maxExps[99] = 2365603
        maxExps[100] = 2365603
    }

    fun getMaxExp(level: Int): Long = maxExps[level]

}