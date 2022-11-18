package com.sd.demo.item_holder

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.sd.demo.item_holder.item.ChildItem
import com.sd.demo.item_holder.item.IParent
import com.sd.demo.item_holder.item.TestItem
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
            FItemHolder.activity(this).get(TestItem::class.java)
                    === FItemHolder.activity(this).get(TestItem::class.java)
        )
        val testItem = FItemHolder.activity(this).get(TestItem::class.java)
        testItem.sayHello()
    }

    private fun testPutItem() {
        FItemHolder.activity(this).putItem("ok")
        val item = FItemHolder.activity(this).query(String::class.java)
        Log.i(TAG, "stringItem:$item")
    }

    private fun testPutItemByClass() {
        FItemHolder.activity(this).putItem(IParent::class.java, ChildItem())
        val item = FItemHolder.activity(this).query(IParent::class.java)
        item?.startRun()
    }

    override fun onDestroy() {
        super.onDestroy()
        Handler(Looper.getMainLooper()).postDelayed({
            require(FItemHolder.activity(this@MainActivity).isActive.not())
            Log.i(TAG, "onDestroy check OK!")
        }, 3000)
    }

    companion object {
        const val TAG = "MainActivity"
    }
}