package com.lealone.harbor.service.codec

import com.lealone.harbor.common.TPMSConsts
import com.lealone.harbor.service.handler.SocketHandler
import com.lealone.harbor.utils.*
import com.lealone.harbor.vo.PackageData
import com.lealone.harbor.vo.req.LocationInfoUploadMsg
import com.lealone.harbor.vo.req.TerminalRegisterMsg
import org.slf4j.LoggerFactory

class MsgDecoder {
    private val log = LoggerFactory.getLogger(MsgDecoder::class.java)
    fun bytes2PackageData(data: ByteArray, socket: SocketHandler): PackageData {
        // 1. 16byte 或 12byte 消息头
        val msgHeader = parseMsgHeaderFromBytes(data)
        // 0. 终端套接字地址信息
        val ret = PackageData(msgHeader, socket)
        // 2. 消息体
        // 有子包信息,消息体起始字节后移四个字节:消息包总数(word(16))+包序号(word(16))
        var msgBodyByteStartIndex = 12
        if (msgHeader.hasSubPackage) {
            msgBodyByteStartIndex = 14
        }
        ret.msgBodyBytes = data.copyOfRange(msgBodyByteStartIndex, data.size)
        // 3. 去掉分隔符之后，最后一位就是校验码
        val checkSumInPkg = data[data.size - 1].toInt()
        val calculatedCheckSum = getCheckSum4JT808(data, 0, data.size - 1)
        ret.checkSum = calculatedCheckSum
        if (checkSumInPkg != calculatedCheckSum) {
            log.warn("检验码不一致,msgid:{},pkg:{},calculated:{}", msgHeader.msgId, checkSumInPkg, calculatedCheckSum)
        }

        return ret
    }

    fun toTerminalRegisterMsg(packageData: PackageData): TerminalRegisterMsg? {
        val ret = TerminalRegisterMsg(packageData)
        val data = ret.msgBodyBytes
        if (data == null || data.isEmpty()) {
            log.warn("[空白的注册信息],phone={},flowid={}", packageData.msgHeader.terminalPhone, packageData.msgHeader.flowId)
            return null
        }
        val body = TerminalRegisterMsg.TerminalRegInfo()
        // 1. byte[0-1] 省域ID(WORD)
        // 设备安装车辆所在的省域，省域ID采用GB/T2260中规定的行政区划代码6位中前两位
        // 0保留，由平台取默认值
        body.provinceId = parseIntFromBytes(data, 0, 2)
        // 2. byte[2-3] 设备安装车辆所在的市域或县域,市县域ID采用GB/T2260中规定的行 政区划代码6位中后四位
        // 0保留，由平台取默认值
        body.cityId = parseIntFromBytes(data, 4, 4)
        // 3. byte[4-8] 制造商ID(BYTE[5]) 5 个字节，终端制造商编码
        // byte[] tmp = new byte[5];
        body.manufacturerId = parseStringFromBytes(data, 4, 5)
        // 4. byte[9-16] 终端型号(BYTE[8]) 八个字节， 此终端型号 由制造商自行定义 位数不足八位的，补空格。
        body.terminalType = parseStringFromBytes(data, 9, 8)
        // 5. byte[17-23] 终端ID(BYTE[7]) 七个字节， 由大写字母 和数字组成， 此终端 ID由制造 商自行定义
        body.terminalId = parseStringFromBytes(data, 17, 7)
        // 6. byte[24] 车牌颜色(BYTE) 车牌颜 色按照JT/T415-2006 中5.4.12 的规定
        body.licensePlateColor = parseIntFromBytes(data, 24, 1)
        // 7. byte[25-x] 车牌(STRING) 公安交 通管理部门颁 发的机动车号牌
        body.licensePlate = parseStringFromBytes(data, 24, data.size - 25)
        ret.terminalRegInfo = body
        return ret
    }

    fun toLocationInfoUploadMsg(packageData: PackageData): LocationInfoUploadMsg? {
        val ret = LocationInfoUploadMsg(packageData)

        val data = ret.msgBodyBytes
        if (data == null || data.isEmpty()) {
            log.warn("[空白的位置信息],phone={},flowid={}", packageData.msgHeader.terminalPhone, packageData.msgHeader.flowId)
            return null
        }
        // 1. byte[0-3] 报警标志(DWORD(32))
        ret.warningFlagField = parseIntFromBytes(data, 0, 4)
        // 2. byte[4-7] 状态(DWORD(32))
        ret.statusField = parseIntFromBytes(data, 4, 4)
        // 3. byte[8-11] 纬度(DWORD(32)) 以度为单位的纬度值乘以10^6，精确到百万分之一度
        ret.latitude = parseLonOrLatFromBytes(data, 8, 4)
        // 4. byte[12-15] 经度(DWORD(32)) 以度为单位的经度值乘以10^6，精确到百万分之一度
        ret.longitude = parseLonOrLatFromBytes(data, 12, 4)
        // 5. byte[16-17] 高程(WORD(16)) 海拔高度，单位为米（ m）
        ret.elevation = parseIntFromBytes(data, 16, 2)
        // byte[18-19] 速度(WORD) 1/10km/h
        ret.speed = parseFloatFromBytes(data, 18, 2)
        // byte[20-21] 方向(WORD) 0-359，正北为 0，顺时针
        ret.direction = parseIntFromBytes(data, 20, 2)
        // byte[22-26] 时间(BCD[6]) YY-MM-DD-hh-mm-ss
        // GMT+8 时间，本标准中之后涉及的时间均采用此时区
//        val temp = data.copyOfRange(22,28)
        ret.time = parseBcdStringFromBytes(data, 22, 6)
        log.info(ret.toString())
        return ret

    }


    fun parseStringFromBytes(data: ByteArray, startIndex: Int, lenth: Int): String {
        return parseStringFromBytes(data, startIndex, lenth, "")
    }

    fun parseStringFromBytes(data: ByteArray, startIndex: Int, lenth: Int, defaultVal: String): String {
        return try {
            val tmp = ByteArray(lenth)
            System.arraycopy(data, startIndex, tmp, 0, lenth)
            String(tmp, TPMSConsts.string_charset)
        } catch (e: Exception) {
            log.error("解析字符串出错:", e)
            defaultVal
        }

    }

    fun parseMsgHeaderFromBytes(data: ByteArray): PackageData.MsgHeader {
        val msgHeader = PackageData.MsgHeader()
        // 1. 消息ID word(16)
        msgHeader.msgId = parseIntFromBytes(data, 0, 2)
        val msgBodyProps = parseIntFromBytes(data, 2, 2)
        // 2. 消息体属性 word(16)=================>
        msgHeader.msgBodyPropsField = msgBodyProps
        // [ 0-9 ] 0000,0011,1111,1111(3FF)(消息体长度)
        msgHeader.msgBodyLength = (msgBodyProps and 0x3FF)
        // [10-12] 0001,1100,0000,0000(1C00)(加密类型)
        msgHeader.encryptionType = ((msgBodyProps and 0x1c00) shr 10)
        // [ 13_ ] 0010,0000,0000,0000(2000)(是否有子包)
        msgHeader.hasSubPackage = (((msgBodyProps and 0x2000) shl 13) == 1)
        // [14-15] 1100,0000,0000,0000(C000)(保留位)
        msgHeader.reservedBit = ((msgBodyProps and 0xc000) shr 14).toString()
        // 消息体属性 word(16)<=================

        // 3. 终端手机号 bcd[6]
        msgHeader.terminalPhone = parseBcdStringFromBytes(data, 4, 6)
        // 4. 消息流水号 word(16) 按发送顺序从 0 开始循环累加
        msgHeader.flowId = parseIntFromBytes(data, 10, 2)
        // 5. 消息包封装项
        // 有子包信息
        if (msgHeader.hasSubPackage) {
            // 消息包封装项字段
            msgHeader.packageInfoField = parseIntFromBytes(data, 12, 4)
            // byte[0-1] 消息包总数(word(16))
            msgHeader.totalSubPackage = parseIntFromBytes(data, 12, 2)
            // byte[2-3] 包序号(word(16)) 从 1 开始
            msgHeader.subPackageSeq = parseIntFromBytes(data, 14, 2)
        }
        return msgHeader
    }

    private fun parseBcdStringFromBytes(data: ByteArray, startIndex: Int, length: Int): String {
        return parseBcdStringFromBytes(data, startIndex, length, "")
    }

    private fun parseBcdStringFromBytes(data: ByteArray, startIndex: Int, length: Int, defaultVal: String): String {
        return try {
            bcd2String(data.copyOfRange(startIndex, startIndex + length))
        } catch (e: Exception) {
            log.error("解析BCD(8421码)出错:", e)
            defaultVal
        }
    }

    private fun parseIntFromBytes(data: ByteArray, startIndex: Int, length: Int): Int {
        return parseIntFromBytes(data, startIndex, length, 0)
    }

    private fun parseIntFromBytes(data: ByteArray, startIndex: Int, length: Int, defaultVal: Int): Int {
        return try {
            byteToInteger(data.copyOfRange(startIndex, startIndex + length))
        } catch (e: Exception) {
            log.error("解析整数出错:", e)
            defaultVal
        }
    }

    private fun parseLonOrLatFromBytes(data: ByteArray, startIndex: Int, length: Int): Float {
        return try {
            // 字节数大于4,从起始索引开始向后处理4个字节,其余超出部分丢弃
            val len = if (length > 4) 4 else length
            val tmp = ByteArray(len)
            System.arraycopy(data, startIndex, tmp, 0, len)
            val temp = bytes2Long(tmp)
            temp.toFloat() / 1000000
        } catch (e: Exception) {
            log.error("解析浮点数出错:{}", e)
            0f
        }
    }

    private fun parseFloatFromBytes(data: ByteArray, startIndex: Int, length: Int): Float {
        return this.parseFloatFromBytes(data, startIndex, length, 0f)
    }

    private fun parseFloatFromBytes(data: ByteArray, startIndex: Int, length: Int, defaultVal: Float): Float {
        try {
            // 字节数大于4,从起始索引开始向后处理4个字节,其余超出部分丢弃
            val len = if (length > 4) 4 else length
            val tmp = ByteArray(len)
            System.arraycopy(data, startIndex, tmp, 0, len)
            return byte2Float(tmp)
        } catch (e: Exception) {
            log.error("解析浮点数出错:", e)
            return defaultVal
        }

    }
}