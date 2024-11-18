package com.example.dictionaryappwithretrofitapi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SavedWordsAdapter(
    private val words: List<SavedWord>,
    private val onClick: (SavedWord) -> Unit
) : RecyclerView.Adapter<SavedWordsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val partOfSpeech: TextView = itemView.findViewById(R.id.pof_text_view)
        val definition: TextView = itemView.findViewById(R.id.defination_tv)
        val synonyms: TextView = itemView.findViewById(R.id.synonyms_textview)
        val antonyms: TextView = itemView.findViewById(R.id.antonyms_textview)

        fun bind(savedWord: SavedWord) {
            partOfSpeech.text = savedWord.word
            definition.text = savedWord.phonetic
            synonyms.text = savedWord.meaning
            antonyms.text = savedWord.meaning
            itemView.setOnClickListener { onClick(savedWord) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.meaning_recycler_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = words.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(words[position])
    }
}
