package com.lealone.harbor.utils

import org.apache.log4j.PropertyConfigurator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream

class Config {
    private var log4jInited = false
    init {
        initLog4j()
    }

//    fun getLogger(clazz: Class<*>): Logger {
//        return LoggerFactory.getLogger(clazz)
//    }

    @Synchronized
    fun initLog4j() {
        if (log4jInited)
            return
        log4jInited = true
        val log4jFile = System.getProperty("log4j")
        var inStream: InputStream? = null
        if (log4jFile != null) {
            try {
                inStream = FileInputStream(File(log4jFile))
            } catch (e: IOException) {
            }

        }
        // 要加"/"号，否则返回null
        if (inStream == null) {
            inStream = FileInputStream("./log4j.properties")
        }
        PropertyConfigurator.configure(inStream)
    }
    companion object
    {
        init {

        }
    }
}
