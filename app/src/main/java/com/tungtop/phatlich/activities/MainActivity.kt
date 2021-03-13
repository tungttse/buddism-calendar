package com.tungtop.phatlich

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.tungtop.phatlich.fragements.CalendarFragement
import com.tungtop.phatlich.fragements.DayFragement
import com.tungtop.phatlich.fragements.EventFragement
import com.tungtop.phatlich.fragements.SettingFragement
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private var dayFragment: DayFragement? = null
    private var calendarFragment: CalendarFragement? = null
    private var eventFragment: EventFragement? = null
    private var settingFragment: SettingFragement? = null


    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_day -> {
//                if (dayFragment == null) {
                    dayFragment = DayFragement()
//                }
                    loadFragment(dayFragment!!)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_calendar -> {
                    calendarFragment = CalendarFragement()
                    loadFragment(calendarFragment!!)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_event -> {
//                if (eventFragment == null) {
                    eventFragment = EventFragement()
//                }
                    loadFragment(eventFragment!!)
                    return@OnNavigationItemSelectedListener true
                }

                R.id.navigation_setting -> {
                    if (settingFragment == null) {
                        settingFragment = SettingFragement()
                    }
                    loadFragment(settingFragment!!)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    fun BottomNavigationView.disableShiftMode() {
        val menuView = getChildAt(0) as BottomNavigationMenuView

        menuView.javaClass.getDeclaredField("mShiftingMode").apply {
            isAccessible = true
            setBoolean(menuView, false)
            isAccessible = false
        }

        @SuppressLint("RestrictedApi")
        for (i in 0 until menuView.childCount) {
            (menuView.getChildAt(i) as BottomNavigationItemView).apply {
                setShiftingMode(false)
                setChecked(false)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navigationView = findViewById<View>(R.id.navigation) as BottomNavigationView
        navigationView.disableShiftMode()

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        //TODO: hardcode
        navigation.selectedItemId = R.id.navigation_calendar
    }

    /**
     * add/replace fragment in container
     */
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.content, fragment, fragment.javaClass.simpleName)
            .addToBackStack(null)
            .commit()
    }
}
