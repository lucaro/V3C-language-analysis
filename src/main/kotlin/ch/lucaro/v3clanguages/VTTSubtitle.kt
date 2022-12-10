package ch.lucaro.v3clanguages

import java.io.File

class VTTSubtitle(file: File) {

    companion object {

        fun parseTimeStamp(timestamp: String): Long {

            var millis = 0L
            timestamp.substringBeforeLast('.').split(":").forEach {
                millis = (millis * 60) + it.toLong()
            }
            millis *= 1000
            return millis + timestamp.substringAfterLast('.').toLong()

        }

    }

    val elements: List<SubtitleElement>

    init {

        val lines = file.readLines(Charsets.UTF_8)

        val timestamps = lines.filterIndexed { index, _ -> (index - 2) % 3 == 0 }.map {
            it.split(" --> ").map { s -> parseTimeStamp(s) }
        }

        val strings = lines.filterIndexed { index, s -> index > 0 && index % 3 == 0 }

        elements = timestamps.zip(strings).map {
            SubtitleElement(it.first, it.second.trim())
        }

    }


}