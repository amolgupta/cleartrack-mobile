package xyz.getclear.android.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import xyz.getclear.domain.common.AnalyticsWrapper


class AnalyticsWrapperImpl constructor(val context: Context) : AnalyticsWrapper {

    private var firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)

    override fun logEvent(name: String) {
        firebaseAnalytics.logEvent(name, Bundle())
    }

    override fun logEvent(name: String, values: Map<String, String>) {
        val bundle = Bundle()
        values.forEach {
            bundle.putString(it.key, it.value)
        }
        firebaseAnalytics.logEvent(name, bundle)
    }
}
