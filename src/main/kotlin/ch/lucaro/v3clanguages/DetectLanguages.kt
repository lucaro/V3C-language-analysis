package ch.lucaro.v3clanguages

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.pemistahl.lingua.api.Language
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder
import java.io.File

object DetectLanguages {

    @JvmStatic
    fun main(args: Array<String>) {

        val mapper = jacksonObjectMapper()

        val collections = listOf("V3C1", "V3C2", "V3C3")

        for (collection in collections) {

            val files = File("data/$collection").listFiles { _, name -> name.endsWith("vtt") } ?: emptyArray()

            val writer = File("data/${collection}.tsv").printWriter()

            for (file in files) {
                val subtitle = VTTSubtitle(file)

                val detector = LanguageDetectorBuilder
                    .fromAllLanguages()
                    .build()

                val detections = subtitle.elements.filter{ it.relevant }.map {

                    if (it.music) {
                        return@map it to "MUSIC"
                    }

                    val detectedLanguage = detector.detectLanguageOf(it.string)

                    val detected = if (detectedLanguage != Language.UNKNOWN) {
                        detectedLanguage.name
                    } else {
                        CharCodes.getScript(it.string)?.name ?: "UNKNOWN"
                    }

                    it to detected

                }

                val ratios = detections.groupBy { it.second }.mapValues { it.value.sumOf { d -> d.first.durationMs } }

                writer.println("${file.nameWithoutExtension}\t${mapper.writeValueAsString(ratios)}")
                println(file.name)
            }

            writer.flush()
            writer.close()

        }

    }

}