package com.example.jiaozzrecords.components.service

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.example.jiaozzrecords.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * 一个可绑定且可启动的后台 Service，用于全局播放和控制音乐。
 */
class MusicPlayerService : Service() {
    inner class LocalBinder : Binder() {
        fun getService(): MusicPlayerService = this@MusicPlayerService
    }
    private val binder = LocalBinder()

    private lateinit var player: MediaPlayer

    private val _position = MutableStateFlow(0f)
    val position: StateFlow<Float> = _position.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _coverBitmap = MutableStateFlow<Bitmap?>(null)
    val coverBitmap: StateFlow<Bitmap?> = _coverBitmap.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        // 1️⃣ 初始化 MediaPlayer
        player = MediaPlayer.create(this, R.raw.nujabes_aruarian_dance).apply {
            isLooping = true
        }

        // 2️⃣ 读取 ID3 封面
        val afd = resources.openRawResourceFd(R.raw.nujabes_aruarian_dance)
        afd?.let {
            val retriever = MediaMetadataRetriever().apply {
                setDataSource(it.fileDescriptor, it.startOffset, it.length)
            }
            retriever.embeddedPicture?.let { data ->
                _coverBitmap.value = BitmapFactory.decodeByteArray(data, 0, data.size)
            }
            retriever.release()
            it.close()
        }

        // 3️⃣ 进度轮询
        scope.launch {
            while (isActive) {
                _position.value = player.currentPosition.toFloat()
                _isPlaying.value = player.isPlaying
                delay(500)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    /** 总时长（毫秒） */
    val duration: Int
        get() = player.duration

    fun play() {
        if (!player.isPlaying) {
            player.start()
            _isPlaying.value = true
        }
    }

    fun pause() {
        if (player.isPlaying) {
            player.pause()
            _isPlaying.value = false
        }
    }

    fun seekTo(ms: Int) {
        player.seekTo(ms)
        _position.value = ms.toFloat()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
        player.release()
    }
}