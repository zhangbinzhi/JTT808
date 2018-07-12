package com.lealone.harbor.utils

/**
 * BCD字节数组===>String
 *
 * @param bytes
 * @return 十进制字符串
 */
fun bcd2String(bytes: ByteArray): String {
    val temp = StringBuilder(bytes.size * 2)
    for (i in bytes.indices) {
        // 高四位
        temp.append((bytes[i].toInt() and 0xf0).ushr(4))
        // 低四位
        temp.append(bytes[i].toInt() and 0x0f)
    }
    return if (temp.toString().substring(0, 1).equals("0", ignoreCase = true)) temp.toString().substring(1) else temp.toString()
}

/**
 * 字符串==>BCD字节数组
 *
 * @param str
 * @return BCD字节数组
 */
fun string2Bcd(str: String): ByteArray {
    var str = str
    // 奇数,前补零
    if (str.length and 0x1 == 1) {
        str = "0$str"
    }

    val ret = ByteArray(str.length / 2)
    val bs = str.toByteArray()
    for (i in ret.indices) {

        val high = ascII2Bcd(bs[2 * i])
        val low = ascII2Bcd(bs[2 * i + 1])

        // TODO 只遮罩BCD低四位?
        ret[i] = (high.toInt() shl 4 or low.toInt()).toByte()
    }
    return ret
}

private fun ascII2Bcd(asc: Byte): Byte {
    return if (asc >= '0'.toByte() && asc <= '9'.toByte())
        (asc - '0'.toByte()).toByte()
    else if (asc >= 'A'.toByte() && asc <= 'F'.toByte())
        (asc - 'A'.toByte() + 10).toByte()
    else if (asc >= 'a'.toByte() && asc <= 'f'.toByte())
        (asc - 'a'.toByte() + 10).toByte()
    else
        (asc - 48).toByte()
}