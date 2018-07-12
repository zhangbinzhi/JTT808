package com.lealone.harbor.utils

import java.util.*
import kotlin.experimental.and


/**
 * 把一个整形该为byte
 *
 * @param value
 * @return
 * @throws Exception
 */
fun integerTo1Byte(value: Int): Byte {
    return (value and 0xFF).toByte()
}

/**
 * 把一个整形该为1位的byte数组
 *
 * @param value
 * @return
 * @throws Exception
 */
fun integerTo1Bytes(value: Int): ByteArray {
    val result = ByteArray(1)
    result[0] = (value and 0xFF).toByte()
    return result
}

/**
 * 把一个整形改为2位的byte数组
 *
 * @param value
 * @return
 * @throws Exception
 */
fun integerTo2Bytes(value: Int): ByteArray {
    val result = ByteArray(2)
    result[0] = (value.ushr(8) and 0xFF).toByte()
    result[1] = (value and 0xFF).toByte()
    return result
}

/**
 * 把一个整形改为3位的byte数组
 *
 * @param value
 * @return
 * @throws Exception
 */
fun integerTo3Bytes(value: Int): ByteArray {
    val result = ByteArray(3)
    result[0] = (value.ushr(16) and 0xFF).toByte()
    result[1] = (value.ushr(8) and 0xFF).toByte()
    result[2] = (value and 0xFF).toByte()
    return result
}

/**
 * 把一个整形改为4位的byte数组
 *
 * @param value
 * @return
 * @throws Exception
 */
fun integerTo4Bytes(value: Int): ByteArray {
    val result = ByteArray(4)
    result[0] = (value.ushr(24) and 0xFF).toByte()
    result[1] = (value.ushr(16) and 0xFF).toByte()
    result[2] = (value.ushr(8) and 0xFF).toByte()
    result[3] = (value and 0xFF).toByte()
    return result
}

/**
 * 把byte[]转化位整形,通常为指令用
 *
 * @param value
 * @return
 * @throws Exception
 */
fun byteToInteger(value: ByteArray): Int {
    val result: Int
    if (value.size == 1) {
        result = oneByteToInteger(value[0])
    } else if (value.size == 2) {
        result = twoBytesToInteger(value)
    } else if (value.size == 3) {
        result = threeBytesToInteger(value)
    } else if (value.size == 4) {
        result = fourBytesToInteger(value)
    } else {
        result = fourBytesToInteger(value)
    }
    return result
}

/**
 * 把一个byte转化位整形,通常为指令用
 *
 * @param value
 * @return
 * @throws Exception
 */
fun oneByteToInteger(value: Byte): Int {
    return value.toInt() and 0xFF
}

/**
 * 把一个2位的数组转化位整形
 *
 * @param value
 * @return
 * @throws Exception
 */
fun twoBytesToInteger(value: ByteArray): Int {
    // if (value.length < 2) {
    // throw new Exception("Byte array too short!");
    // }
    val temp0 = value[0].toInt() and 0xFF
    val temp1 = value[1].toInt() and 0xFF
    return (temp0 shl 8) + temp1
}


/**
 * 把一个3位的数组转化位整形
 *
 * @param value
 * @return
 * @throws Exception
 */
fun threeBytesToInteger(value: ByteArray): Int {
    val temp0 = value[0].toInt() and 0xFF
    val temp1 = value[1].toInt() and 0xFF
    val temp2 = value[2].toInt() and 0xFF
    return (temp0 shl 16) + (temp1 shl 8) + temp2
}

/**
 * 把一个4位的数组转化位整形,通常为指令用
 *
 * @param value
 * @return
 * @throws Exception
 */
fun fourBytesToInteger(value: ByteArray): Int {
    // if (value.length < 4) {
    // throw new Exception("Byte array too short!");
    // }
    val temp0 = value[0].toInt() and 0xFF
    val temp1 = value[1].toInt() and 0xFF
    val temp2 = value[2].toInt() and 0xFF
    val temp3 = value[3].toInt() and 0xFF
    return (temp0 shl 24) + (temp1 shl 16) + (temp2 shl 8) + temp3
}

/**
 * 把一个4位的数组转化位整形
 *
 * @param value
 * @return
 * @throws Exception
 */
@Throws(Exception::class)
fun fourBytesToLong(value: ByteArray): Long {
    // if (value.length < 4) {
    // throw new Exception("Byte array too short!");
    // }
    val temp0 = value[0].toInt() and 0xFF
    val temp1 = value[1].toInt() and 0xFF
    val temp2 = value[2].toInt() and 0xFF
    val temp3 = value[3].toInt() and 0xFF
    return (temp0.toLong() shl 24) + (temp1 shl 16).toLong() + (temp2 shl 8).toLong() + temp3.toLong()
}

/**
 * 把一个数组转化长整形
 *
 * @param value
 * @return
 * @throws Exception
 */
fun bytes2Long(value: ByteArray): Long {
    var result: Long = 0
    val len = value.size
    var temp: Int
    for (i in 0 until len) {
        temp = (len - 1 - i) * 8
        if (temp == 0) {
            result += (value[i].toInt() and 0x0ff).toLong()
        } else {
            result += (value[i].toInt() and 0x0ff shl temp).toLong()
        }
    }
    return result
}

/**
 * 把一个长整形改为byte数组
 *
 * @param value
 * @return
 * @throws Exception
 */
fun longToBytes(value: Long): ByteArray {
    return longToBytes(value, 8)
}

/**
 * 把一个长整形改为byte数组
 *
 * @param value
 * @return
 * @throws Exception
 */
fun longToBytes(value: Long, len: Int): ByteArray {
    val result = ByteArray(len)
    var temp: Int
    for (i in 0 until len) {
        temp = (len - 1 - i) * 8
        if (temp == 0) {
            result[i] = (value and 0x0ff).toByte()
        } else {
            result[i] = (value.ushr(temp) and 0x0ff).toByte()
        }
    }
    return result
}

/**
 * 得到一个消息ID
 *
 * @return
 * @throws Exception
 */
@Throws(Exception::class)
fun generateTransactionID(): ByteArray {
    val id = ByteArray(16)
    System.arraycopy(integerTo2Bytes((Math.random() * 65536).toInt()), 0, id, 0, 2)
    System.arraycopy(integerTo2Bytes((Math.random() * 65536).toInt()), 0, id, 2, 2)
    System.arraycopy(integerTo2Bytes((Math.random() * 65536).toInt()), 0, id, 4, 2)
    System.arraycopy(integerTo2Bytes((Math.random() * 65536).toInt()), 0, id, 6, 2)
    System.arraycopy(integerTo2Bytes((Math.random() * 65536).toInt()), 0, id, 8, 2)
    System.arraycopy(integerTo2Bytes((Math.random() * 65536).toInt()), 0, id, 10, 2)
    System.arraycopy(integerTo2Bytes((Math.random() * 65536).toInt()), 0, id, 12, 2)
    System.arraycopy(integerTo2Bytes((Math.random() * 65536).toInt()), 0, id, 14, 2)
    return id
}

/**
 * 把IP拆分位int数组
 *
 * @param ip
 * @return
 * @throws Exception
 */
@Throws(Exception::class)
fun getIntIPValue(ip: String): IntArray {
    val sip = ip.split("[.]".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    // if (sip.length != 4) {
    // throw new Exception("error IPAddress");
    // }
    return intArrayOf(Integer.parseInt(sip[0]), Integer.parseInt(sip[1]), Integer.parseInt(sip[2]), Integer.parseInt(sip[3]))
}

/**
 * 把byte类型IP地址转化位字符串
 *
 * @param address
 * @return
 * @throws Exception
 */
@Throws(Exception::class)
fun getStringIPValue(address: ByteArray): String {
    val first = oneByteToInteger(address[0])
    val second = oneByteToInteger(address[1])
    val third = oneByteToInteger(address[2])
    val fourth = oneByteToInteger(address[3])

    return "$first.$second.$third.$fourth"
}

/**
 * 合并字节数组
 *
 * @param first
 * @param rest
 * @return
 */
fun concatAll(first: ByteArray, vararg rest: ByteArray): ByteArray {
    var totalLength = first.size
    for (array in rest) {
        totalLength += array.size
    }
    val result = Arrays.copyOf(first, totalLength)
    var offset = first.size
    for (array in rest) {
        System.arraycopy(array, 0, result, offset, array.size)
        offset += array.size

    }
    return result
}

/**
 * 合并字节数组
 *
 * @param rest
 * @return
 */
fun concatAll(rest: List<ByteArray>): ByteArray {
    var totalLength = 0
    for (array in rest) {

        totalLength += array.size

    }
    val result = ByteArray(totalLength)
    var offset = 0
    for (array in rest) {

        System.arraycopy(array, 0, result, offset, array.size)
        offset += array.size

    }
    return result
}

fun byte2Float(bs: ByteArray): Float {
    println(bytes2Long(bs))
    return if (bs.size == 4) {
        java.lang.Float.intBitsToFloat(
                (bs[3].toInt() and 0xFF shl 24) + (bs[2].toInt() and 0xFF shl 16) + (bs[1].toInt() and 0xFF shl 8) + (bs[0].toInt() and 0xFF))
    } else {
        java.lang.Float.intBitsToFloat(
                (bs[1].toInt() and 0xFF shl 8) + (bs[0].toInt() and 0xFF))
    }

}

fun byteBE2Float(bytes: ByteArray): Float {
    var l: Int
    if (bytes.size == 4) {
        l = bytes[0].toInt()
        l = l and 0xff
        l = l or (bytes[1].toLong() shl 8).toInt()
        l = l and 0xffff
        l = l or (bytes[2].toLong() shl 16).toInt()
        l = l and 0xffffff
        l = l or (bytes[3].toLong() shl 24).toInt()
        return java.lang.Float.intBitsToFloat(l)
    } else {
        l = bytes[0].toInt()
        l = l and 0xff
        l = l or (bytes[1].toLong() shl 8).toInt()
        return java.lang.Float.intBitsToFloat(l)
    }
}

fun getCheckSum4JT808(bs: ByteArray, start: Int, end: Int): Int {
    if (start < 0 || end > bs.size)
        throw ArrayIndexOutOfBoundsException("getCheckSum4JT808 error : index out of bounds(start=" + start
                + ",end=" + end + ",bytes length=" + bs.size + ")")
    var cs = 0
    for (i in start until end) {
        cs = cs xor bs[i].toInt()
    }
    return cs
}

fun getBitRange(number: Int, start: Int, end: Int): Int {
    if (start < 0)
        throw IndexOutOfBoundsException("min index is 0,but start = $start")
    if (end >= Integer.SIZE)
        throw IndexOutOfBoundsException("max index is " + (Integer.SIZE - 1) + ",but end = " + end)

    return (number shl Integer.SIZE - (end + 1)).ushr(Integer.SIZE - (end - start + 1))
}

fun getBitAt(number: Int, index: Int): Int {
    if (index < 0)
        throw IndexOutOfBoundsException("min index is 0,but $index")
    if (index >= Integer.SIZE)
        throw IndexOutOfBoundsException("max index is " + (Integer.SIZE - 1) + ",but " + index)

    return 1 shl index and number shr index
}

fun getBitAtS(number: Int, index: Int): Int {
    val s = Integer.toBinaryString(number)
    return Integer.parseInt(s[index] + "")
}

fun getBitRangeS(number: Int, start: Int, end: Int): Int {
    val s = Integer.toBinaryString(number)
    var sb = StringBuilder(s)
    while (sb.length < Integer.SIZE) {
        sb.insert(0, "0")
    }
    val tmp = sb.reverse().substring(start, end + 1)
    sb = StringBuilder(tmp)
    return Integer.parseInt(sb.reverse().toString(), 2)
}

fun floatToBytes(f: Float): ByteArray {
    // 把float转换为byte[]
    val fbit = java.lang.Float.floatToIntBits(f)
    val b = ByteArray(4)
    for (i in 0..3) {
        b[i] = (fbit shr 24 - i * 8).toByte()
    }
    // 翻转数组
    val len = b.size
    // 建立一个与源数组元素类型相同的数组
    val dest = ByteArray(len)
    // 为了防止修改源数组，将源数组拷贝一份副本
    System.arraycopy(b, 0, dest, 0, len)
    var temp: Byte
    // 将顺位第i个与倒数第i个交换
    for (i in 0 until len / 2) {
        temp = dest[i]
        dest[i] = dest[len - i - 1]
        dest[len - i - 1] = temp
    }

    return dest

}

