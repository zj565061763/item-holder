package com.sd.lib.itemholder

import android.app.Activity
import android.app.Application
import android.os.Bundle

internal class FActivityItemHolder(activity: Activity) : FItemHolder<Activity>(activity) {
    private val _activity = activity

    override fun init(): Boolean {
        if (_activity.isFinishing) return false
        with(_activity.application) {
            unregisterActivityLifecycleCallbacks(_lifecycleCallback)
            registerActivityLifecycleCallbacks(_lifecycleCallback)
        }
        return true
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
            if (_activity == activity) {
                _activity.application.unregisterActivityLifecycleCallbacks(this)
                destroy()
            }
        }
    }
}