package ch.lucaro.v3clanguages

import java.lang.Character.UnicodeScript
import java.util.SortedMap
import kotlin.math.expm1

object CharCodes {

    fun getScript(s: String): UnicodeScript? {
        val counts = IntArray(UnicodeScript.values().size)
        var mostFrequentScript: UnicodeScript? = null
        var maxCount = 0
        val n = s.codePointCount(0, s.length)
        var i = 0
        while (i < n) {
            val codePoint = s.codePointAt(i)
            val script = UnicodeScript.of(codePoint)
            val count = ++counts[script.ordinal]
            if (mostFrequentScript == null || count > maxCount) {
                maxCount = count
                mostFrequentScript = script
            }
            i = s.offsetByCodePoints(i, 1)
        }
        return mostFrequentScript
    }

    fun getScripts(s: String): List<UnicodeScript> {

        val n = s.codePointCount(0, s.length)
        var i = 0
        val scripts = mutableListOf<UnicodeScript>()
        while (i < n) {
            val codePoint = s.codePointAt(i)
            val script = UnicodeScript.of(codePoint)
            scripts.add(script)
            i = s.offsetByCodePoints(i, 1)
        }
        return scripts

    }

    fun getScriptsHistogram(s: String): Map<UnicodeScript, Int> {
        return getScripts(s).groupBy { it }.mapValues { it.value.size }.toList().sortedByDescending { it.second }.toMap()
    }

}