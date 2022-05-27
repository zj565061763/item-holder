package com.sd.lib.itemholder

import android.app.Activity
import android.content.Context
import com.sd.lib.itemholder.impl.FActivityItemHolder
import java.lang.ref.WeakReference
import java.lang.reflect.Modifier
import java.util.*

open class FItemHolder<T> {
    private val _mapItemHolder = mutableMapOf<Class<out Item<T>>, Item<T>>()
    private val _targetRef: WeakReference<T>

    constructor(target: T) {
        requireNotNull(target) { "target is null" }
        _targetRef = WeakReference(target)
    }

    protected fun getTarget(): T? {
        return _targetRef.get()
    }

    /**
     * 获取Item
     */
    @Synchronized
    fun <I : Item<T>> getItem(clazz: Class<I>): I? {
        checkItemClass(clazz)
        val item = _mapItemHolder[clazz] ?: return null
        return item as I
    }

    /**
     * 获得Item，如果不存在则创建返回
     */
    @Synchronized
    fun <I : Item<T>> getOrCreateItem(clazz: Class<I>): I {
        checkItemClass(clazz)
        val cache = _mapItemHolder[clazz]
        if (cache != null) return cache as I

        val item = createItem(clazz)
        requireNotNull(item) { "createItem return null" }

        _mapItemHolder[clazz] = item
        val target = getTarget()
        if (target != null) {
            initItem(item, target)
        }
        return item
    }

    /**
     * 清空所有Item
     */
    @Synchronized
    fun clearItem() {
        _mapItemHolder.values.forEach {
            it.destroy()
        }
        _mapItemHolder.clear()
    }

    /**
     * 销毁当前对象
     */
    protected fun destroy() {
        remove(getTarget())
        clearItem()
    }

    /**
     * 初始化Item
     */
    protected open fun <I : Item<T>> initItem(item: I, target: T) {
        item.init(target)
    }

    private fun <I : Item<T>> createItem(clazz: Class<I>): I? {
        try {
            return clazz.newInstance()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        }
        return null
    }

    private fun <I : Item<*>> checkItemClass(clazz: Class<I>) {
        require(!clazz.isInterface) { "class must not be an interface" }
        require(!Modifier.isAbstract(clazz.modifiers)) { "class must not be abstract" }
    }

    interface Item<T> {
        /**
         * 初始化
         */
        fun init(target: T)
        /**
         * 销毁
         */
        fun destroy()
    }

    companion object {
        private val MAP_HOLDER = WeakHashMap<Any, FItemHolder<*>>()

        @JvmStatic
        @Synchronized
        fun activity(context: Context): FItemHolder<Activity> {
            require(context is Activity) { "context must be ${Activity::class.java}" }
            val cache = MAP_HOLDER[context]
            if (cache != null) return cache as FActivityItemHolder

            val holder = FActivityItemHolder(context)
            if (!context.isFinishing) {
                MAP_HOLDER[context] = holder
            }
            return holder
        }

        @JvmStatic
        @Synchronized
        private fun remove(target: Any?) {
            if (target != null) {
                MAP_HOLDER.remove(target)
            }
        }
    }
}