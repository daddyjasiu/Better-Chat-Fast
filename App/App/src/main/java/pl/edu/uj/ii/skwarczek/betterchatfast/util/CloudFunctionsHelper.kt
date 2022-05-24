package pl.edu.uj.ii.skwarczek.betterchatfast.util

import com.google.firebase.functions.FirebaseFunctions

object CloudFunctionsHelper {

    val test = FirebaseFunctions.getInstance()

    fun testFun(){
        test.getHttpsCallable()
    }

}