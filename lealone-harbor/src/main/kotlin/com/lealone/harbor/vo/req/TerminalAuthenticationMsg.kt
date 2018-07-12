package com.lealone.harbor.vo.req

import com.lealone.harbor.vo.PackageData

class TerminalAuthenticationMsg(pkg: PackageData) : PackageData(pkg.msgHeader,pkg.socket) {
    init {
        this.checkSum = pkg.checkSum
        this.msgBodyBytes = pkg.msgBodyBytes
    }

    var authCode: String = ""
    override fun toString(): String {
        return "TerminalAuthenticationMsg(authCode=$authCode), msgHeader=$msgHeader"
    }

}