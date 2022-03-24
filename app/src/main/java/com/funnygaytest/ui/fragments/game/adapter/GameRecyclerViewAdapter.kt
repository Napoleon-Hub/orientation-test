package com.funnygaytest.ui.fragments.game.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.funnygaytest.data.room.entity.Answer
import com.funnygaytest.databinding.ItemAnswerBinding

class GameRecyclerViewAdapter :
    ListAdapter<Answer, GameRecyclerViewAdapter.ItemViewHolder>(DiffUtilCallback) {

    var listener: ((position: Int, points: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemPersonBinding = ItemAnswerBinding.inflate(layoutInflater, parent, false)
        return ItemViewHolder(itemPersonBinding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        if (getItem(position) != null)holder.bind(position, getItem(position), listener)
    }

    class ItemViewHolder(private val binding: ItemAnswerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, answer: Answer, listener: ((position: Int, points: Int) -> Unit)?) {
            binding.apply {
                tvAnswer.text = answer.answerString
                btnAnswer.isChecked = answer.pressed
                viewAnswer.setOnClickListener {
                    listener?.invoke(position, answer.answerPoints)
                    answer.pressed = true
                    btnAnswer.isChecked = answer.pressed
                }
            }
        }
    }

    object DiffUtilCallback : DiffUtil.ItemCallback<Answer>() {
        override fun areContentsTheSame(oldItem: Answer, newItem: Answer) =
            oldItem.answerPoints == newItem.answerPoints && oldItem.answerString == newItem.answerString
                    && oldItem.pressed == newItem.pressed

        override fun areItemsTheSame(oldItem: Answer, newItem: Answer) = oldItem == newItem
    }

}