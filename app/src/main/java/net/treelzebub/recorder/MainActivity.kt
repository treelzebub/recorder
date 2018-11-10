package net.treelzebub.recorder

import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import com.yalantis.waves.util.Horizon
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var horizon: Horizon

    private val config by lazy {
        Recorder.Config(filesDir)
    }

    private val recorder by lazy {
        Recorder(config, PositionListener(horizon, config.buffer))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        horizon = Horizon(gl_surface, ResourcesCompat.getColor(resources, android.R.color.background_dark, theme),
            config.sampleRate, config.channelConfig, config.encoding)
            .apply { setMaxVolumeDb(120) }
    }

    override fun onStop() {
        super.onStop()
        recorder.release()
    }
}
