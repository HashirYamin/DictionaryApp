package com.example.dictionaryappwithretrofitapi

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "SavedWordsDB", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = """
            CREATE TABLE SavedWords(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                word TEXT,
                phonetic TEXT,
                meaning TEXT
            )
        """
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS SavedWords")
        onCreate(db)
    }

    fun insertWord(word: String, phonetic: String, meaning: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put("word", word)
            put("phonetic", phonetic)
            put("meaning", meaning)
        }
        val result = db.insert("SavedWords", null, contentValues)
        db.close()
        return result != -1L
    }

    fun getAllWords(): List<SavedWord> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM SavedWords", null)
        val words = mutableListOf<SavedWord>()
        if (cursor.moveToFirst()) {
            do {
                words.add(
                    SavedWord(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3)
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return words
    }
}

data class SavedWord(val id: Int, val word: String, val phonetic: String, val meaning: String)
