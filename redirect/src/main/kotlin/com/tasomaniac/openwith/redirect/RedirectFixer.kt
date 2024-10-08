package com.tasomaniac.openwith.redirect

import com.tasomaniac.openwith.rx.SchedulingStrategy
import io.reactivex.rxjava3.core.Single
import okhttp3.Call
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit.SECONDS
import javax.inject.Inject

class RedirectFixer(
    client: OkHttpClient,
    private val scheduling: SchedulingStrategy,
    private val timeoutInSec: Int
) {

    @Inject
    constructor(client: OkHttpClient, scheduling: SchedulingStrategy) : this(client, scheduling, DEFAULT_TIMEOUT_IN_SEC)

    private val client = client.newBuilder()
        .connectTimeout(2, SECONDS)
        .readTimeout(2, SECONDS)
        .writeTimeout(2, SECONDS)
        .followRedirects(false)
        .followSslRedirects(false)
        .build()

    private var call: Call? = null
    @Volatile private var lastUrl: HttpUrl? = null

    fun followRedirects(url: HttpUrl): Single<HttpUrl> {
        this.lastUrl = url
        return Single
            .fromCallable { doFollowRedirects(url) }
            .timeout(timeoutInSec.toLong(), SECONDS)
            .doOnError { call?.cancel() }
            .onErrorReturn { lastUrl }
            .doOnDispose { call?.cancel() }
            .compose(scheduling.forSingle())
    }

    private fun doFollowRedirects(url: HttpUrl): HttpUrl =
        fetchLocationHeader(url)?.toHttpUrlOrNull()
            ?.let {
                lastUrl = it
                doFollowRedirects(it)
            } ?: url

    private fun fetchLocationHeader(url: HttpUrl): String? {
        val call = client.newCall(request(url))
        this.call = call
        return try {
            call.execute().use { it.header("Location") }
        } catch (ignored: IOException) {
            null
        }
    }

    private fun request(httpUrl: HttpUrl) = Request.Builder().url(httpUrl).build()

    companion object {
        private const val DEFAULT_TIMEOUT_IN_SEC = 5
    }
}
