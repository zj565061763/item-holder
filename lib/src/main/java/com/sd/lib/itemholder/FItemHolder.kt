package com.sd.lib.itemholder

import android.app.Activity
import android.content.Context
import com.sd.lib.itemholder.impl.FActivityItemHolder
import java.lang.reflect.Modifier
import java.util.*

open class FItemHolder<T>(target: T) {
    private val _target: T
    private val _mapItemHolder = mutableMapOf<Class<*>, Any>()

    /**
     * 当前对象是否已经被销毁
     */
    @Volatile
    var isDestroyed = false
        private set(value) {
            require(value)
            field = value
        }

    init {
        _target = target
    }

    protected fun getTarget(): T {
        return _target
    }

    /**
     * 获取Item
     */
    @Synchronized
    fun <I> getItem(clazz: Class<I>): I? {
        val item = _mapItemHolder[clazz] ?: return null
        return item as I
    }

    /**
     * 保存Item
     */
    @Synchronized
    fun putItem(item: Any) {
        _mapItemHolder[item.javaClass] = item
    }

    /**
     * 保存Item
     */
    @Synchronized
    fun <I> putItem(clazz: Class<in I>, item: I) {
        _mapItemHolder[clazz] = item!!
    }

    /**
     * 获得Item，如果不存在则创建返回
     */
    @Synchronized
    fun <I : Item<T>> getOrCreateItem(clazz: Class<I>): I {
        require(!clazz.isInterface) { "class should not be an interface." }
        require(!Modifier.isAbstract(clazz.modifiers)) { "class should not be abstract." }

        val cache = _mapItemHolder[clazz]
        if (cache != null) return cache as I

        val item = createItem(clazz)
        _mapItemHolder[clazz] = item

        if (!isDestroyed) {
            initItem(item, _target)
        }
        return item
    }

    /**
     * 创建Item
     */
    protected open fun <I : Item<T>> createItem(clazz: Class<I>): I {
        return clazz.newInstance()
    }

    /**
     * 初始化Item
     */
    protected open fun <I : Item<T>> initItem(item: I, target: T) {
        item.init(target)
    }

    /**
     * 清空所有Item
     */
    @Synchronized
    fun clearItem() {
        _mapItemHolder.values.forEach {
            if (it is AutoCloseable) {
                try {
                    it.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        _mapItemHolder.clear()
    }

    /**
     * 销毁当前对象。
     * 如果自定义了子类，需要在合适的时机调用销毁，否则当前对象会一直被[MAP_HOLDER]持有。
     */
    protected open fun destroy() {
        if (isDestroyed) return

        /**
         * 销毁之后不需要重置[_target]为null，因为[MAP_HOLDER]已经不持有当前对象了。
         * 如果外部持有了当前对象，要自己注意不要延长当前对象的生命周期，导致内存泄露
         */
        isDestroyed = true
        remove(_target)
        clearItem()
    }


    interface Item<T> : AutoCloseable {
        /**
         * 初始化
         */
        fun init(target: T)
    }

    companion object {
        private val MAP_HOLDER = WeakHashMap<Any, FItemHolder<*>>()

        @JvmStatic
        @Synchronized
        fun activity(target: Context): FItemHolder<Activity> {
            require(target is Activity) { "target must be instance of ${Activity::class.java}" }
            val cache = MAP_HOLDER[target]
            if (cache != null) return cache as FActivityItemHolder

            val holder = FActivityItemHolder(target)
            if (!target.isFinishing) {
                MAP_HOLDER[target] = holder
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