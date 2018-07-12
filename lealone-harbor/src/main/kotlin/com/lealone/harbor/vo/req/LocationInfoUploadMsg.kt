package com.lealone.harbor.vo.req

import com.lealone.harbor.vo.PackageData

public class LocationInfoUploadMsg(pkg: PackageData) : PackageData(pkg.msgHeader,pkg.socket) {
    init {
        this.checkSum = pkg.checkSum
        this.msgBodyBytes = pkg.msgBodyBytes
    }

    // 告警信息
    // byte[0-3]
    var warningFlagField: Int = 0
    // byte[4-7] 状态(DWORD(32))
    var statusField: Int = 0
    // byte[8-11] 纬度(DWORD(32))
    var latitude: Float = 0.toFloat()
    // byte[12-15] 经度(DWORD(32))
    var longitude: Float = 0.toFloat()
    // byte[16-17] 高程(WORD(16)) 海拔高度，单位为米（ m）
    // TODO ==>int?海拔
    var elevation: Int = 0
    // byte[18-19] 速度(WORD) 1/10km/h
    // TODO ==>float?速度
    var speed: Float = 0.toFloat()
    // byte[20-21] 方向(WORD) 0-359，正北为 0，顺时针
    var direction: Int = 0
    // byte[22-x] 时间(BCD[6]) YY-MM-DD-hh-mm-ss
    // GMT+8 时间，本标准中之后涉及的时间均采用此时区
    var time: String? = null

    override fun toString(): String {
        return "LocationInfoUploadMsg(warningFlagField=$warningFlagField, statusField=$statusField, latitude=$latitude, longitude=$longitude, elevation=$elevation, speed=$speed, direction=$direction, time=$time)"
    }


}


