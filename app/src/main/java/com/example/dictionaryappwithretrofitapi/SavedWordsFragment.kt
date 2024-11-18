package com.example.dictionaryappwithretrofitapi

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SavedWordsFragment : Fragment() {

    private lateinit var adapter: SavedWordsAdapter
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_saved_words, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DatabaseHelper(requireContext())
        val savedWords = dbHelper.getAllWords()

        adapter = SavedWordsAdapter(savedWords) { savedWord ->
            val intent = Intent(requireContext(), MainActivity::class.java).apply {
                putExtra("word", savedWord.word)
                putExtra("phonetic", savedWord.phonetic)
                putExtra("meaning", savedWord.meaning)
            }
            startActivity(intent)
        }

        view.findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@SavedWordsFragment.adapter
        }
    }
}
