package com.nicaiya.multiprocesssharedpre

import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import java.io.Serializable

/**
 * 实现SharedPreferences接口
 *
 * @author zheng.jie
 */
internal class SharedPreferencesProxy(
    context: Context,
    name: String,
    mode: Int
) : SharedPreferences {

    private val mContext = context
    private val mName = name
    private val mMode = mode

    companion object {
        private val TAG = SharedPreferencesProxy::class.java.simpleName
    }

    private var authority: String? = null
    private var authorityUri: Uri? = null

    override fun contains(key: String): Boolean {
        return call(key, OP_CONTAINS, Bundle()) as Boolean
    }

    override fun getBoolean(key: String, defValue: Boolean): Boolean {
        val arg = Bundle()
        arg.putBoolean(KEY_DEFAULT_VALUE, defValue)
        return call(key, OP_GET_BOOLEAN, arg) as Boolean
    }

    override fun getInt(key: String, defValue: Int): Int {
        val arg = Bundle()
        arg.putInt(KEY_DEFAULT_VALUE, defValue)
        return call(key, OP_GET_INT, arg) as Int
    }

    override fun getAll(): Map<String, Any?>? {
        return call(null, OP_GET_ALL, Bundle()) as Map<String, Any?>?
    }

    override fun edit(): SharedPreferences.Editor {
        return EditorImpl()
    }

    override fun getLong(key: String, defValue: Long): Long {
        val arg = Bundle()
        arg.putLong(KEY_DEFAULT_VALUE, defValue)
        return call(key, OP_GET_LONG, arg) as Long
    }

    override fun getFloat(key: String, defValue: Float): Float {
        val arg = Bundle()
        arg.putFloat(KEY_DEFAULT_VALUE, defValue)
        return call(key, OP_GET_FLOAT, arg) as Float
    }

    override fun getStringSet(key: String, defValues: Set<String>?): Set<String>? {
        val arg = Bundle()
        arg.putSerializable(KEY_DEFAULT_VALUE, defValues as Serializable)
        return call(key, OP_GET_STRING_SET, arg) as Set<String>?
    }

    override fun getString(key: String, defValue: String?): String? {
        val arg = Bundle()
        arg.putString(KEY_DEFAULT_VALUE, defValue)
        return call(key, OP_GET_STRING, arg) as String
    }

    override fun unregisterOnSharedPreferenceChangeListener(
        listener: SharedPreferences.OnSharedPreferenceChangeListener?
    ) {
        throw UnsupportedOperationException("not support unregister change listener")
    }

    override fun registerOnSharedPreferenceChangeListener(
        listener: SharedPreferences.OnSharedPreferenceChangeListener?
    ) {
        throw UnsupportedOperationException("not support register change listener")
    }

    private fun call(key: String?, opType: Int, arg: Bundle): Any? {
        val uri = getAuthorityUri(mContext, mName)
        arg.putString(KEY_PRE_NAME, mName)
        arg.putInt(KEY_PRE_MODE, mMode)
        arg.putInt(KEY_OP_TYPE, opType)
        arg.putString(KEY_KEY, key)
        return try {
            mContext.contentResolver.call(uri, METHOD_MP, null, arg)?.get(KEY_VALUE)
        } catch (e: Throwable) {
            Log.e(TAG, "call error:", e)
            null
        }
    }

    private fun getAuthorityUri(context: Context, name: String): Uri {
        if (authorityUri == null) {
            if (authority == null) {
                authority = context.packageName + ".multi_process_shared_pref"
            }
            authorityUri = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + authority)
        }
        return Uri.withAppendedPath(authorityUri, name)
    }

    inner class EditorImpl : SharedPreferences.Editor {

        private val mModified = HashMap<String, Any?>()

        override fun putLong(key: String, value: Long): SharedPreferences.Editor {
            mModified[key] = value
            return this
        }

        override fun putInt(key: String, value: Int): SharedPreferences.Editor {
            mModified[key] = value
            return this
        }

        override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor {
            mModified[key] = value
            return this
        }

        override fun putFloat(key: String, value: Float): SharedPreferences.Editor {
            mModified[key] = value
            return this
        }

        override fun putString(key: String, value: String?): SharedPreferences.Editor {
            mModified[key] = value
            return this
        }

        override fun putStringSet(
            key: String,
            values: MutableSet<String>?
        ): SharedPreferences.Editor {
            mModified[key] = values
            return this
        }

        override fun clear(): SharedPreferences.Editor {
            mModified.clear()
            return this
        }

        override fun remove(key: String): SharedPreferences.Editor {
            mModified.remove(key)
            return this
        }

        override fun commit(): Boolean {
            return store(OP_COMMIT)
        }

        override fun apply() {
            store(OP_APPLY)
        }

        private fun store(opType: Int): Boolean {
            val extras = Bundle()
            extras.putSerializable(KEY_VALUE, mModified)
            extras.putInt(KEY_OP_TYPE, opType)
            return call(null, opType, extras) as Boolean
        }

    }

}