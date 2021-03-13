package com.tungtop.phatlich.fragements

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tungtop.phatlich.R

class SettingFragement: Fragment() {
    private var cacheView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (cacheView == null) {
            cacheView = inflater.inflate(R.layout.fragement_setting, container, false)
        }
        return cacheView
    }
}