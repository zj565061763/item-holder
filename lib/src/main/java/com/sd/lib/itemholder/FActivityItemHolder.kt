package com.sd.lib.itemholder

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.sd.lib.itemholder.FItemHolder

internal class FActivityItemHolder(activity: Activity) : FItemHolder<Activity>(activity) {

    override fun onAttach(): Boolean {
        if (getTarget().isFinishing) return false
        getTarget().application.registerActivityLifecycleCallbacks(_lifecycleCallback)
        return super.onAttach()
    }

    override fun onDetach() {
        super.onDetach()
        getTarget().application.unregisterActivityLifecycleCallbacks(_lifecycleCallback)
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
                detach()
            }
        }
    }
}