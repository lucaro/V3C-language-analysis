package ch.lucaro.v3clanguages

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File

object AggregateProportions {

    @JvmStatic
    fun main(args: Array<String>) {

        val durationMap = File("data/V3C_durations.csv").readLines()
            .drop(1).associate {
                val split = it.split(",")
                split[0] to split[1].toLong()
            }

        val mapper = jacksonObjectMapper()

        val collections = listOf("V3C1", "V3C2", "V3C3")

        val mapType = object : TypeReference<Map<String, Long>>(){}

        var globalDuration = 0L
        val globalProportionalMap = mutableMapOf<String, Long>()

        val globalWriter = File("data/proportional.tsv").printWriter()

        for (collection in collections) {

            val lines = File("data/${collection}.tsv").readLines()
            val writer = File("data/${collection}_proportional.tsv").printWriter()

            var collectionDuration = 0L
            val collectionProportionalMap = mutableMapOf<String, Long>()

            for (line in lines) {

                val split = line.split("\t")
                val id = split[0]
                val map = mapper.readValue(split[1], mapType)

                val totalDuration = durationMap[id]!!

                val durationSum = map.values.sum().toDouble()
                val remainingDuration = totalDuration - durationSum

                val proportional = map.mapValues { it.value.toDouble() / totalDuration }.toMutableMap()
                proportional["NONE"] = remainingDuration / totalDuration

                writer.println("$id\t${mapper.writeValueAsString(proportional)}")

                globalDuration += totalDuration
                collectionDuration += totalDuration

                map.forEach {
                    globalProportionalMap[it.key] = it.value + (globalProportionalMap[it.key] ?: 0)
                    collectionProportionalMap[it.key] = it.value + (collectionProportionalMap[it.key] ?: 0)
                }

            }

            writer.flush()
            writer.close()

            val map = collectionProportionalMap.mapValues { it.value.toDouble() / collectionDuration }.toMutableMap()
            map["NONE"] = 1.0 - map.values.sum()

            globalWriter.println("$collection\t${mapper.writeValueAsString(map)}")

        }

        val map = globalProportionalMap.mapValues { it.value.toDouble() / globalDuration }.toMutableMap()
        map["NONE"] = 1.0 - map.values.sum()

        globalWriter.println("V3C\t${mapper.writeValueAsString(map)}")

        globalWriter.flush()
        globalWriter.close()

    }

}