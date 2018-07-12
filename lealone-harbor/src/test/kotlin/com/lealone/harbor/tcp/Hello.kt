package com.lealone.harbor.tcp

fun main(args: Array<String>){
    val c  = Test()
    c.hello("sss")
}
class Test{

    fun hello( name: String){
        println("welcome $name")
    }
}