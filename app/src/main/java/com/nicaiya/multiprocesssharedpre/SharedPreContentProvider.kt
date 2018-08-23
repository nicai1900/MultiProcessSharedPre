package com.nicaiya.multiprocesssharedpre

import android.content.ContentProvider
import android.content.ContentValues
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import java.io.Serializable

class SharedPreContentProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        return true
    }

    override fun call(method: String?, arg: String?, extras: Bundle?): Bundle? {
        if (extras == null) {
            throw IllegalArgumentException("extras must not be null")
        }
        val mode = extras.getInt(KEY_PRE_MODE)
        val name = extras.getString(KEY_PRE_NAME)
        if (name.isNullOrEmpty()) {
            throw IllegalArgumentException("pre name can not be null or empty")
        }
        // 在构造函数调用完成后，context不会为空
        val sp = context!!.getSharedPreferences(name, mode)
        return handleOp(sp, extras)
    }

    private fun handleOp(sp: SharedPreferences, extras: Bundle): Bundle? {
        val key = extras.getString(KEY_KEY)
        val op = extras.getInt(KEY_OP_TYPE)
        val result = Bundle()
        when (op) {
            OP_GET_ALL -> result.putSerializable(KEY_VALUE, HashMap(sp.all))
            OP_GET_STRING -> result.putString(
                KEY_VALUE,
                sp.getString(key, extras.getString(KEY_DEFAULT_VALUE))
            )
            OP_GET_INT -> result.putInt(
                KEY_VALUE, sp.getInt(key, extras.getInt(KEY_DEFAULT_VALUE))
            )
            OP_GET_LONG -> result.putLong(
                KEY_VALUE, sp.getLong(key, extras.getLong(KEY_DEFAULT_VALUE))
            )
            OP_GET_FLOAT -> result.putFloat(
                KEY_VALUE, sp.getFloat(key, extras.getFloat(KEY_DEFAULT_VALUE))
            )
            OP_GET_BOOLEAN -> result.putBoolean(
                KEY_VALUE, sp.getBoolean(key, extras.getBoolean(KEY_DEFAULT_VALUE))
            )
            OP_GET_STRING_SET -> {
                val defValues = extras.getSerializable(KEY_DEFAULT_VALUE) as Set<String>
                result.putSerializable(KEY_VALUE, sp.getStringSet(key, defValues) as Serializable)
            }
            OP_CONTAINS -> result.putBoolean(
                KEY_VALUE, sp.contains(key)
            )
            OP_APPLY, OP_COMMIT -> {
                val values = extras.getSerializable(KEY_VALUE) as HashMap<*, *>
                val editor = sp.edit()

                if (values.isEmpty()) {
                    editor.clear()
                } else {
                    for (entry in values.entries) {
                        val k = entry.key as String
                        val v = entry.value
                        when (v) {
                            is String -> editor.putString(k, v)
                            is Set<*> -> editor.putStringSet(k, v as Set<String>)
                            is Int -> editor.putInt(k, v)
                            is Long -> editor.putLong(k, v)
                            is Float -> editor.putFloat(k, v)
                            is Boolean -> editor.putBoolean(k, v)
                            null -> editor.remove(k)
                        }
                    }
                }
                if (op == OP_APPLY) {
                    editor.apply()
                    result.putBoolean(KEY_VALUE, true)
                } else {
                    result.putBoolean(KEY_VALUE, editor.commit())
                }
            }
        }
        return result
    }

    override fun insert(uri: Uri?, values: ContentValues?): Uri {
        throw UnsupportedOperationException("insert not support")
    }

    override fun query(
        uri: Uri?,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor {
        throw UnsupportedOperationException("query not support")
    }

    override fun update(
        uri: Uri?,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        throw UnsupportedOperationException("update not support")
    }

    override fun delete(uri: Uri?, selection: String?, selectionArgs: Array<out String>?): Int {
        throw UnsupportedOperationException("delete not support")
    }

    override fun getType(uri: Uri?): String {
        throw UnsupportedOperationException("getType not support")
    }

}