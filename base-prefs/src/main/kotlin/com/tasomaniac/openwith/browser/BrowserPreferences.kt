package com.tasomaniac.openwith.browser

import android.content.ComponentName
import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject

class BrowserPreferences @Inject constructor(private val sharedPreferences: SharedPreferences) {

    var mode: Mode
        set(value) {
            sharedPreferences.edit {
                putString(KEY, value.value)
                if (value is Mode.Browser) {
                    putString(KEY_BROWSER_NAME, value.displayLabel)
                    putString(KEY_BROWSER_COMPONENT, value.componentName.flattenToString())
                } else {
                    remove(KEY_BROWSER_NAME)
                    remove(KEY_BROWSER_COMPONENT)
                }
            }
        }
        get() {
            return when (val value = sharedPreferences.getString(KEY, null)) {
                null, "always_ask" -> Mode.AlwaysAsk
                "none" -> Mode.None
                "browser" -> Mode.Browser(browserName!!, componentName)
                else -> error("Unknown when checking browser mode: $value")
            }
        }

    private val browserName: String?
        get() = sharedPreferences.getString(KEY_BROWSER_NAME, null)

    private val componentName: ComponentName
        get() {
            val browserComponent = sharedPreferences.getString(KEY_BROWSER_COMPONENT, null)!!
            return ComponentName.unflattenFromString(browserComponent)!!
        }

    sealed class Mode(val value: String) {

        data object None : Mode("none")
        data object AlwaysAsk : Mode("always_ask")
        data class Browser(val displayLabel: String, val componentName: ComponentName) : Mode("browser")
    }

    companion object {
        private const val KEY = "pref_browser"
        private const val KEY_BROWSER_NAME = "pref_browser_name"
        private const val KEY_BROWSER_COMPONENT = "pref_browser_component"
    }
}
