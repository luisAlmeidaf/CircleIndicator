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
    private lateinit var pagerSnapHelper: PagerSnapHelper
    private lateinit var circleIndicator: CircleIndicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        setupViews()
        addPageSnapHelper()
        createImagesAdapter()
        setupRecyclerView()
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.rv_image_gallery)
        circleIndicator = findViewById(R.id.indicator)
    }

    private fun addPageSnapHelper() {
        pagerSnapHelper = PagerSnapHelper()
        pagerSnapHelper.attachToRecyclerView(recyclerView)
    }

    private fun createImagesAdapter() {
        imagesAdapter = ImagesAdapter()
        imagesAdapter.submitList(imagesList)
    }

    private fun setupRecyclerView() {
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