package net.treelzebub.recorder

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder.AudioSource.MIC
import android.os.Handler
import android.os.Looper
import com.yalantis.audio.lib.AudioUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class Recorder(
    private val config: Config,
    private val listener: AudioRecord.OnRecordPositionUpdateListener
) {

    data class Config(
        val file: File,
        val source: Int = MIC,
        val sampleRate: Int = 44100,
        val channelConfig: Int = AudioFormat.CHANNEL_IN_STEREO,
        val encoding: Int = AudioFormat.ENCODING_PCM_16BIT
    ) {

        val bufferSize = 2 * AudioRecord.getMinBufferSize(sampleRate, channelConfig, encoding)
        val buffer = ByteArray(bufferSize)
    }

    private val recorder = with(config) {
        AudioRecord(source, sampleRate, channelConfig, encoding, bufferSize)
    }

    init { init() }

    private fun init() {
        AudioUtil.initProcessor(config.sampleRate, config.channelConfig, config.encoding)
    }

    fun start() {
        recorder.startRecording()
        GlobalScope.launch {
            Looper.prepare()
            recorder.setRecordPositionUpdateListener(listener, Handler(Looper.myLooper()))
            val bytePerSample = config.encoding / 8
            val samplesToDraw = config.bufferSize / bytePerSample
            recorder.positionNotificationPeriod = samplesToDraw

            // We need to read first chunk to activate our listener.
            // https://code.google.com/p/android/issues/detail?id=53996
            recorder.read(config.buffer, 0, config.bufferSize)
            Looper.loop()
        }
    }

    fun stop() = recorder.stop()

    fun release() {
        recorder.release()
        AudioUtil.disposeProcessor()
    }
}