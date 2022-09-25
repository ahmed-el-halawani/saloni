package com.demo.barber.ui.model3dviewer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.demo.barber.databinding.Fragment3dViewerBinding
import com.demo.barber.ui.core.BaseFragment

class Model3dViewer : BaseFragment() {
    val binding: Fragment3dViewerBinding by lazy {
        Fragment3dViewerBinding.inflate(layoutInflater)
    }

    val args: Model3dViewerArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setBackButton(binding.btnBack)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.webview.settings.javaScriptEnabled = true
        binding.webview.loadUrl(args.link)
    }
}