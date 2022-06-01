package com.sd.lib.itemholder

import android.app.Activity
import android.content.Context
import com.sd.lib.itemholder.impl.FActivityItemHolder
import java.lang.reflect.Modifier

open class FItemHolder<T>(target: T) {
    private val _target: T
    private val _mapItemHolder = mutableMapOf<Class<*>, Any>()

    /**
     * 当前对象是否已经添加到[MAP_HOLDER]
     */
    @Volatile
    var isAttached = false
        private set

    init {
        _target = target
    }

    protected fun getTarget(): T {
        return _target
    }

    /**
     * 获取Item
     */
    fun <I> getItem(clazz: Class<I>): I? {
        val item = _mapItemHolder[clazz] ?: return null
        return item as I
    }

    /**
     * 保存Item
     */
    @Synchronized
    fun putItem(item: Any): Boolean {
        if (isAttached) {
            _mapItemHolder[item.javaClass] = item
            return true
        }
        return false
    }

    /**
     * 保存Item
     */
    @Synchronized
    fun <I> putItem(clazz: Class<in I>, item: I): Boolean {
        if (isAttached) {
            _mapItemHolder[clazz] = item!!
            return true
        }
        return false
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
        if (isAttached) {
            if (initItem(item, _target)) {
                _mapItemHolder[clazz] = item
            }
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
    protected open fun <I : Item<T>> initItem(item: I, target: T): Boolean {
        item.init(target)
        return true
    }

    /**
     * 将当前对象添加到[MAP_HOLDER]
     */
    @Synchronized
    fun attach(): Boolean {
        if (isAttached) return true
        if (onAttach()) {
            addHolder(this)
            isAttached = true
        }
        return isAttached
    }

    /**
     * 将当前对象，从[MAP_HOLDER]移除，会清空所有Item。
     * 子类需要在合适的时机调用销毁，否则当前对象会一直被持有。
     */
    @Synchronized
    fun detach() {
        if (!isAttached) return

        /**
         * 销毁之后不需要重置[_target]为null，因为[MAP_HOLDER]已经不持有当前对象了。
         * 外部不应该主动持有当前对象，延长当前对象的生命周期。
         */
        isAttached = false
        removeHolder(this)

        /**
         * 销毁逻辑执行之前触发，允许子类在回调中获取Item，做一些额外的同步操作。
         */
        onDetach()
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
     * 当前对象即将被添加到[MAP_HOLDER]，返回true-继续添加，返回false-停止添加
     */
    protected open fun onAttach(): Boolean {
        return true
    }

    /**
     * 当前对象被移除回调
     */
    protected open fun onDetach() {

    }

    interface Item<T> : AutoCloseable {
        /**
         * 初始化
         */
        fun init(target: T)
    }

    companion object {
        private val MAP_HOLDER = mutableMapOf<Any, FItemHolder<*>>()

        @JvmStatic
        fun activity(target: Context): FItemHolder<Activity> {
            require(target is Activity) { "context should be instance of ${Activity::class.java}" }
            val cache = MAP_HOLDER[target]
            if (cache != null) return cache as FActivityItemHolder
            return FActivityItemHolder(target).also { it.attach() }
        }

        /**
         * 返回target对应的holder
         */
        @JvmStatic
        fun <T : Any> target(target: T): FItemHolder<T> {
            require(target !is Activity) { "You should use activity() instead" }
            val cache = MAP_HOLDER[target]
            if (cache != null) return cache as FItemHolder<T>
            return FItemHolder(target).also { it.attach() }
        }

        /**
         * 添加holder
         */
        private fun addHolder(holder: FItemHolder<*>) {
            val target = holder._target!!
            synchronized(MAP_HOLDER) {
                val oldHolder = MAP_HOLDER[target]
                if (oldHolder != null && oldHolder !== holder) {
                    throw RuntimeException("target holder has been attached.")
                }
                MAP_HOLDER[target] = holder
            }
        }

        /**
         * 移除holder
         */
        private fun removeHolder(holder: FItemHolder<*>) {
            synchronized(MAP_HOLDER) {
                MAP_HOLDER.remove(holder._target)
            }
        }
    }
}