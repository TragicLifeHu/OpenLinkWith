package com.tasomaniac.openwith

import com.tasomaniac.openwith.data.Analytics
import com.tasomaniac.openwith.data.Analytics.DebugAnalytics
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
internal object AnalyticsModule {
    @Provides
    @Singleton
    fun provideAnalytics(): Analytics {
        return DebugAnalytics()
    }
}
