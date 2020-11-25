package com.nuo.xxjjplayer

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.FrameLayout
import androidx.annotation.NonNull
import tv.danmaku.ijk.media.player.IMediaPlayer
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import java.io.IOException


class IjkVideoView : FrameLayout {

    private var mContext: Context? = null
    private var mMediaPlayer: IMediaPlayer? = null //视频控制类

    private var mVideoPlayerListener: VideoPlayerListener? = null //自定义监听器
    private var mSurfaceView: SurfaceView? = null //播放视图
    private var mPath = "" //视频文件地址


    constructor(@NonNull context : Context):super(context){
        initVideoView(context)
    }
    constructor(@NonNull context : Context,@NonNull attrs : AttributeSet):super(context,attrs){
        initVideoView(context)
    }
    constructor(@NonNull context : Context,@NonNull attrs : AttributeSet,@NonNull defStyleAttr:Int):super(context,attrs,defStyleAttr){
        initVideoView(context)
    }

    abstract class VideoPlayerListener : IMediaPlayer.OnPreparedListener,
        IMediaPlayer.OnCompletionListener, IMediaPlayer.OnErrorListener{

    }

    private fun initVideoView(context: Context) {
        mContext = context
        isFocusable = true
    }

    fun setPath(path: String) {
        if (TextUtils.equals("", mPath)) {
            mPath = path
            initSurfaceView()
        } else {
            mPath = path
            loadVideo()
        }
    }

    private fun initSurfaceView() {
        mSurfaceView = SurfaceView(mContext)
        mSurfaceView?.holder?.addCallback(LmnSurfaceCallback{loadVideo()})
        val layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT,
            Gravity.CENTER
        )
        mSurfaceView?.layoutParams = layoutParams
        this.addView(mSurfaceView)
    }

    //surfaceView的监听器
    private  class LmnSurfaceCallback(val loadinvoke:()->Unit) : SurfaceHolder.Callback {

        override fun surfaceCreated(holder: SurfaceHolder) {}
        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            loadinvoke.invoke()
        }
        override fun surfaceDestroyed(holder: SurfaceHolder) {}
    }

    //加载视频
    private fun loadVideo() {
        if (mMediaPlayer != null) {
            mMediaPlayer?.stop()
            mMediaPlayer?.release()
        }
        val ijkMediaPlayer = IjkMediaPlayer()
        mMediaPlayer = ijkMediaPlayer
        if (mVideoPlayerListener != null) {
            mMediaPlayer?.setOnPreparedListener(mVideoPlayerListener)
            mMediaPlayer?.setOnErrorListener(mVideoPlayerListener)
        }
        try {
            mMediaPlayer?.dataSource = mPath
        } catch (e: IOException) {
            e.printStackTrace()
        }
        mMediaPlayer?.setDisplay(mSurfaceView?.holder)
        mMediaPlayer?.prepareAsync()
    }

    fun setListener(listener: VideoPlayerListener?) {
        mVideoPlayerListener = listener
        if (mMediaPlayer != null) {
            mMediaPlayer!!.setOnPreparedListener(listener)
        }
    }

    fun isPlaying(): Boolean {
        return if (mMediaPlayer != null) {
            mMediaPlayer!!.isPlaying
        } else false
    }

    fun start() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.start()
        }
    }

    fun pause() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.pause()
        }
    }

    fun stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.stop()
        }
    }

    fun reset() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.reset()
        }
    }

    fun release() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.reset()
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }

}