package com.funshow.rtmp_rootencoder_rotation

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.pedro.common.ConnectChecker
import com.pedro.encoder.input.gl.render.filters.AndroidViewFilterRender
import com.pedro.encoder.input.sources.video.Camera1Source
import com.pedro.encoder.input.sources.video.Camera2Source
import com.pedro.extrasources.CameraXSource
import com.pedro.library.base.recording.RecordController
import com.pedro.library.generic.GenericStream
import com.pedro.library.util.BitrateAdapter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//拍照片段
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class CameraFragment: Fragment() , ConnectChecker {

    //初始化片段
    companion object {
        fun getInstance(): CameraFragment = CameraFragment()
    }

    //產生串流
    val genericStream: GenericStream by lazy {
        GenericStream(requireContext(), this).apply {
            getGlInterface().autoHandleOrientation = true
            getStreamClient().setBitrateExponentialFactor(0.5f)
        }
    }

    //基本宣告
    private lateinit var surfaceView: SurfaceView
    private lateinit var bRecord: ImageView
    private lateinit var bSwitchCamera: ImageView
    private lateinit var etUrl: EditText
    private lateinit var bStartStop: ImageView
    private lateinit var txtBitrate: TextView
    private val width = 640
    private val height = 480
    private val vBitrate = 1200 * 1000
    private var rotation = 0
    private val sampleRate = 32000
    private val isStereo = true
    private val aBitrate = 128 * 1000
    private var recordPath = ""
    //位元率適配器用於根據頻寬動態變更位元率。
    private val bitrateAdapter = BitrateAdapter {
        genericStream.setVideoBitrateOnFly(it)
    }.apply {
        setMaxBitrate(vBitrate + aBitrate)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //畫面綁定
        val view = inflater.inflate(R.layout.fragment_camera, container, false)

        //程式碼和 xml 綁定
        bStartStop = view.findViewById(R.id.b_start_stop)
        bRecord = view.findViewById(R.id.b_record)
        bSwitchCamera = view.findViewById(R.id.switch_camera)
        etUrl = view.findViewById(R.id.et_rtp_url)
        txtBitrate = view.findViewById(R.id.txt_bitrate)
        surfaceView = view.findViewById(R.id.surfaceView)

        //設定 surfaceView
        (activity as? MainActivity)?.let {
            surfaceView.setOnTouchListener(it)
        }

        //設定響應
        surfaceView.holder.addCallback(object: SurfaceHolder.Callback {
            //預覽已建立
            override fun surfaceCreated(holder: SurfaceHolder) {
                //是否正在預覽
                if (!genericStream.isOnPreview) {
                    genericStream.startPreview(surfaceView)
                }
            }

            //改變預覽
            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                genericStream.getGlInterface().setPreviewResolution(width, height) //設定預覽分辨率
            }

            //銷毀預覽
            override fun surfaceDestroyed(holder: SurfaceHolder) {
                if (genericStream.isOnPreview) {
                    genericStream.stopPreview() //暫停預覽
                }
            }
        })

        //暫停
        bStartStop.setOnClickListener {
            //是否正在串流
            if (!genericStream.isStreaming) {
                genericStream.startStream(etUrl.text.toString()) //開始串流
                bStartStop.setImageResource(R.drawable.stream_stop_icon) //變換 icon
            } else {
                genericStream.stopStream() //停止串流
                bStartStop.setImageResource(R.drawable.stream_icon)
            }
        }

        //存檔紀錄
        bRecord.setOnClickListener {
            if (!genericStream.isRecording) {
                val folder = PathUtils.getRecordPath() //路徑
                //判斷資料夾是否存在
                if (!folder.exists()) {
                    folder.mkdir() // 建立資料夾
                }
                val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()) //設定日期格式
                recordPath = "${folder.absolutePath}/${sdf.format(Date())}.mp4" //設定路徑
                //開始串流紀錄
                genericStream.startRecord(recordPath) { status ->
                    //判斷狀態是否是紀錄中
                    if (status == RecordController.Status.RECORDING) {
                        bRecord.setImageResource(R.drawable.stop_icon) //改變 icon
                    }
                }
                bRecord.setImageResource(R.drawable.pause_icon) //改變圖片
            } else {
                genericStream.stopRecord() //停止紀錄
                bRecord.setImageResource(R.drawable.record_icon)
                PathUtils.updateGallery(requireContext(), recordPath) //更新圖庫
            }
        }
        //前後鏡頭切換
        bSwitchCamera.setOnClickListener {
            when (val source = genericStream.videoSource) {
                is Camera1Source -> source.switchCamera() //使用 camera1
                is Camera2Source -> source.switchCamera() //使用 camera2
                is CameraXSource -> source.switchCamera() //使用 cameraX
            }
        }
        return view
    }

    //設定方向模式
    fun setOrientationMode(isVertical: Boolean) {
        val wasOnPreview = genericStream.isOnPreview
        genericStream.release()
        rotation = if (isVertical) { 90 } else { 0 }
        prepare() //準備
        if (wasOnPreview) genericStream.startPreview(surfaceView)
    }

    //onCreate 生命週期
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prepare() //準備
        genericStream.getStreamClient().setReTries(10) //設定重試次數
    }

    //準備
    private fun prepare() {
        val prepared = try {
            //準備視訊和音頻
            genericStream.prepareVideo(width, height, vBitrate, rotation = rotation) &&
            genericStream.prepareAudio(sampleRate, isStereo, aBitrate)
        } catch (e: IllegalArgumentException) {
            false
        }
        if (!prepared) {
            toast("音訊或視訊配置失敗")
            activity?.finish()
        }
    }

    //onDestroy 生命週期
    override fun onDestroy() {
        super.onDestroy()
        genericStream.release() //發布串流
    }

    //連接開始時
    override fun onConnectionStarted(url: String) {
    }

    //連線成功時
    override fun onConnectionSuccess() {
        toast("Connected")
    }

    //連線失敗時
    override fun onConnectionFailed(reason: String) {
        //重試連線
        if (genericStream.getStreamClient().reTry(5000, reason, null)) {
            toast("Retry")
        } else {
            genericStream.stopStream() //停止串流
            bStartStop.setImageResource(R.drawable.stream_icon)
            toast("Failed: $reason")
        }
    }

    //新比特率
    override fun onNewBitrate(bitrate: Long) {
        bitrateAdapter.adaptBitrate(bitrate, genericStream.getStreamClient().hasCongestion())
        txtBitrate.text = String.format(Locale.getDefault(), "%.1f mb/s", bitrate / 1000_000f)
    }

    //斷開連線時
    override fun onDisconnect() {
        txtBitrate.text = String()
        toast("Disconnected")
    }

    //授權錯誤
    override fun onAuthError() {
        genericStream.stopStream()
        bStartStop.setImageResource(R.drawable.stream_icon)
        toast("Auth error")
    }

    //授權成功
    override fun onAuthSuccess() {
        toast("Auth success")
    }
}