package net.treelzebub.recorder

import android.widget.ImageView

class Controls(
    private val playStop: ImageView
) {

    fun playStop(isPlaying: Boolean) {
        val drawable = if (isPlaying) android.R.drawable.ic_media_play else android.R.drawable.ic_media_pause
        playStop.setImageResource(drawable)
    }
}