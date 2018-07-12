package com.lealone.harbor.utils

import java.io.ByteArrayOutputStream


/**
 * 接收消息时转义<br></br>
 *
 * <pre>
 * 0x7d01 <====> 0x7d
 * 0x7d02 <====> 0x7e
</pre> *
 *
 * @param bs
 * 要转义的字节数组
 * @param start
 * 起始索引
 * @param end
 * 结束索引
 * @return 转义后的字节数组
 * @throws Exception
 */
@Throws(Exception::class)
fun doEscape4Receive(bs: ByteArray, start: Int, end: Int): ByteArray {
    if (start < 0 || end > bs.size)
        throw ArrayIndexOutOfBoundsException("doEscape4Receive error : index out of bounds(start=" + start
                + ",end=" + end + ",bytes length=" + bs.size + ")")
    var baos: ByteArrayOutputStream? = null
    try {
        baos = ByteArrayOutputStream()
        for (i in 0 until start) {
            baos.write(bs[i].toInt())
        }
        run {
            var i = start
            while (i < end - 1) {
                if (bs[i].toInt() == 0x7d && bs[i + 1].toInt() == 0x01) {
                    baos!!.write(0x7d)
                    i++
                } else if (bs[i].toInt() == 0x7d && bs[i + 1].toInt() == 0x02) {
                    baos!!.write(0x7e)
                    i++
                } else {
                    baos!!.write(bs[i].toInt())
                }
                i++
            }
        }
        for (i in end - 1 until bs.size) {
            baos.write(bs[i].toInt())
        }
        return baos.toByteArray()
    } catch (e: Exception) {
        throw e
    } finally {
        if (baos != null) {
            baos.close()
            baos = null
        }
    }
}

/**
 *
 * 发送消息时转义<br></br>
 *
 * <pre>
 * 0x7e <====> 0x7d02
</pre> *
 *
 * @param bs
 * 要转义的字节数组
 * @param start
 * 起始索引
 * @param end
 * 结束索引
 * @return 转义后的字节数组
 * @throws Exception
 */
@Throws(Exception::class)
fun doEscape4Send(bs: ByteArray, start: Int, end: Int): ByteArray {
    if (start < 0 || end > bs.size)
        throw ArrayIndexOutOfBoundsException("doEscape4Send error : index out of bounds(start=" + start
                + ",end=" + end + ",bytes length=" + bs.size + ")")
    var baos: ByteArrayOutputStream? = null
    try {
        baos = ByteArrayOutputStream()
        for (i in 0 until start) {
            baos.write(bs[i].toInt())
        }
        for (i in start until end) {
            if (bs[i].toInt() == 0x7e) {
                baos.write(0x7d)
                baos.write(0x02)
            } else {
                baos.write(bs[i].toInt())
            }
        }
        for (i in end until bs.size) {
            baos.write(bs[i].toInt())
        }
        return baos.toByteArray()
    } catch (e: Exception) {
        throw e
    } finally {
        if (baos != null) {
            baos.close()
            baos = null
        }
    }
}

fun generateMsgBodyProps(msgLen: Int, enctyptionType: Int, isSubPackage: Boolean, reversed_14_15: Int): Int {
    // [ 0-9 ] 0000,0011,1111,1111(3FF)(消息体长度)
    // [10-12] 0001,1100,0000,0000(1C00)(加密类型)
    // [ 13_ ] 0010,0000,0000,0000(2000)(是否有子包)
    // [14-15] 1100,0000,0000,0000(C000)(保留位)
    val subPkg = if (isSubPackage) 1 else 0
    val ret = (msgLen and 0x3FF or (enctyptionType shl 10 and 0x1C00) or (subPkg shl 13 and 0x2000)
            or (reversed_14_15 shl 14 and 0xC000))
    return ret and 0xffff
}

@Throws(Exception::class)
fun generateMsgHeader(phone: String, msgType: Int, body: ByteArray, msgBodyProps: Int, flowId: Int): ByteArray {
    var baos: ByteArrayOutputStream? = null
    try {
        baos = ByteArrayOutputStream()
        // 1. 消息ID word(16)
        baos.write(integerTo2Bytes(msgType))
        // 2. 消息体属性 word(16)
        baos.write(integerTo2Bytes(msgBodyProps))
        // 3. 终端手机号 bcd[6]
        baos.write(string2Bcd(phone))
        // 4. 消息流水号 word(16),按发送顺序从 0 开始循环累加
        baos.write(integerTo2Bytes(flowId))
        // 消息包封装项 此处不予考虑
        return baos.toByteArray()
    } finally {
        if (baos != null) {
            baos.close()
        }
    }
}