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
        testTarget()
        testApp()
//        testCrash()
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

    private fun testTarget() {
        FItemHolder.target("1").let {
            it.attach()
            it.putItem("target string")
        }

        val item = FItemHolder.target("1").getItem(String::class.java)
        Log.i(TAG, "target stringItem:$item")
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

    private fun testCrash() {
        val holder1 = FItemHolder.target(TAG)
        val holder2 = FItemHolder.target(TAG)

        require(holder1.isAttached.not())
        require(holder2.isAttached.not())
        require(holder1 !== holder2)

        holder1.attach()
        holder2.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        /**
         * 在合适的时机detach掉通过[FItemHolder.target]方法创建的holder，否则该对象会一直被持有
         */
        FItemHolder.target("1").detach()

        Handler(Looper.getMainLooper()).postDelayed({
            require(FItemHolder.activity(this@MainActivity).isAttached.not())
            Log.i(TAG, "onDestroy check OK!")
        }, 3000)
    }

    companion object {
        const val TAG = "MainActivity"
    }
}