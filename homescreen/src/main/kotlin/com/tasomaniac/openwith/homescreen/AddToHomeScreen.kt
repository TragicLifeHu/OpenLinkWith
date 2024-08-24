package com.tasomaniac.openwith.homescreen

import android.app.Activity
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.tasomaniac.openwith.data.Analytics
import com.tasomaniac.openwith.homescreen.AddToHomeScreenDialogFragment.Companion.newInstance
import com.tasomaniac.openwith.redirect.RedirectFixActivity.Companion.createIntent
import com.tasomaniac.openwith.resolver.DisplayActivityInfo
import com.tasomaniac.openwith.resolver.ResolverActivity
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class AddToHomeScreen @Inject constructor(private val analytics: Analytics) : DaggerAppCompatActivity() {

    private inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
        SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val activityInfo = data.parcelable<DisplayActivityInfo>(ResolverActivity.RESULT_EXTRA_INFO)
                    val intent = data.parcelable<Intent>(ResolverActivity.RESULT_EXTRA_INTENT)
                    if (activityInfo != null && intent != null) {
                        newInstance(activityInfo, intent)
                            .show(supportFragmentManager)
                    } else {
                        finish()
                    }
                } else {
                    finish()
                }
            } else {
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analytics!!.sendScreenView("AddToHomeScreen")

        val foundUrl = intent.getStringExtra(Intent.EXTRA_TEXT)

        if (foundUrl != null) {
            val intent = createIntent(this, foundUrl)
                .putExtra(ResolverActivity.EXTRA_ADD_TO_HOME_SCREEN, true)
            startForResult.launch(intent)
        } else {
            Toast.makeText(this, R.string.error_invalid_url, Toast.LENGTH_SHORT).show()
        }
    }
}
