package com.funshow.rtmp_userootencoder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.pedro.common.ConnectChecker
import com.pedro.encoder.input.sources.audio.InternalAudioSource
import com.pedro.encoder.input.sources.audio.MicrophoneSource
import com.pedro.encoder.input.sources.audio.MixAudioSource
import com.pedro.library.base.recording.RecordController

class MainActivity : AppCompatActivity() , ConnectChecker {

    private lateinit var button: ImageView
    private lateinit var etUrl: EditText
    private lateinit var bRecord: ImageView
    private var currentAudioSource: MenuItem? = null

    private val activityResultContract = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val data = result.data
        Log.i("APPLifeCycle" , "activityResultContract")
        if (data != null && result.resultCode == RESULT_OK) {
            val screenService = ScreenService.INSTANCE
            if (screenService != null) {
                val endpoint = etUrl.text.toString()
                if (screenService.prepareStream(result.resultCode, data)) {
                    screenService.startStream(endpoint)
                } else {
                    toast("Prepare stream failed")
                }
            }
        } else {
            toast("No permissions available")
            button.setImageResource(R.drawable.stream_icon)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("APPLifeCycle" , "onCreate")
        try {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            setContentView(R.layout.activity_main)
            button = findViewById(R.id.b_start_stop)
            etUrl = findViewById(R.id.et_rtp_url)
            bRecord = findViewById(R.id.b_record)

            val screenService = ScreenService.INSTANCE
            //No streaming/recording start service
            if (screenService == null) {
                startService(Intent(this, ScreenService::class.java))
            }
            if (screenService != null && screenService.isStreaming()) {
                button.setImageResource(R.drawable.stream_stop_icon)
            } else {
                button.setImageResource(R.drawable.stream_icon)
            }
            if (screenService != null && screenService.isRecording()) {
                bRecord.setImageResource(R.drawable.stop_icon)
            } else {
                bRecord.setImageResource(R.drawable.record_icon)
            }

            button.setOnClickListener {
                try {
                    val service = ScreenService.INSTANCE
                    if (service != null) {
                        service.setCallback(this)
                        if (!service.isStreaming()) {
                            button.setImageResource(R.drawable.stream_stop_icon)
                            activityResultContract.launch(service.sendIntent())
                        } else {
                            stopStream()
                        }
                    }
                } catch ( e : Exception) {
                    Log.i("FunshowError" , "e = $e")
                }
            }

            bRecord.setOnClickListener {
                ScreenService.INSTANCE?.toggleRecord { state ->
                    when (state) {
                        RecordController.Status.STARTED -> {
                            bRecord.setImageResource(R.drawable.pause_icon)
                        }
                        RecordController.Status.STOPPED -> {
                            bRecord.setImageResource(R.drawable.record_icon)
                        }
                        RecordController.Status.RECORDING -> {
                            bRecord.setImageResource(R.drawable.stop_icon)
                        }
                        else -> {}
                    }
                }
            }
        } catch ( e : Exception) {
            Log.i("FunshowError" , "e = $e")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.i("APPLifeCycle" , "onCreateOptionsMenu")
        menuInflater.inflate(R.menu.screen_menu, menu)
        val defaultAudioSource = when (ScreenService.INSTANCE?.getCurrentAudioSource()) {
            is MicrophoneSource -> menu.findItem(R.id.audio_source_microphone)
            is InternalAudioSource -> menu.findItem(R.id.audio_source_internal)
            is MixAudioSource -> menu.findItem(R.id.audio_source_mix)
            else -> menu.findItem(R.id.audio_source_microphone)
        }
        currentAudioSource = defaultAudioSource.updateMenuColor(this, currentAudioSource)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        try {
            Log.i("APPLifeCycle" , "onOptionsItemSelected")
            when (item.itemId) {
                R.id.audio_source_microphone, R.id.audio_source_internal, R.id.audio_source_mix -> {
                    val service = ScreenService.INSTANCE
                    if (service != null) {
                        service.toggleAudioSource(item.itemId)
                        currentAudioSource = item.updateMenuColor(this, currentAudioSource)
                    }
                }
            }
        } catch (e: IllegalArgumentException) {
            toast("Change source error: ${e.message}")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        Log.i("APPLifeCycle" , "onDestroy")
        super.onDestroy()
        val screenService = ScreenService.INSTANCE
        if (screenService != null && !screenService.isStreaming() && !screenService.isRecording()) {
            screenService.setCallback(null)
            activityResultContract.unregister()
            //stop service only if no streaming or recording
            stopService(Intent(this, ScreenService::class.java))
        }
    }

    private fun stopStream() {
        Log.i("APPLifeCycle" , "stopStream")
        val screenService = ScreenService.INSTANCE
        screenService?.stopStream()
        button.setImageResource(R.drawable.stream_icon)
    }

    override fun onConnectionStarted(url: String) {
        Log.i("ActivityLog" , "url = $url")
        Log.i("APPLifeCycle" , "onConnectionStarted")
    }

    override fun onConnectionSuccess() {
        Log.i("APPLifeCycle" , "onConnectionSuccess")
        toast("Connected")
    }

    override fun onConnectionFailed(reason: String) {
        Log.i("APPLifeCycle" , "onConnectionFailed")
        stopStream()
        toast("Failed: $reason")
    }

    override fun onNewBitrate(bitrate: Long) {
        Log.i("APPLifeCycle" , "onNewBitrate")
    }

    override fun onDisconnect() {
        Log.i("APPLifeCycle" , "onDisconnect")
        toast("Disconnected")
    }

    override fun onAuthError() {
        Log.i("APPLifeCycle" , "onAuthError")
        stopStream()
        toast("Auth error")
    }

    override fun onAuthSuccess() {
        Log.i("APPLifeCycle" , "onAuthSuccess")
        toast("Auth success")
    }
}