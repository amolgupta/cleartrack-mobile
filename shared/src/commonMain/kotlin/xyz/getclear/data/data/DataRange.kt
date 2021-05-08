package xyz.getclear.data.data

enum class DateRange(val days: Int, val tag: String) {
    WEEK( 7, "1w"),
    MONTH( 30, "1m"),
    THREE_MONTH( 92, "3m"),
    SIX_MONTH( 183, "6m"),
    YEAR( 365, "1y"),

    //    YEAR_TO_DATE(60 * 60 * 24 * 365, "ytd"),
    MAX( 365 * 10, "max");

    companion object {
        fun findByTag(tag: String): DateRange {
            values().forEach {
                if (it.tag == tag) {
                    return it
                }
            }
            throw IllegalArgumentException()
        }
    }
}