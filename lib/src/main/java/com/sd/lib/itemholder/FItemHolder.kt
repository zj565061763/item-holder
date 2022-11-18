package com.sd.lib.itemholder

import android.app.Activity
import android.content.Context
import java.lang.reflect.Modifier

abstract class FItemHolder<T> internal constructor(target: T) {
    private val _target: T
    private val _itemHolder: MutableMap<Class<*>, Any> = mutableMapOf()

    val isActive: Boolean
        get() {
            synchronized(Companion) {
                return MapHolder[_target!!] === this
            }
        }

    init {
        _target = target
    }

    /**
     * 获得Item，如果不存在则创建返回。
     */
    fun <I> get(clazz: Class<I>): I {
        require(!clazz.isInterface) { "class should not be an interface." }
        require(!Modifier.isAbstract(clazz.modifiers)) { "class should not be abstract." }
        synchronized(Companion) {
            val cache = _itemHolder[clazz]
            if (cache != null) return cache as I

            val item = clazz.newInstance()
            if (isActive) {
                if (item is Item) {
                    item.init()
                }
                _itemHolder[clazz] = item!!
            }
            return item
        }
    }

    /**
     * 查询[clazz]对应的Item
     */
    fun <I> query(clazz: Class<I>): I? {
        synchronized(Companion) {
            if (!isActive) return null
            val item = _itemHolder[clazz] ?: return null
            return item as I
        }
    }

    /**
     * 保存Item
     */
    fun putItem(item: Any) {
        synchronized(Companion) {
            if (!isActive) return
            _itemHolder[item.javaClass] = item
        }
    }

    /**
     * 保存Item
     */
    fun <I> putItem(clazz: Class<in I>, item: I) {
        synchronized(Companion) {
            if (!isActive) return
            _itemHolder[clazz] = item!!
        }
    }

    /**
     * 移除Item
     */
    fun removeItem(clazz: Class<*>) {
        synchronized(Companion) {
            _itemHolder.remove(clazz)
        }
    }

    /**
     * 初始化
     */
    internal fun internalInit(): Boolean {
        return init()
    }

    /**
     * 初始化
     */
    protected abstract fun init(): Boolean

    /**
     * 销毁当前对象
     */
    protected fun destroy() {
        synchronized(Companion) {
            if (!isActive) return

            _itemHolder.values.forEach {
                if (it is AutoCloseable) {
                    try {
                        it.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            _itemHolder.clear()

            MapHolder.remove(_target!!)
        }
    }

    interface Item : AutoCloseable {
        /**
         * 初始化
         */
        fun init()
    }

    companion object {
        private val MapHolder: MutableMap<Any, FItemHolder<*>> = mutableMapOf()

        /**
         * 返回activity对应的holder，在activity生命周期中返回的是同一个holder对象
         */
        @JvmStatic
        fun activity(target: Context): FItemHolder<Activity> {
            require(target is Activity) { "context should be instance of ${Activity::class.java}" }
            synchronized(this@Companion) {
                val cache = MapHolder[target]
                if (cache != null) return cache as FActivityItemHolder

                val holder = FActivityItemHolder(target)
                if (holder.internalInit()) {
                    MapHolder[target] = holder
                }
                return holder
            }
        }
    }
}