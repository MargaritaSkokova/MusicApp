package com.maran.musicapp.domain

class TimeConvertor {
    companion object {
        fun formatMilliseconds(milliseconds: Int): String {
            val minutes = (milliseconds / 1000) / 60
            val seconds = (milliseconds / 1000) % 60
            return String.format("%02d:%02d", minutes, seconds)
        }

        fun formatSeconds(seconds: Int): String {
            val minutes = seconds / 60
            val newSeconds = seconds % 60
            return String.format("%02d:%02d", minutes, newSeconds)
        }
    }
}