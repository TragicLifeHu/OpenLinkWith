package com.tasomaniac.openwith.intro

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.tasomaniac.openwith.data.Analytics
import com.tasomaniac.openwith.extensions.restart
import com.tasomaniac.openwith.rx.SchedulingStrategy
import com.tasomaniac.openwith.settings.advanced.usage.UsageStats.isEnabled
import com.tasomaniac.openwith.settings.advanced.usage.UsageStats.observeAccessGiven
import com.tasomaniac.openwith.settings.advanced.usage.maybeStartUsageAccessSettings
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

class IntroActivity : AppIntro() {
    @JvmField @Inject
    var analytics: Analytics? = null

    @JvmField @Inject
    var schedulingStrategy: SchedulingStrategy? = null

    private val disposables = CompositeDisposable()

    private var usageStatsSlideAdded = false

    override fun init(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            analytics!!.sendScreenView("App Intro")
        }

        addPage(
            AppIntroFragment.Builder()
                .title(R.string.title_tutorial_0)
                .description(R.string.description_tutorial_0)
                .drawable(R.drawable.tutorial_0).build()
        )

        addPage(
            AppIntroFragment.Builder()
                .title(R.string.title_tutorial_1)
                .description(R.string.description_tutorial_1)
                .drawable(R.drawable.tutorial_1).build()
        )

        addPage(
            AppIntroFragment.Builder()
                .title(R.string.title_tutorial_2)
                .description(R.string.description_tutorial_2)
                .drawable(R.drawable.tutorial_2).build()
        )

        addPage(
            AppIntroFragment.Builder()
                .title(R.string.title_tutorial_3)
                .description(R.string.description_tutorial_3)
                .drawable(R.drawable.tutorial_3).build()
        )

        if (resources.getBoolean(R.bool.add_to_home_screen_enabled)) {
            addPage(
                AppIntroFragment.Builder()
                    .title(R.string.title_tutorial_4)
                    .description(R.string.description_tutorial_4)
                    .drawable(R.drawable.tutorial_4).build()
            )
        }

        if (!isEnabled(this)) {
            addUsageStatsSlide()
        }
    }

    private fun addUsageStatsSlide() {
        usageStatsSlideAdded = true
        addPage(
            AppIntroFragment.Builder()
                .title(R.string.title_tutorial_5)
                .description(R.string.description_tutorial_5)
                .drawable(R.drawable.tutorial_5).build()
        )

        setDoneText(getString(R.string.usage_access_give_access))
    }

    override fun onSkipPressed() {
        finish()
    }

    override fun onNextPressed() {
    }

    override fun onDonePressed() {
        if (usageStatsSlideAdded && !isEnabled(this)) {
            val success = this.maybeStartUsageAccessSettings()
            if (success) {
                observeUsageStats()
            } else {
                finish()
            }
        } else {
            finish()
        }
    }

    private fun observeUsageStats() {
        val disposable = observeAccessGiven(this)
            .compose(schedulingStrategy!!.forCompletable())
            .subscribe { this.restart() }
        disposables.add(disposable)
    }

    override fun onResume() {
        super.onResume()

        if (usageStatsSlideAdded && isEnabled(this)) {
            setDoneText(getString(R.string.done))
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (shouldTrackUsageAccess()) {
            analytics!!.sendEvent(
                "Usage Access",
                "Given in first intro",
                isEnabled(this).toString()
            )
        }
        disposables.clear()
    }

    private fun shouldTrackUsageAccess(): Boolean {
        return intent.getBooleanExtra(EXTRA_FIRST_START, false)
    }

    companion object {
        private const val EXTRA_FIRST_START = "first_start"

        fun newIntent(context: Context?): Intent {
            return Intent(context, IntroActivity::class.java)
                .putExtra(EXTRA_FIRST_START, true)
        }
    }
}
