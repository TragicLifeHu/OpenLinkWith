package com.tasomaniac.openwith.settings.advanced.features

import android.os.Build.VERSION_CODES.M
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.preference.Preference
import com.tasomaniac.openwith.R
import com.tasomaniac.openwith.settings.Settings
import javax.inject.Inject

@RequiresApi(M)
class FeaturesListSettings @Inject constructor(
    private val featurePreferences: FeaturePreferences,
    fragment: FeaturesListFragment
) : Settings(fragment) {

    override fun setup() {
        addPreferencesFromResource(R.xml.pref_features)
    }

    override fun resume() {
        Feature.entries.forEach { feature ->
            val enabled = featurePreferences.isEnabled(feature)
            fragment.findPreference<Preference>(feature.prefKey)?.setSummary(enabled.toSummary())
        }
    }

    @StringRes
    private fun Boolean.toSummary() =
        if (this) R.string.pref_state_feature_enabled else R.string.pref_state_feature_disabled
}
