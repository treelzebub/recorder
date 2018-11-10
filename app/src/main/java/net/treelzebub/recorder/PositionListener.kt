package net.treelzebub.recorder

import android.media.AudioRecord
import com.yalantis.waves.util.Horizon

class PositionListener(
    private val horizon: Horizon,
    private val buffer: ByteArray
) : AudioRecord.OnRecordPositionUpdateListener {

    override fun onMarkerReached(recorder: AudioRecord) {}

    override fun onPeriodicNotification(recorder: AudioRecord) {
        val isRecording = recorder.recordingState == AudioRecord.RECORDSTATE_RECORDING
        val isError = recorder.read(buffer, 0, buffer.size) == -1
        if (isRecording && !isError) {
            horizon.updateView(buffer)
        }
    }
}