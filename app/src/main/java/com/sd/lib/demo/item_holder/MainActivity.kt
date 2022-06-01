package com.sd.lib.demo.item_holder

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.sd.lib.demo.item_holder.item.ApplicationItem
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
        testApp()
    }

    private fun testItem() {
        require(
            FItemHolder.activity(this).getOrCreateItem(TestItem::class.java)
                    === FItemHolder.activity(this).getOrCreateItem(TestItem::class.java)
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

    private fun testApp() {
        FItemHolder.app().putItem("app string")
        val item = FItemHolder.app().getItem(String::class.java)
        Log.i(TAG, "app stringItem:$item")

        require(
            FItemHolder.app().getOrCreateItem(ApplicationItem::class.java)
                    === FItemHolder.app().getOrCreateItem(ApplicationItem::class.java)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        Handler(Looper.getMainLooper()).postDelayed({
            require(FItemHolder.activity(this@MainActivity).isAttached.not())
            Log.i(TAG, "onDestroy check OK!")
        }, 3000)
    }

    companion object {
        const val TAG = "MainActivity"
    }
}