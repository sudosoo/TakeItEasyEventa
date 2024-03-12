package com.sudoSoo.takeItEasyEvent.common.service

import org.hibernate.exception.DataException

object CommonService {
    fun checkNotNullData(value : Any?, message : String){
        try{ requireNotNull(value) }
        catch (e : IllegalArgumentException){
            throw IllegalArgumentException("DataException : $message")
        }
    }

}