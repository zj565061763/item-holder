package com.sd.demo.item_holder.item

import android.app.Activity
import com.sd.demo.item_holder.logMsg
import com.sd.lib.itemholder.FItemHolder

class TestItem : FItemHolder.Item<Activity> {
    override fun init(target: Activity) {
        logMsg { "$this init:${target}" }
    }

    fun run() {
        logMsg { "$this run" }
    }

    override fun close() {
        logMsg { "$this close" }
    }
}