package ch.lucaro.v3clanguages

data class SubtitleElement(val startTime: Long, val endTime: Long, val string: String) {

    constructor(times: List<Long>, string: String): this(times.min(), times.max(), string)

    companion object {

        private val musicTerms = listOf(
            "♪", "♩", "♫", "♬",
            "upbeat music playing",
            "music playing", "music",
            "интригующая музыка",
            "позитивающая музыка",
            "напряженная музыка",
            "спокойная музыка",
            "позитиващая музыка",
            "музыкальная заставка",
            "динамичная музыка",
            "музыка", "музыкальная",
            "sound of the water", "uh-uh!",
            "្", "ᄜ", "ლ", "ᵃ", "ᵇ", "ᵈ", "ᵉ", "ʰ",
            "බල්න", "බල", "ල", "ල්", "න",
            "ᅲ"
        )

        fun isMusic(s: String): Boolean {
            var replaced = s.lowercase()
            musicTerms.forEach {
                replaced = replaced.replace(it, "")
            }
            replaced = replaced.replace("\\s".toRegex(), "")
            return replaced.length < (s.length / 2)
        }


    }

    init {
        require(endTime >= startTime) {"invalid time interval"}
    }

    val durationMs: Long
        get() = endTime - startTime

    val music: Boolean by lazy { isMusic(string) }

    val relevant: Boolean by lazy {
        if (durationMs < 500) { //to short in time
            return@lazy false
        }
        if (string.length < 6) { //too few characters
            return@lazy false
        }
        if (CharCodes.getScript(string) == Character.UnicodeScript.COMMON) { //primarily 'COMMON' characters
            return@lazy false
        }
        true
    }

}
