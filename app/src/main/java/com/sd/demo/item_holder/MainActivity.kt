package com.sd.demo.item_holder

import android.os.Bundle
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
        testGetItem()
        testPutItemByClass()
    }

    private fun testGetItem() {
        val item1 = FItemHolder.activity(this).get(TestItem::class.java)
        val item2 = FItemHolder.activity(this).get(TestItem::class.java)
        check(item1 === item2)
        item1.run()
    }

    private fun testPutItemByClass() {
        FItemHolder.activity(this).putItem(IParent::class.java, ChildItem())
        val item = FItemHolder.activity(this).query(IParent::class.java)
        item?.run()
    }
}

inline fun logMsg(block: () -> String) {
    Log.i("FItemHolder-demo", block())
}