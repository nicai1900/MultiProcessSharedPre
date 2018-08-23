package com.nicaiya.multiprocesssharedpre

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.ushowmedia.framework.data.preference.getMultiProcessSharedPref

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        findViewById<Button>(R.id.bt_test).setOnClickListener { testSave() }
    }


    fun testSave() {
        val sp = getMultiProcessSharedPref(this, "test")

        sp.edit().putBoolean("tb", true).putString("ss", "name").apply()

    }
}
