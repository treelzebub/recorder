package net.treelzebub.recorder

import android.Manifest
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.yalantis.waves.util.Horizon
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var horizon: Horizon

    private val config by lazy {
        Recorder.Config(File(filesDir.absolutePath + "temp.tmp"))
    }

    private val recorder by lazy {
        Recorder(config, PositionListener(horizon, Controls(play_stop), config.buffer))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Dexter.withActivity(this)
            .withPermission(Manifest.permission.RECORD_AUDIO)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {}

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {}

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {}
            })
            .check()

        play_stop.setOnClickListener {
            if (recorder.isRecording) {
                recorder.stop()
                play_stop.setImageResource(android.R.drawable.ic_media_play)
            } else {
                recorder.start()
                play_stop.setImageResource(android.R.drawable.ic_media_pause)
            }
        }

        horizon = Horizon(gl_surface, ResourcesCompat.getColor(resources, android.R.color.background_dark, theme),
            config.sampleRate, config.channelConfig, config.encoding)
            .apply { setMaxVolumeDb(120) }
    }

    override fun onStop() {
        super.onStop()
        recorder.release()
    }
}
