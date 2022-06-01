package com.sd.lib.demo.item_holder.item

import android.app.Application
import android.util.Log
import com.sd.lib.demo.item_holder.MainActivity
import com.sd.lib.itemholder.FItemHolder

class ApplicationItem : FItemHolder.Item<Application> {
    override fun init(target: Application) {
        Log.i(MainActivity.TAG, "init:${target} item:$this")
    }

    override fun close() {
        Log.i(MainActivity.TAG, "close item:$this")
    }
}