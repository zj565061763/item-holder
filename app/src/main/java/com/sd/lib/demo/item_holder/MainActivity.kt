package com.sd.lib.demo.item_holder

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.sd.lib.demo.item_holder.item.ChildItem
import com.sd.lib.demo.item_holder.item.IParent
import com.sd.lib.demo.item_holder.item.TestItem
import com.sd.lib.itemholder.FItemHolder

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        testItem()
        testPutItem()
        testPutItemByClass()
    }

    private fun testItem() {
        require(
            FItemHolder.activity(this).getOrCreateItem(TestItem::class.java)
                    == FItemHolder.activity(this).getOrCreateItem(TestItem::class.java)
        )
        val testItem = FItemHolder.activity(this).getOrCreateItem(TestItem::class.java)
        testItem.sayHello()
    }

    private fun testPutItem() {
        FItemHolder.activity(this).putItem("ok")
        val item = FItemHolder.activity(this).getItem(String::class.java)
        Log.i(TAG, "stringItem:$item")
    }

    private fun testPutItemByClass() {
        FItemHolder.activity(this).putItem(IParent::class.java, ChildItem())
        val item = FItemHolder.activity(this).getItem(IParent::class.java)
        item?.startRun()
    }

    companion object {
        const val TAG = "MainActivity"
    }
}