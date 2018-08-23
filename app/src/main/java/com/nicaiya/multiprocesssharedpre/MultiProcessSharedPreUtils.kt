package com.nicaiya.multiprocesssharedpre

import android.content.Context
import android.content.SharedPreferences

const val METHOD_MP = "multi_process"
const val KEY_PRE_NAME = "name"
const val KEY_PRE_MODE = "mode"

const val KEY_KEY = "key"
const val KEY_VALUE = "value"
const val KEY_DEFAULT_VALUE = "default_value"

const val KEY_OP_TYPE = "op_type"

const val OP_GET_ALL = 1
const val OP_GET_STRING = 2
const val OP_GET_INT = 3
const val OP_GET_LONG = 4
const val OP_GET_FLOAT = 5
const val OP_GET_BOOLEAN = 6
const val OP_GET_STRING_SET = 7
const val OP_CONTAINS = 8
const val OP_APPLY = 9
const val OP_COMMIT = 10

/**
 *
 * 获取可跨进程访问的SharedPreferences对象
 *
 * 如果是是Provider进程，则返回原生SharedPreferences
 * 如果不是Provider，则返回代理的 {@link SharedPreferencesProxy}
 *
 * @see SharedPreferences
 * @see SharedPreferencesProxy
 *
 */
fun getMultiProcessSharedPref(ctx: Context, name: String): SharedPreferences {
//    return if (ProcessUtils.isMainProcess(ctx)) {
//        ctx.getSharedPreferences(name, Context.MODE_PRIVATE)
//    } else {
      return  SharedPreferencesProxy(ctx, name, Context.MODE_PRIVATE)
    //}
}
