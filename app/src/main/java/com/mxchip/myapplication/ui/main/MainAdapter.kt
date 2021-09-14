package com.mxchip.myapplication.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mxchip.myapplication.bean.DeviceBean
import com.mxchip.myapplication.databinding.ItemMainBinding

class MainAdapter(
    private val data: List<DeviceBean>,
    private val itemClickListener: (device: DeviceBean) -> Unit,
    private val itemClickLongListener: (device: DeviceBean) -> Unit
) : RecyclerView.Adapter<MainAdapter.MainVH>() {

    inner class MainVH(private val binding: ItemMainBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            bean: DeviceBean,
            itemClickListener: (device: DeviceBean) -> Unit
        ) {
            binding.bean = bean
            binding.root.setOnClickListener {
                itemClickListener.invoke(bean)
            }
            binding.root.setOnLongClickListener {
                itemClickLongListener.invoke(bean)
                true
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainVH {
        return MainVH(ItemMainBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MainVH, position: Int) {
        holder.bind(data[position], itemClickListener)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}