package com.funshow.rtmp_rootencoder_rotation

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Build
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.Surface
import android.view.View
import androidx.annotation.RequiresApi
import com.pedro.encoder.input.gl.SpriteGestureController
import com.pedro.encoder.input.gl.render.filters.AnalogTVFilterRender
import com.pedro.encoder.input.gl.render.filters.AndroidViewFilterRender
import com.pedro.encoder.input.gl.render.filters.BasicDeformationFilterRender
import com.pedro.encoder.input.gl.render.filters.BeautyFilterRender
import com.pedro.encoder.input.gl.render.filters.BlackFilterRender
import com.pedro.encoder.input.gl.render.filters.BlurFilterRender
import com.pedro.encoder.input.gl.render.filters.BrightnessFilterRender
import com.pedro.encoder.input.gl.render.filters.CartoonFilterRender
import com.pedro.encoder.input.gl.render.filters.ChromaFilterRender
import com.pedro.encoder.input.gl.render.filters.CircleFilterRender
import com.pedro.encoder.input.gl.render.filters.ColorFilterRender
import com.pedro.encoder.input.gl.render.filters.ContrastFilterRender
import com.pedro.encoder.input.gl.render.filters.CropFilterRender
import com.pedro.encoder.input.gl.render.filters.DuotoneFilterRender
import com.pedro.encoder.input.gl.render.filters.EarlyBirdFilterRender
import com.pedro.encoder.input.gl.render.filters.EdgeDetectionFilterRender
import com.pedro.encoder.input.gl.render.filters.ExposureFilterRender
import com.pedro.encoder.input.gl.render.filters.FireFilterRender
import com.pedro.encoder.input.gl.render.filters.GammaFilterRender
import com.pedro.encoder.input.gl.render.filters.GlitchFilterRender
import com.pedro.encoder.input.gl.render.filters.GreyScaleFilterRender
import com.pedro.encoder.input.gl.render.filters.HalftoneLinesFilterRender
import com.pedro.encoder.input.gl.render.filters.Image70sFilterRender
import com.pedro.encoder.input.gl.render.filters.LamoishFilterRender
import com.pedro.encoder.input.gl.render.filters.MoneyFilterRender
import com.pedro.encoder.input.gl.render.filters.NegativeFilterRender
import com.pedro.encoder.input.gl.render.filters.NoiseFilterRender
import com.pedro.encoder.input.gl.render.filters.PixelatedFilterRender
import com.pedro.encoder.input.gl.render.filters.PolygonizationFilterRender
import com.pedro.encoder.input.gl.render.filters.RGBSaturationFilterRender
import com.pedro.encoder.input.gl.render.filters.RainbowFilterRender
import com.pedro.encoder.input.gl.render.filters.RippleFilterRender
import com.pedro.encoder.input.gl.render.filters.RotationFilterRender
import com.pedro.encoder.input.gl.render.filters.SaturationFilterRender
import com.pedro.encoder.input.gl.render.filters.SepiaFilterRender
import com.pedro.encoder.input.gl.render.filters.SharpnessFilterRender
import com.pedro.encoder.input.gl.render.filters.SnowFilterRender
import com.pedro.encoder.input.gl.render.filters.SwirlFilterRender
import com.pedro.encoder.input.gl.render.filters.TemperatureFilterRender
import com.pedro.encoder.input.gl.render.filters.ZebraFilterRender
import com.pedro.encoder.input.gl.render.filters.`object`.GifObjectFilterRender
import com.pedro.encoder.input.gl.render.filters.`object`.ImageObjectFilterRender
import com.pedro.encoder.input.gl.render.filters.`object`.SurfaceFilterRender
import com.pedro.encoder.input.gl.render.filters.`object`.TextObjectFilterRender
import com.pedro.encoder.utils.gl.TranslateTo
import com.pedro.library.view.GlInterface
import java.io.IOException

//過濾菜單
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class FilterMenu(private val context: Context) {

    val spriteGestureController = SpriteGestureController() //Sprite手勢控制器

    //在選項項目上選擇
    fun onOptionsItemSelected(item: MenuItem, glInterface: GlInterface): Boolean {
        //停止圖像、文字和 gif 流物件的偵聽器。
        spriteGestureController.stopListener()
        when (item.itemId) {
            //清空過濾
            R.id.no_filter -> {
                glInterface.clearFilters() //清空過濾
                return true
            }
            //類比電視
            R.id.analog_tv -> {
                glInterface.setFilter(AnalogTVFilterRender())
                return true
            }
            //安卓視圖
            R.id.android_view -> {
                val view: View = LayoutInflater.from(context).inflate(R.layout.layout_android_filter, null)
                //取得根視圖以了解 XML 佈局中的最大寬度和最大高度
                val previewSize = glInterface.encoderSize
                val sizeSpecWidth = View.MeasureSpec.makeMeasureSpec(previewSize.x, View.MeasureSpec.EXACTLY)
                val sizeSpecHeight = View.MeasureSpec.makeMeasureSpec(previewSize.y, View.MeasureSpec.EXACTLY)
                //設定視圖大小以允許渲染
                view.measure(sizeSpecWidth, sizeSpecHeight)
                view.layout(0, 0, previewSize.x, previewSize.y)
                val androidViewFilterRender = AndroidViewFilterRender()
                androidViewFilterRender.view = view
                glInterface.setFilter(androidViewFilterRender)
                return true
            }
            //基本變形
            R.id.basic_deformation -> {
                glInterface.setFilter(BasicDeformationFilterRender()) //基本變形濾鏡渲染
                return true
            }
            //美顏
            R.id.beauty -> {
                glInterface.setFilter(BeautyFilterRender()) //美顏濾鏡渲染
                return true
            }
            //黑色
            R.id.black -> {
                glInterface.setFilter(BlackFilterRender()) //黑色濾鏡渲染
                return true
            }
            //模糊
            R.id.blur -> {
                glInterface.setFilter(BlurFilterRender()) //模糊濾鏡渲染
                return true
            }
            //亮度
            R.id.brightness -> {
                glInterface.setFilter(BrightnessFilterRender()) //亮度濾鏡渲染
                return true
            }
            //卡通
            R.id.cartoon -> {
                glInterface.setFilter(CartoonFilterRender()) //卡通濾鏡渲染
                return true
            }
            //色度
            R.id.chroma -> {
                val chromaFilterRender = ChromaFilterRender() //色度濾鏡渲染
                glInterface.setFilter(chromaFilterRender)
                chromaFilterRender.setImage(BitmapFactory.decodeResource(context.resources, R.drawable.bg_chroma))
                return true
            }
            //圓形
            R.id.circle -> {
                glInterface.setFilter(CircleFilterRender()) //圓形濾鏡渲染
                return true
            }
            //顏色
            R.id.color -> {
                glInterface.setFilter(ColorFilterRender()) //色彩濾鏡渲染
                return true
            }
            //對比
            R.id.contrast -> {
                glInterface.setFilter(ContrastFilterRender()) //對比濾鏡渲染
                return true
            }
            //裁剪
            R.id.crop -> {
                glInterface.setFilter(CropFilterRender().apply {
                    //裁切影像中心，寬度為 40%，高度為 40%
                    setCropArea(30f, 30f, 40f, 40f)
                }) //裁剪濾鏡渲染
                return true
            }
            //雙色調
            R.id.duotone -> {
                glInterface.setFilter(DuotoneFilterRender()) //雙色調濾鏡渲染
                return true
            }
            //早鳥
            R.id.early_bird -> {
                glInterface.setFilter(EarlyBirdFilterRender()) //早鳥濾鏡渲染
                return true
            }
            //邊緣偵測
            R.id.edge_detection -> {
                glInterface.setFilter(EdgeDetectionFilterRender()) //邊緣偵測濾鏡渲染
                return true
            }
            //曝光濾鏡
            R.id.exposure -> {
                glInterface.setFilter(ExposureFilterRender()) //曝光濾鏡渲染
                return true
            }
            //火
            R.id.fire -> {
                glInterface.setFilter(FireFilterRender()) //火濾鏡渲染
                return true
            }
            //伽瑪
            R.id.gamma -> {
                glInterface.setFilter(GammaFilterRender()) //伽瑪濾鏡渲染
                return true
            }
            //毛刺
            R.id.glitch -> {
                glInterface.setFilter(GlitchFilterRender()) //毛刺濾鏡渲染
                return true
            }
            //GIF
            R.id.gif -> {
                try {
                    val gifObjectFilterRender = GifObjectFilterRender() //Gif物件過濾器渲染
                    gifObjectFilterRender.setGif(context.resources.openRawResource(R.raw.banana))
                    glInterface.setFilter(gifObjectFilterRender)
                    gifObjectFilterRender.setScale(50f, 50f)
                    gifObjectFilterRender.setPosition(TranslateTo.BOTTOM)
                    spriteGestureController.setBaseObjectFilterRender(gifObjectFilterRender) //Optional
                } catch (ignored: IOException) { }
                return true
            }
            //灰階
            R.id.grey_scale -> {
                glInterface.setFilter(GreyScaleFilterRender()) //灰階濾鏡渲染
                return true
            }
            //半色調線條
            R.id.halftone_lines -> {
                glInterface.setFilter(HalftoneLinesFilterRender()) //半色調線條濾鏡渲染
                return true
            }
            //圖片
            R.id.image -> {
                val imageObjectFilterRender = ImageObjectFilterRender() //影像物件濾鏡渲染
                glInterface.setFilter(imageObjectFilterRender)
                imageObjectFilterRender.setImage(
                    BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher)
                )
                imageObjectFilterRender.setScale(50f, 50f)
                imageObjectFilterRender.setPosition(TranslateTo.RIGHT)
                spriteGestureController.setBaseObjectFilterRender(imageObjectFilterRender) //Optional
                spriteGestureController.setPreventMoveOutside(false) //Optional
                return true
            }
            //圖片70S
            R.id.image_70s -> {
                glInterface.setFilter(Image70sFilterRender()) //圖片70s濾鏡渲染
                return true
            }
            //lamoish
            R.id.lamoish -> {
                glInterface.setFilter(LamoishFilterRender()) //Lamoish濾鏡渲染
                return true
            }
            //金錢
            R.id.money -> {
                glInterface.setFilter(MoneyFilterRender()) //貨幣過濾器渲染
                return true
            }
            //消極的
            R.id.negative -> {
                glInterface.setFilter(NegativeFilterRender()) //負濾鏡渲染
                return true
            }
            //雜訊
            R.id.noise -> {
                glInterface.setFilter(NoiseFilterRender()) //雜訊過濾器渲染
                return true
            }
            //像素化
            R.id.pixelated -> {
                glInterface.setFilter(PixelatedFilterRender()) //像素化濾鏡渲染
                return true
            }
            //多邊形化
            R.id.polygonization -> {
                glInterface.setFilter(PolygonizationFilterRender()) //多邊形濾鏡渲染
                return true
            }
            //彩虹
            R.id.rainbow -> {
                glInterface.setFilter(RainbowFilterRender()) //彩虹濾鏡渲染
                return true
            }
            //rgb_飽和
            R.id.rgb_saturate -> {
                val rgbSaturationFilterRender = RGBSaturationFilterRender() //RGB飽和度濾鏡渲染
                glInterface.setFilter(rgbSaturationFilterRender)
                //Reduce green and blue colors 20%. Red will predominate.
                rgbSaturationFilterRender.setRGBSaturation(1f, 0.8f, 0.8f)
                return true
            }
            //波紋
            R.id.ripple -> {
                glInterface.setFilter(RippleFilterRender()) //波紋濾鏡渲染
                return true
            }
            //旋轉
            R.id.rotation -> {
                val rotationFilterRender = RotationFilterRender() //旋轉濾鏡渲染
                glInterface.setFilter(rotationFilterRender)
                rotationFilterRender.rotation = 90
                return true
            }
            //飽和度
            R.id.saturation -> {
                glInterface.setFilter(SaturationFilterRender()) //飽和度濾鏡渲染
                return true
            }
            //棕褐色
            R.id.sepia -> {
                glInterface.setFilter(SepiaFilterRender()) //棕褐色濾鏡渲染
                return true
            }
            //銳利度
            R.id.sharpness -> {
                glInterface.setFilter(SharpnessFilterRender()) //銳利度濾鏡渲染
                return true
            }
            //雪花
            R.id.snow -> {
                glInterface.setFilter(SnowFilterRender()) //雪花濾鏡渲染
                return true
            }
            //漩渦
            R.id.swirl -> {
                glInterface.setFilter(SwirlFilterRender()) //漩渦過濾器渲染
                return true
            }
            //表面過濾
            R.id.surface_filter -> {
                //表面過濾渲染
                val surfaceFilterRender = SurfaceFilterRender {
                        surfaceTexture -> //您可以使用在表面上繪製的其他 api 來渲染此濾鏡。例如你可以使用VLC
                    val mediaPlayer: MediaPlayer = MediaPlayer.create(context, R.raw.big_bunny_240p)
                    mediaPlayer.setSurface(Surface(surfaceTexture))
                    mediaPlayer.start()
                }
                glInterface.setFilter(surfaceFilterRender)
                //影片為 360x240，因此選擇一個百分比來保持寬高比（50% x 33.3% 螢幕）
                surfaceFilterRender.setScale(50f, 33.3f)
                spriteGestureController.setBaseObjectFilterRender(surfaceFilterRender) //Optional
                return true
            }
            //溫度
            R.id.temperature -> {
                glInterface.setFilter(TemperatureFilterRender()) //溫度過濾器渲染
                return true
            }
            //文字
            R.id.text -> {
                val textObjectFilterRender = TextObjectFilterRender() //文字物件過濾渲染
                glInterface.setFilter(textObjectFilterRender)
                textObjectFilterRender.setText("Hello world", 22f, Color.RED)
                textObjectFilterRender.setScale(50f, 50f)
                textObjectFilterRender.setPosition(TranslateTo.CENTER)
                spriteGestureController.setBaseObjectFilterRender(textObjectFilterRender)
                return true
            }
            //斑馬
            R.id.zebra -> {
                glInterface.setFilter(ZebraFilterRender()) //斑馬濾鏡渲染
                return true
            }
            else -> return false
        }
    }
}