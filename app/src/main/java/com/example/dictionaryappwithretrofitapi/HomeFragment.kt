package com.example.dictionaryappwithretrofitapi

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dictionaryappwithretrofitapi.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: MeaningAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = MeaningAdapter(emptyList())
        binding.meaningRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.meaningRecyclerView.adapter = adapter

        // Word of the Day logic
        showWordOfTheDay()

        binding.searchBtn.setOnClickListener {
            val word = binding.searchInput.text.toString().trim()
            if (word.isNotEmpty()) {
                getMeaning(word)
                hideWordOfTheDay()
            }
        }
        binding.saveButton.setOnClickListener {
            val word = binding.wordTextview.text.toString()
            val phonetic = binding.phoneticTextview.text.toString()
            val meaning = adapter.meaningList.joinToString("\n") { it.definitions.joinToString(",") }

            if (word.isNotEmpty() && meaning.isNotEmpty()) {
                val dbHelper = DatabaseHelper(requireContext()) // Use requireContext() for accessing the context
                if (dbHelper.insertWord(word, phonetic, meaning)) {
                    Toast.makeText(requireContext(), "Word saved successfully", Toast.LENGTH_SHORT).show() // Use requireContext()
                } else {
                    Toast.makeText(requireContext(), "Failed to save word", Toast.LENGTH_SHORT).show() // Use requireContext()
                }
            }
        }


    }

    private fun showWordOfTheDay() {
        // A predefined list of words to improve vocabulary
        val vocabWords = listOf(
            "serendipity", "ephemeral", "eloquent", "ambiguous", "conundrum",
            "alacrity", "cogent", "magnanimous", "ubiquitous", "veracity",
            "lucid", "nuance", "pragmatic", "tenacity", "zealous",
            "serendipity", "ephemeral", "eloquent", "ambiguous", "conundrum",
            "alacrity", "cogent", "magnanimous", "ubiquitous", "veracity",
            "lucid", "nuance", "pragmatic", "tenacity", "zealous",
            "sophisticated", "idiosyncratic", "ineffable", "ebullient", "pernicious",
            "luminous", "halcyon", "resilient", "esoteric", "mellifluous",
            "iridescent", "ambivalence", "nostalgia", "ethereal", "obfuscate",
            "sanguine", "inevitable", "taciturn", "euphoria", "quintessential",
            "labyrinthine", "mercurial", "oblivion", "vivacious", "transient",
            "juxtaposition", "impetuous", "cacophony", "ostentatious", "gregarious",
            "empathy", "meticulous", "auspicious", "epiphany", "ponderous",
            "egalitarian", "surreptitious", "camaraderie", "inexorable", "reverie",
            "eclectic", "cathartic", "propensity", "aesthetic", "perspicacious",
            "capricious", "recalcitrant", "effervescent", "serene", "obdurate"
        )

        // Get the current index of the word of the day from SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("WordOfTheDayPrefs", Context.MODE_PRIVATE)
        var currentWordIndex = sharedPreferences.getInt("wordIndex", 0) // Default to 0 if no value exists

        // Fetch the word of the day based on the current index
        val wordOfTheDay = vocabWords[currentWordIndex]

        // Fetch and display the meaning of the word
        getMeaning(wordOfTheDay)

        // Ensure search inputs are visible
        binding.searchInput.visibility = View.VISIBLE
        binding.searchBtn.visibility = View.VISIBLE

        // Increment the word index to show the next word the next time the app is opened
        currentWordIndex++

        // If we've reached the end of the list, reset to 0 (optional, so it starts from the first word again)
        if (currentWordIndex >= vocabWords.size) {
            currentWordIndex = 0
        }

        // Save the updated word index to SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putInt("wordIndex", currentWordIndex)
        editor.apply()
    }
    private fun hideWordOfTheDay() {
        // Clear RecyclerView and other UI components if necessary
        binding.wordTextview.text = ""
        binding.phoneticTextview.text = ""
        binding.searchInput.text.clear()
        binding.wordOfTheDay.visibility = View.GONE

        adapter.updateNewData(emptyList())
    }
    private fun getMeaning(word: String) {
        setInProgress(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.dictionaryApi.getMeaning(word)
                if (response.body() == null) throw Exception()

                withContext(Dispatchers.Main) {
                    setInProgress(false)
                    response.body()?.first()?.let {
                        setUi(it)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    setInProgress(false)
                    Toast.makeText(requireContext(), "ERROR", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setUi(response: WordResult) {
        binding.wordTextview.text = response.word
        binding.phoneticTextview.text = response.phonetic
        adapter.updateNewData(response.meanings)
    }

    private fun setInProgress(inProgress: Boolean) {
        if (inProgress) {
            binding.searchBtn.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.searchBtn.visibility = View.VISIBLE
            binding.progressBar.visibility = View.INVISIBLE
        }
    }
}
