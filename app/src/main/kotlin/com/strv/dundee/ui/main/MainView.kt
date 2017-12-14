package com.strv.dundee.ui.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.strv.dundee.R
import com.strv.dundee.databinding.ActivityMainBinding
import com.strv.ktools.vmb

interface MainView {
}

class MainActivity : AppCompatActivity(), MainView {
    private val vmb by vmb<MainViewModel, ActivityMainBinding>(R.layout.activity_main) { MainViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(vmb.binding.toolbar)
    }
}
