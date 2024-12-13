package com.funshow.rtmp_rootencoder_rotation

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.annotation.RequiresApi
import com.pedro.encoder.input.sources.audio.MicrophoneSource
import com.pedro.encoder.input.sources.video.Camera1Source
import com.pedro.encoder.input.sources.video.Camera2Source
import com.pedro.extrasources.BitmapSource
import com.pedro.extrasources.CameraUvcSource
import com.pedro.extrasources.CameraXSource

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class MainActivity : AppCompatActivity(), OnTouchListener {
    //基本宣告
    private val cameraFragment = CameraFragment.getInstance()
    private val filterMenu: FilterMenu by lazy { FilterMenu(this) }
    private var currentVideoSource: MenuItem? = null
    private var currentAudioSource: MenuItem? = null
    private var currentOrientation: MenuItem? = null
    private var currentFilter: MenuItem? = null

    //onCreate 生命週期
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().add(R.id.container, cameraFragment).commit()
    }

    //建立選項選單
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.rotation_menu, menu)
        val defaultVideoSource = menu.findItem(R.id.video_source_camera2)
        val defaultAudioSource = menu.findItem(R.id.audio_source_microphone)
        val defaultOrientation = menu.findItem(R.id.orientation_horizontal)
        val defaultFilter = menu.findItem(R.id.no_filter)
        currentVideoSource = defaultVideoSource.updateMenuColor(this, currentVideoSource)
        currentAudioSource = defaultAudioSource.updateMenuColor(this, currentAudioSource)
        currentOrientation = defaultOrientation.updateMenuColor(this, currentOrientation)
        currentFilter = defaultFilter.updateMenuColor(this, currentFilter)
        return true
    }

    //在選項項目上選擇
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        try {
            when (item.itemId) {
                //視訊來源相機1
                R.id.video_source_camera1 -> {
                    currentVideoSource = item.updateMenuColor(this, currentVideoSource)
                    cameraFragment.genericStream.changeVideoSource(Camera1Source(applicationContext))
                }
                //視訊來源相機2
                R.id.video_source_camera2 -> {
                    currentVideoSource = item.updateMenuColor(this, currentVideoSource)
                    cameraFragment.genericStream.changeVideoSource(Camera2Source(applicationContext))
                }
                //視訊來源相機x
                R.id.video_source_camerax -> {
                    currentVideoSource = item.updateMenuColor(this, currentVideoSource)
                    cameraFragment.genericStream.changeVideoSource(CameraXSource(applicationContext))
                }
                //視訊來源相機_uvc
                R.id.video_source_camera_uvc -> {
                    currentVideoSource = item.updateMenuColor(this, currentVideoSource)
                    cameraFragment.genericStream.changeVideoSource(CameraUvcSource())
                }
                //影片來源位圖
                R.id.video_source_bitmap -> {
                    currentVideoSource = item.updateMenuColor(this, currentVideoSource)
                    val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
                    cameraFragment.genericStream.changeVideoSource(BitmapSource(bitmap))
                }
                //音訊源麥克風
                R.id.audio_source_microphone -> {
                    currentAudioSource = item.updateMenuColor(this, currentAudioSource)
                    cameraFragment.genericStream.changeAudioSource(MicrophoneSource())
                }
                //方向_水平
                R.id.orientation_horizontal -> {
                    currentOrientation = item.updateMenuColor(this, currentOrientation)
                    cameraFragment.setOrientationMode(false)
                }
                //方向_垂直
                R.id.orientation_vertical -> {
                    currentOrientation = item.updateMenuColor(this, currentOrientation)
                    cameraFragment.setOrientationMode(true)
                }
                else -> {
                    val result = filterMenu.onOptionsItemSelected(item ,  cameraFragment.genericStream.getGlInterface())
                    if (result) currentFilter = item.updateMenuColor(this, currentFilter)
                    return result
                }
            }
        } catch (e: IllegalArgumentException) {
            toast("Change source error: ${e.message}")
        }
        return super.onOptionsItemSelected(item)
    }

    //觸控事件
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        //碰觸
        if (filterMenu.spriteGestureController.spriteTouched(view, motionEvent)) {
            filterMenu.spriteGestureController.moveSprite(view, motionEvent) //移動
            filterMenu.spriteGestureController.scaleSprite(motionEvent) //縮放
            return true
        }
        return false
    }
}