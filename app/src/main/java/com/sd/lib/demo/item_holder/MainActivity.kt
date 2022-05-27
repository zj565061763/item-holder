package com.sd.lib.demo.item_holder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sd.lib.demo.item_holder.item.TestItem
import com.sd.lib.itemholder.FItemHolder

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val testItem = FItemHolder.activity(this).getOrCreateItem(TestItem::class.java)
        testItem.sayHello()
    }

    companion object {
        const val TAG = "MainActivity"
    }
}