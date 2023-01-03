package com.example.dragview.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import com.example.dragview.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
        private const val TAG = "MainFragment"
    }

    private lateinit var binding: FragmentMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.draggableThumb.setOnTouchListener(setMyViewListener())
    }

    private var dx = 0f
    private var dy = 0f

    private fun setMyViewListener(): View.OnTouchListener {
        return View.OnTouchListener { view, event ->
            view.performClick()
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.d(TAG, "ACTION_DOWN: view.x: ${view.x}, view.y: ${view.y}, event.rawX: ${event.rawX}, event.rawY: ${event.rawY}")
                    dx = binding.message.x - event.rawX
                    dy = binding.message.y - event.rawY
                    Log.d(TAG, "ACTION_DOWN: dx = $dx, dy = $dy")
                }
                MotionEvent.ACTION_MOVE -> {
                    Log.d(TAG, "ACTION_MOVE: event.rawX: ${event.rawX}, event.rawY: ${event.rawY}, dy: $dy, binding.root.y = ${binding.root.y}, binding.root.height: ${binding.root.height}")
                    val totalHeight = binding.main.translationY
                    val messageHeight = binding.message.translationY
                    val dottedLineHeight = binding.startDottedLine.translationY
                    Log.d(TAG, "ACTION_MOVE: totalHeight = $totalHeight, messageHeight = $messageHeight, dottedLineHeight = $dottedLineHeight")

                    val newY = calculateNewY(event)
                    val delta = binding.message.y - newY
                    binding.message.animate()
//                        .x(event.rawX + dx) // uncomment this line to move x
                        .y(newY) // uncomment this line to move y
                        .setDuration(0)
                        .start()

                    binding.message2.animate()
//                        .x(event.rawX + dx) // uncomment this line to move x
                        .y(binding.message2.y + delta) // uncomment this line to move y
                        .setDuration(0)
                        .start()

                    binding.topLine.run {
                        val newBottom = newY.toInt()
                        layout(left, top, right, newBottom)
                    }
                    binding.bottomLine.run {
                        val newTop = newY.toInt() + binding.message.height
                        layout(left, newTop, right, bottom)
                    }
                }
            }

            true
        }
    }

    private fun calculateNewY(event: MotionEvent): Float {
        val newY = event.rawY + dy
//        val parentUpperBound = 0f

        val parentUpperBound = binding.actionBar.height.toFloat()

        return minOf(maxOf(newY, parentUpperBound), getMidPointHeight())
    }

    private fun getMidPointHeight() = binding.main.height / 2f - binding.message.height
}