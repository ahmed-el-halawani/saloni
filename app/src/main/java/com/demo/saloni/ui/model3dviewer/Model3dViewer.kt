package com.demo.saloni.ui.model3dviewer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.demo.saloni.databinding.Fragment3dViewerBinding
import com.demo.saloni.ui.core.BaseFragment

class Model3dViewer : BaseFragment() {
    val binding: Fragment3dViewerBinding by lazy {
        Fragment3dViewerBinding.inflate(layoutInflater)
    }

    val args: Model3dViewerArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setBackButton(binding.btnBack)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.webview.loadUrl(args.link)
    }
}