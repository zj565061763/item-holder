package com.sd.demo.item_holder.item

import com.sd.demo.item_holder.logMsg

class ChildItem : IParent, AutoCloseable {
    override fun run() {
        logMsg { "$this run" }
    }

    override fun close() {
        logMsg { "$this close" }
    }
}