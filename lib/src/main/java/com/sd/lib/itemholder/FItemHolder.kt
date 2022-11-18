package com.sd.lib.itemholder

import android.app.Activity
import android.content.Context
import java.lang.reflect.Modifier

open class FItemHolder<T> internal constructor(target: T) {
    private val _target: T
    private val _mapItemHolder = mutableMapOf<Class<*>, Any>()

    /**
     * 当前对象是否已经添加到[MapHolder]
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
    fun <I> getOrNull(clazz: Class<I>): I? {
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
     * 移除Item
     */
    fun removeItem(item: Any) {
        removeItem(item.javaClass)
    }

    /**
     * 移除Item
     */
    @Synchronized
    fun removeItem(clazz: Class<*>) {
        _mapItemHolder.remove(clazz)
    }

    /**
     * 获得Item，如果不存在则创建返回。
     * 注意：如果[isAttached]为false，则创建的Item未初始化
     */
    @Synchronized
    fun <I : Item<T>> get(clazz: Class<I>): I {
        require(!clazz.isInterface) { "class should not be an interface." }
        require(!Modifier.isAbstract(clazz.modifiers)) { "class should not be abstract." }

        val cache = _mapItemHolder[clazz]
        if (cache != null) return cache as I

        val item = createItem(clazz)
        if (isAttached) {
            item.init(_target)
            _mapItemHolder[clazz] = item
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
     * 将当前对象添加到[MapHolder]
     */
    fun attach(): Boolean {
        if (isAttached) return true

        synchronized(MapHolder) {
            val target = _target!!
            if (MapHolder.containsKey(target)) {
                throw RuntimeException("there is a holder has attached with target:$target")
            }

            if (onAttach()) {
                MapHolder[target] = this
                isAttached = true
            }
        }
        return isAttached
    }

    /**
     * 将当前对象，从[MapHolder]中移除，并清空所有Item。
     * 子类需要在合适的时机调用销毁，否则当前对象会一直被持有。
     */
    @Synchronized
    fun detach() {
        if (!isAttached) return

        /**
         * 销毁之后不需要重置[_target]为null，因为[MapHolder]已经不持有当前对象了。
         * 外部不应该主动持有当前对象，延长当前对象的生命周期。
         */
        isAttached = false

        _mapItemHolder.values.forEach {
            if (it is AutoCloseable) {
                try {
                    /**
                     * 如果Item在关闭的过程中，调用[attach]会失败，因为当前holder对象还在[MapHolder]中
                     */
                    it.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        _mapItemHolder.clear()

        try {
            onDetach()
        } finally {
            synchronized(MapHolder) {
                MapHolder.remove(_target!!)
            }
        }
    }

    /**
     * 当前对象即将被添加到[MapHolder]，返回true-继续添加，返回false-停止添加
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
        private val MapHolder = mutableMapOf<Any, FItemHolder<*>>()

        /**
         * 返回activity对应的holder，对象第一次被创建的时候会触发[attach]，并在activity销毁的时候触发[detach]。
         * 在activity的整个生命周期中，此方法返回的是与该activity绑定的同一个holder对象，直到该holder被[detach]。
         */
        @JvmStatic
        fun activity(target: Context): FItemHolder<Activity> {
            require(target is Activity) { "context should be instance of ${Activity::class.java}" }
            val cache = MapHolder[target]
            if (cache != null) return cache as FActivityItemHolder
            return FActivityItemHolder(target).also { it.attach() }
        }
    }
}