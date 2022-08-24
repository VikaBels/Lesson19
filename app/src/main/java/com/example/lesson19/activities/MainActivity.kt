package com.example.lesson19.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.lesson19.BROADCAST_ACTION_SEND_TEXT
import com.example.lesson19.EXTRA_RESULT_TEXT
import com.example.lesson19.databinding.ActivityMainBinding
import com.example.lesson19.tasts.CommunicatingWithServerTask

class MainActivity : AppCompatActivity() {
    private var bindingMain: ActivityMainBinding? = null

    private val serverResponseReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val serverResponse = intent.getStringExtra(EXTRA_RESULT_TEXT)
            onNewServerResponseReceive(serverResponse)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bindingMain = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingMain.root)

        this.bindingMain = bindingMain

        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            serverResponseReceiver,
            IntentFilter(BROADCAST_ACTION_SEND_TEXT)
        )
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(serverResponseReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        bindingMain = null
    }

    private fun setupListeners() {
        bindingMain?.buttonSend?.setOnClickListener {
            changeVisibilityProgressBar(true)
            startServerTask()
        }
    }

    private fun startServerTask() {
        val communicatingWithServerTask =
            CommunicatingWithServerTask(bindingMain?.editTextRequest?.text?.toString())
        communicatingWithServerTask.execute()
    }

    private fun onNewServerResponseReceive(response: String?) {
        if (!response.isNullOrEmpty()) {
            bindingMain?.textViewResponse?.text = response
            changeVisibilityProgressBar(false)
        }
    }

    private fun changeVisibilityProgressBar(isVisible: Boolean) {
        bindingMain?.progressBar?.isVisible = isVisible
    }
}