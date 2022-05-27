package com.sd.lib.demo.item_holder.item

import android.app.Activity
import android.util.Log
import com.sd.lib.demo.item_holder.MainActivity
import com.sd.lib.itemholder.FItemHolder

class TestItem : FItemHolder.Item<Activity> {
    override fun init(target: Activity) {
        Log.i(MainActivity.TAG, "init:${target} item:$this")
    }

    fun sayHello() {
        Log.i(MainActivity.TAG, "sayHello item:$this")
    }

    override fun destroy() {
        Log.i(MainActivity.TAG, "destroy item:$this")
    }
}