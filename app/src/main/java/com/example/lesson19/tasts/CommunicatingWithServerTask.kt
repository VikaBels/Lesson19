package com.example.lesson19.tasts

import android.content.Intent
import android.content.res.Resources
import android.os.AsyncTask
import android.text.Editable
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.lesson19.BROADCAST_ACTION_SEND_TEXT
import com.example.lesson19.EXTRA_RESULT_TEXT
import com.example.lesson19.R
import com.example.lesson19.models.App.Companion.getInstanceApp
import okhttp3.*
import java.io.IOException
import java.lang.Exception

class CommunicatingWithServerTask(
    private val textParameter: String?
) : AsyncTask<Void?, String?, Void?>() {
    companion object {
        const val URL_WITHOUT_PARAMETER =
            "https://pub.zame-dev.org/senla-training-addition/lesson-19.php?param="
        const val TAG_INTERRUPTED_EXCEPTION = "CommunicatingWithTask"
        const val MESSAGE_INTERRUPTED_EXCEPTION = "Server request error"
    }

    private val client = OkHttpClient()

    override fun onProgressUpdate(vararg values: String?) {
        super.onProgressUpdate(*values)
        sendMessage(values.joinToString())
    }

    override fun doInBackground(vararg p0: Void?): Void? {
        try {
            getResponseFromServer()
        } catch (ex: InterruptedException) {
            Log.e(TAG_INTERRUPTED_EXCEPTION, MESSAGE_INTERRUPTED_EXCEPTION, ex)
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
            val response = client.newCall(getRequest()).execute()

            if (response.isSuccessful) {
                response.body()?.string()
            } else {
                response.code().toString()
            }

        } catch (e: IOException) {
            val resource = getInstanceApp().resources
            resource.getString(
                R.string.txt_error_no_internet
            )
        }

        publishProgress(resultServerText)
    }

    private fun getRequest(): Request {
        val urlWithParameter = "$URL_WITHOUT_PARAMETER$textParameter"

        return Request.Builder()
            .url(urlWithParameter)
            .build()
    }
}