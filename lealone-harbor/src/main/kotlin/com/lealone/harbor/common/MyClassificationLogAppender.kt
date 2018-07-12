package com.lealone.harbor.common

import org.apache.log4j.DailyRollingFileAppender
import org.apache.log4j.Priority

public class MyClassificationLogAppender: DailyRollingFileAppender() {

     override fun isAsSevereAsThreshold(priority: Priority): Boolean {
        return this.getThreshold() == priority
    }
}