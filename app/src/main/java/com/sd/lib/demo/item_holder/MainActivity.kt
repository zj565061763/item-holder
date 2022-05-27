package com.sd.lib.demo.item_holder

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.sd.lib.demo.item_holder.item.TestItem
import com.sd.lib.itemholder.FItemHolder

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        require(
            FItemHolder.activity(this).getOrCreateItem(TestItem::class.java)
                    == FItemHolder.activity(this).getOrCreateItem(TestItem::class.java)
        )

        val testItem = FItemHolder.activity(this).getOrCreateItem(TestItem::class.java)
        testItem.sayHello()

        FItemHolder.activity(this).putItem("ok")
        val stringItem = FItemHolder.activity(this).getItem(String::class.java)
        Log.i(TAG, "stringItem:$stringItem")
    }

    companion object {
        const val TAG = "MainActivity"
    }
}