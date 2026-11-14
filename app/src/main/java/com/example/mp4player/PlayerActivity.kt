package com.example.mp4player

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView

class PlayerActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        playerView = findViewById(R.id.playerView)

        val uri: Uri? = intent.data
        if (uri == null) {
            Log.e(TAG, "No URI passed")
            finish()
            return
        }

        initializePlayer(uri)
    }

    private fun initializePlayer(uri: Uri) {
        // 1) DataSource: Default (file/content/http)
        val dataSourceFactory = DefaultDataSource.Factory(this)

        // 2) MediaSourceFactory
        val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)

        // 3) ExoPlayer بإعدادات خفيفة لجهاز 1GB RAM
        val exoPlayer = ExoPlayer.Builder(this)
            .setMediaSourceFactory(mediaSourceFactory)
            .setLoadControl(
                androidx.media3.exoplayer.DefaultLoadControl.Builder()
                    .setBufferDurationsMs(
                        15_000,  // minBufferMs
                        30_000,  // maxBufferMs
                        1_000,   // bufferForPlaybackMs
                        2_000    // bufferForPlaybackAfterRebufferMs
                    )
                    .setTargetBufferBytes(12 * 1024 * 1024) // 12MB
                    .setPrioritizeTimeOverSizeThresholds(true)
                    .build()
            )
            .build()

        // 4) ربط PlayerView
        playerView.player = exoPlayer

        // 5) مستمع الأخطاء (للتشخيص)
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                Log.e(TAG, "Playback error: ${error.errorCodeName}", error)
            }
        })

        // 6) تحميل + تشغيل
        exoPlayer.setMediaItem(MediaItem.fromUri(uri))
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true

        player = exoPlayer
    }

    override fun onStop() {
        super.onStop()
        // تحرير الموارد (بدون حفظ الموضع بعد — المرحلة 2)
        player?.release()
        player = null
        playerView.player = null
    }

    companion object {
        private const val TAG = "PlayerActivity"
    }
}
