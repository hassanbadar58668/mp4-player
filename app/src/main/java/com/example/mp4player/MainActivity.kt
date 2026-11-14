package com.example.mp4player

import android.content.Intent
import com.example.mp4player.R
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val pickVideoLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            // صلاحية قراءة دائمة (مهمة لأن URI بيفقد صلاحيته بعد إعادة التشغيل)
            contentResolver.takePersistableUriPermission(
                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            openPlayer(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.pickVideoButton).setOnClickListener {
            pickVideoLauncher.launch(arrayOf("video/mp4", "video/*"))
        }
    }

    private fun openPlayer(uri: Uri) {
        startActivity(Intent(this, PlayerActivity::class.java).apply {
            data = uri
        })
    }
}
