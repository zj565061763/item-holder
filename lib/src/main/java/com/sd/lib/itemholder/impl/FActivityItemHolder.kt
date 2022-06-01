package com.sd.lib.itemholder.impl

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.sd.lib.itemholder.FItemHolder

internal class FActivityItemHolder : FItemHolder<Activity> {
    constructor(activity: Activity) : super(activity) {
        if (!activity.isFinishing) {
            activity.application.registerActivityLifecycleCallbacks(_lifecycleCallback)
        }
    }

    override fun <I : Item<Activity>> initItem(item: I, target: Activity): Boolean {
        if (target.isFinishing) return false
        return super.initItem(item, target)
    }

    private val _lifecycleCallback = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        }

        override fun onActivityStarted(activity: Activity) {
        }

        override fun onActivityResumed(activity: Activity) {
        }

        override fun onActivityPaused(activity: Activity) {
        }

        override fun onActivityStopped(activity: Activity) {
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }

        override fun onActivityDestroyed(activity: Activity) {
            if (getTarget() == activity) {
                activity.application.unregisterActivityLifecycleCallbacks(this)
                detach()
            }
        }
    }
}