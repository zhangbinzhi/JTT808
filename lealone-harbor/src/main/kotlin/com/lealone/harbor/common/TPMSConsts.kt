package com.lealone.harbor.common

import java.nio.charset.Charset

object TPMSConsts {
    const val string_encoding = "GBK"

    val string_charset = Charset.forName(string_encoding)
    // 标识位
    val pkg_delimiter :Byte= 0x7e
    // 客户端发呆15分钟后,服务器主动断开连接
    var tcp_client_idle_minutes = 30

    // 终端通用应答
    val msg_id_terminal_common_resp = 0x0001
    // 终端心跳
    val msg_id_terminal_heart_beat = 0x0002
    // 终端注册
    val msg_id_terminal_register = 0x0100
    // 终端注销
    val msg_id_terminal_log_out = 0x0003
    // 终端鉴权
    val msg_id_terminal_authentication = 0x0102
    // 位置信息汇报
    val msg_id_terminal_location_info_upload = 0x0200
    // 胎压数据透传
    val msg_id_terminal_transmission_tyre_pressure = 0x0600
    // 查询终端参数应答
    val msg_id_terminal_param_query_resp = 0x0104

    // 平台通用应答
    val cmd_common_resp = 0x8001
    // 终端注册应答
    val cmd_terminal_register_resp = 0x8100
    // 设置终端参数
    val cmd_terminal_param_settings = 0X8103
    // 查询终端参数
    val cmd_terminal_param_query = 0x8104

}