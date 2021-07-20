package com.luisfalmeida.circleindicator.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.luisfalmeida.circleindicator.adapter.ImagesAdapter.*
import com.luisfalmeida.circleindicator.databinding.ItemGalleryBinding

class ImagesAdapter : ListAdapter<Int, ImagesViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImagesViewHolder {
        return ImagesViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Int>() {
            override fun areItemsTheSame(
                oldItem: Int,
                newItem: Int
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: Int,
                newItem: Int
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    class ImagesViewHolder(
        private val itemBinding: ItemGalleryBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(image: Int) {
            itemBinding.run {
                galleryImage.setImageResource(image)
            }
        }

        companion object {
            fun create(parent: ViewGroup): ImagesViewHolder {
                val itemBinding = ItemGalleryBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)

                return ImagesViewHolder(itemBinding)
            }
        }
    }
}