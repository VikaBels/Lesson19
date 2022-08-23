package com.example.lesson19.tasts

import android.content.Intent
import android.content.res.Resources
import android.os.AsyncTask
import android.text.Editable
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.lesson19.BROADCAST_ACTION_SEND_TEXT
import com.example.lesson19.EXTRA_RESULT_TEXT
import com.example.lesson19.models.App.Companion.getInstanceApp
import okhttp3.*
import java.io.IOException

class CommunicatingWithServerTask(
    private val textParameter: Editable?
) : AsyncTask<Void?, String?, Void?>() {
    companion object {
        const val URL_WITHOUT_PARAMETER =
            "https://pub.zame-dev.org/senla-training-addition/lesson-19.php?param="
        const val NAME_RESOURCE_ERROR = "txt_error_no_internet"
        const val DEFAULT_TYPE_RESOURCE_ERROR = "string"
    }

    override fun onProgressUpdate(vararg values: String?) {
        super.onProgressUpdate(*values)
        sendMessage(values.joinToString())
    }

    override fun doInBackground(vararg p0: Void?): Void? {
        try {
            getResponseFromServer()
        } catch (ex: InterruptedException) {
            println(ex)
        }
        return null
    }

    private fun sendMessage(value: String) {
        val intent = Intent(BROADCAST_ACTION_SEND_TEXT)
        intent.putExtra(EXTRA_RESULT_TEXT, value)
        LocalBroadcastManager.getInstance(getInstanceApp()).sendBroadcast(intent)
    }

    private fun getResponseFromServer() {
        val resultServerText: String? = try {
            val response = OkHttpClient().newCall(getRequest()).execute()

            if (response.isSuccessful) {
                response.body()?.string()
            } else {
                response.code().toString()
            }

        } catch (e: IOException) {
            val resource: Resources = getInstanceApp().resources
            resource.getString(
                resource.getIdentifier(
                    NAME_RESOURCE_ERROR,
                    DEFAULT_TYPE_RESOURCE_ERROR,
                    getInstanceApp().packageName
                )
            )
        }

        publishProgress(resultServerText)
    }

    private fun getRequest(): Request {
        val urlWithParameter = buildString {
            append(URL_WITHOUT_PARAMETER)
            append(textParameter)
        }

        return Request.Builder()
            .url(urlWithParameter)
            .build()
    }
}