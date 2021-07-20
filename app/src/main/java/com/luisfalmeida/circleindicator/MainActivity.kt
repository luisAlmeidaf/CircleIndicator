package com.luisfalmeida.circleindicator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.luisfalmeida.circleindicator.adapter.ImagesAdapter
import com.luisfalmeida.circleindicator.circleindicator.CircleIndicator


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var imagesAdapter: ImagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        setupViews()
        setupRecyclerView()
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.rv_image_gallery)
    }

    private fun setupRecyclerView() {

        val pagerSnapHelper = PagerSnapHelper()
        pagerSnapHelper.attachToRecyclerView(recyclerView)

        imagesAdapter = ImagesAdapter()
        imagesAdapter.submitList(imagesList)

        val circleIndicator = findViewById<CircleIndicator>(R.id.indicator)

        recyclerView.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = imagesAdapter
            circleIndicator.attachToRecyclerView(this, pagerSnapHelper)
        }
    }

    private val imagesList = listOf(
        R.drawable.darth,
        R.drawable.han,
        R.drawable.luke,
        R.drawable.yoda,
    )
}