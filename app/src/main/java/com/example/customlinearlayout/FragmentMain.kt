package com.example.customlinearlayout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.customlinearlayout.databinding.FragmentMainBinding
import java.lang.RuntimeException


class FragmentMain : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding
    get() = _binding ?: throw RuntimeException("Binding is null")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
    }

    private fun initBinding() {
        with(binding) {
            one.setOnClickListener {
                parentFragmentManager.beginTransaction().replace(R.id.fragment_container, FragmentOne()).addToBackStack("One").commit()
            }
            two.setOnClickListener {
                parentFragmentManager.beginTransaction().replace(R.id.fragment_container, FragmentTwo()).addToBackStack("Two").commit()
            }
            three.setOnClickListener {
                parentFragmentManager.beginTransaction().replace(R.id.fragment_container, FragmentThree()).addToBackStack("Three").commit()
            }
            four.setOnClickListener {
                parentFragmentManager.beginTransaction().replace(R.id.fragment_container, FragmentFour()).addToBackStack("Four").commit()
            }
            five.setOnClickListener {
                parentFragmentManager.beginTransaction().replace(R.id.fragment_container, FragmentFive()).addToBackStack("Five").commit()
            }
            six.setOnClickListener {
                parentFragmentManager.beginTransaction().replace(R.id.fragment_container, FragmentSix()).addToBackStack("Six").commit()
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}