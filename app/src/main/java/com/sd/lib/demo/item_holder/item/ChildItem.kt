package com.sd.lib.demo.item_holder.item

import android.util.Log
import com.sd.lib.demo.item_holder.MainActivity

class ChildItem : IParent {
    override fun startRun() {
        Log.i(MainActivity.TAG, "startRun item:$this")
    }
}