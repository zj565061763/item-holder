package com.sd.demo.item_holder.item

import com.sd.demo.item_holder.logMsg
import com.sd.lib.itemholder.FItemHolder

class TestItem : FItemHolder.Item {
    override fun init() {
        logMsg { "$this init" }
    }

    fun run() {
        logMsg { "$this run" }
    }

    override fun close() {
        logMsg { "$this close" }
    }
}