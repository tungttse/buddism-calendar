package com.tungtop.phatlich
/**
 * @author duc
 */
object VietCalendar {
    val PI = Math.PI
    /**
     *
     * @param dd
     * @param mm
     * @param yy
     * @return the number of days since 1 January 4713 BC (Julian calendar)
     */
    fun jdFromDate(dd: Int, mm: Int, yy: Int): Int {
        val a = (14 - mm) / 12
        val y = yy + 4800 - a
        val m = mm + 12 * a - 3
        var jd = dd + (153 * m + 2) / 5 + 365 * y + y / 4 - y / 100 + y / 400 - 32045
        if (jd < 2299161) {
            jd = dd + (153 * m + 2) / 5 + 365 * y + y / 4 - 32083
        }
        //jd = jd - 1721425;
        return jd
    }

    /**
     * http://www.tondering.dk/claus/calendar.html
     * Section: Is there a formula for calculating the Julian day number?
     * @param jd - the number of days since 1 January 4713 BC (Julian calendar)
     * @return
     */
    fun jdToDate(jd: Int): IntArray {
        val a: Int
        val b: Int
        val c: Int
        if (jd > 2299160) { // After 5/10/1582, Gregorian calendar
            a = jd + 32044
            b = (4 * a + 3) / 146097
            c = a - b * 146097 / 4
        } else {
            b = 0
            c = jd + 32082
        }
        val d = (4 * c + 3) / 1461
        val e = c - 1461 * d / 4
        val m = (5 * e + 2) / 153
        val day = e - (153 * m + 2) / 5 + 1
        val month = m + 3 - 12 * (m / 10)
        val year = b * 100 + d - 4800 + m / 10
        return intArrayOf(day, month, year)
    }

    /**
     * Solar longitude in degrees
     * Algorithm from: Astronomical Algorithms, by Jean Meeus, 1998
     * @param jdn - number of days since noon UTC on 1 January 4713 BC
     * @return
     */
    fun SunLongitude(jdn: Double): Double {
        //return CC2K.sunLongitude(jdn);
        return SunLongitudeAA98(jdn)
    }

    fun SunLongitudeAA98(jdn: Double): Double {
        val T = (jdn - 2451545.0) / 36525 // Time in Julian centuries from 2000-01-01 12:00:00 GMT
        val T2 = T * T
        val dr = PI / 180 // degree to radian
        val M = 357.52910 + 35999.05030 * T - 0.0001559 * T2 - 0.00000048 * T * T2 // mean anomaly, degree
        val L0 = 280.46645 + 36000.76983 * T + 0.0003032 * T2 // mean longitude, degree
        var DL = (1.914600 - 0.004817 * T - 0.000014 * T2) * Math.sin(dr * M)
        DL = DL + (0.019993 - 0.000101 * T) * Math.sin(dr * 2.0 * M) + 0.000290 * Math.sin(dr * 3.0 * M)
        var L = L0 + DL // true longitude, degree
        L = L - 360 * INT(L / 360) // Normalize to (0, 360)
        return L
    }

    fun NewMoon(k: Int): Double {
        //return CC2K.newMoonTime(k);
        return NewMoonAA98(k)
    }

    /**
     * Julian day number of the kth new moon after (or before) the New Moon of 1900-01-01 13:51 GMT.
     * Accuracy: 2 minutes
     * Algorithm from: Astronomical Algorithms, by Jean Meeus, 1998
     * @param k
     * @return the Julian date number (number of days since noon UTC on 1 January 4713 BC) of the New Moon
     */

    fun NewMoonAA98(k: Int): Double {
        val T = k / 1236.85 // Time in Julian centuries from 1900 January 0.5
        val T2 = T * T
        val T3 = T2 * T
        val dr = PI / 180
        var Jd1 = 2415020.75933 + 29.53058868 * k + 0.0001178 * T2 - 0.000000155 * T3
        Jd1 = Jd1 + 0.00033 * Math.sin((166.56 + 132.87 * T - 0.009173 * T2) * dr) // Mean new moon
        val M = 359.2242 + 29.10535608 * k - 0.0000333 * T2 - 0.00000347 * T3 // Sun's mean anomaly
        val Mpr = 306.0253 + 385.81691806 * k + 0.0107306 * T2 + 0.00001236 * T3 // Moon's mean anomaly
        val F = 21.2964 + 390.67050646 * k - 0.0016528 * T2 - 0.00000239 * T3 // Moon's argument of latitude
        var C1 = (0.1734 - 0.000393 * T) * Math.sin(M * dr) + 0.0021 * Math.sin(2.0 * dr * M)
        C1 = C1 - 0.4068 * Math.sin(Mpr * dr) + 0.0161 * Math.sin(dr * 2.0 * Mpr)
        C1 = C1 - 0.0004 * Math.sin(dr * 3.0 * Mpr)
        C1 = C1 + 0.0104 * Math.sin(dr * 2.0 * F) - 0.0051 * Math.sin(dr * (M + Mpr))
        C1 = C1 - 0.0074 * Math.sin(dr * (M - Mpr)) + 0.0004 * Math.sin(dr * (2 * F + M))
        C1 = C1 - 0.0004 * Math.sin(dr * (2 * F - M)) - 0.0006 * Math.sin(dr * (2 * F + Mpr))
        C1 = C1 + 0.0010 * Math.sin(dr * (2 * F - Mpr)) + 0.0005 * Math.sin(dr * (2 * Mpr + M))
        val deltat: Double
        if (T < -11) {
            deltat = 0.001 + 0.000839 * T + 0.0002261 * T2 - 0.00000845 * T3 - 0.000000081 * T * T3
        } else {
            deltat = -0.000278 + 0.000265 * T + 0.000262 * T2
        }
        val JdNew = Jd1 + C1 - deltat
        return JdNew
    }

    fun INT(d: Double): Int {
        return Math.floor(d).toInt()
    }

    fun getSunLongitude(dayNumber: Int, timeZone: Double): Double {
        return SunLongitude(dayNumber.toDouble() - 0.5 - timeZone / 24)
    }

    fun getNewMoonDay(k: Int, timeZone: Double): Int {
        val jd = NewMoon(k)
        return INT(jd + 0.5 + timeZone / 24)
    }

    fun getLunarMonth11(yy: Int, timeZone: Double): Int {
        val off = jdFromDate(31, 12, yy) - 2415021.076998695
        val k = INT(off / 29.530588853)
        var nm = getNewMoonDay(k, timeZone)
        val sunLong = INT(getSunLongitude(nm, timeZone) / 30)
        if (sunLong >= 9) {
            nm = getNewMoonDay(k - 1, timeZone)
        }
        return nm
    }

    fun getLeapMonthOffset(a11: Int, timeZone: Double): Int {
        val k = INT(0.5 + (a11 - 2415021.076998695) / 29.530588853)
        var last: Int // Month 11 contains point of sun longutide 3*PI/2 (December solstice)
        var i = 1 // We start with the month following lunar month 11
        var arc = INT(getSunLongitude(getNewMoonDay(k + i, timeZone), timeZone) / 30)
        do {
            last = arc
            i++
            arc = INT(getSunLongitude(getNewMoonDay(k + i, timeZone), timeZone) / 30)
        } while (arc != last && i < 14)
        return i - 1
    }

    /**
     *
     * @param dd
     * @param mm
     * @param yy
     * @param timeZone
     * @return array of [lunarDay, lunarMonth, lunarYear, leapOrNot]
     */
    fun convertSolar2Lunar(dd: Int, mm: Int, yy: Int, timeZone: Double): IntArray {
        val lunarDay: Int
        var lunarMonth: Int
        var lunarYear: Int
        var lunarLeap: Int
        val dayNumber = jdFromDate(dd, mm, yy)
        val k = INT((dayNumber - 2415021.076998695) / 29.530588853)
        var monthStart = getNewMoonDay(k + 1, timeZone)
        if (monthStart > dayNumber) {
            monthStart = getNewMoonDay(k, timeZone)
        }
        var a11 = getLunarMonth11(yy, timeZone)
        var b11 = a11
        if (a11 >= monthStart) {
            lunarYear = yy
            a11 = getLunarMonth11(yy - 1, timeZone)
        } else {
            lunarYear = yy + 1
            b11 = getLunarMonth11(yy + 1, timeZone)
        }
        lunarDay = dayNumber - monthStart + 1
        val diff = INT(((monthStart - a11) / 29).toDouble())
        lunarLeap = 0
        lunarMonth = diff + 11
        if (b11 - a11 > 365) {
            val leapMonthDiff = getLeapMonthOffset(a11, timeZone)
            if (diff >= leapMonthDiff) {
                lunarMonth = diff + 10
                if (diff == leapMonthDiff) {
                    lunarLeap = 1
                }
            }
        }
        if (lunarMonth > 12) {
            lunarMonth = lunarMonth - 12
        }
        if (lunarMonth >= 11 && diff < 4) {
            lunarYear -= 1
        }
        return intArrayOf(lunarDay, lunarMonth, lunarYear, lunarLeap)
    }

    fun convertLunar2Solar(lunarDay: Int, lunarMonth: Int, lunarYear: Int, lunarLeap: Int, timeZone: Double): IntArray {
        val a11: Int
        val b11: Int
        if (lunarMonth < 11) {
            a11 = getLunarMonth11(lunarYear - 1, timeZone)
            b11 = getLunarMonth11(lunarYear, timeZone)
        } else {
            a11 = getLunarMonth11(lunarYear, timeZone)
            b11 = getLunarMonth11(lunarYear + 1, timeZone)
        }
        val k = INT(0.5 + (a11 - 2415021.076998695) / 29.530588853)
        var off = lunarMonth - 11
        if (off < 0) {
            off += 12
        }
        if (b11 - a11 > 365) {
            val leapOff = getLeapMonthOffset(a11, timeZone)
            var leapMonth = leapOff - 2
            if (leapMonth < 0) {
                leapMonth += 12
            }
            if (lunarLeap != 0 && lunarMonth != leapMonth) {
                println("Invalid input!")
                return intArrayOf(0, 0, 0)
            } else if (lunarLeap != 0 || off >= leapOff) {
                off += 1
            }
        }
        val monthStart = getNewMoonDay(k + off, timeZone)
        return jdToDate(monthStart + lunarDay - 1)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val TZ = 7.0
        val start = jdFromDate(1, 1, 2001)
        val step = 15
        var count = -1
        while (count++ < 240) {
            val jd = start + step * count
            val s = jdToDate(jd)
            val l = convertSolar2Lunar(s[0], s[1], s[2], TZ)
            val s2 = convertLunar2Solar(l[0], l[1], l[2], l[3], TZ)
            if (s[0] == s2[0] && s[1] == s2[1] && s[2] == s2[2]) {
                println("OK! " + s[0] + "/" + s[1] + "/" + s[2] + " -> " + l[0] + "/" + l[1] + "/" + l[2] + if (l[3] == 0) "" else " leap")
            } else {
                System.err.println("ERROR! " + s[0] + "/" + s[1] + "/" + s[2] + " -> " + l[0] + "/" + l[1] + "/" + l[2] + if (l[3] == 0) "" else " leap")
            }
        }
    }
}