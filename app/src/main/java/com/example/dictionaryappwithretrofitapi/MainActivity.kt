package com.example.dictionaryappwithretrofitapi

import android.app.Activity
import android.os.Bundle
import android.provider.Settings.Global
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dictionaryappwithretrofitapi.databinding.ActivityMainBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var adapter: MeaningAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = MeaningAdapter(emptyList())
        binding.meaningRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.meaningRecyclerView.adapter = adapter

        // Set up Word of the Day
        showWordOfTheDay()

        binding.searchBtn.setOnClickListener {
            val word = binding.searchInput.text.toString().trim()
            if (word.isNotEmpty()) {
                getMeaning(word)
                hideWordOfTheDay()
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
        val sharedPreferences = getSharedPreferences("WordOfTheDayPrefs", MODE_PRIVATE)
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
        GlobalScope.launch {
            try {
                val response = RetrofitInstance.dictionaryApi.getMeaning(word)
                if (response.body() == null) {
                    throw (Exception())
                }
                runOnUiThread {
                    setInProgress(false)
                    response.body()?.first()?.let {
                        setUi(it)
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    setInProgress(false)
                    Toast.makeText(this@MainActivity, "ERROR", Toast.LENGTH_SHORT).show()
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
