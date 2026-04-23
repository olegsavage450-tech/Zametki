package com.example.myapp

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var editTextNote: TextInputEditText
    private lateinit var buttonAdd: MaterialButton
    private lateinit var recyclerViewNotes: RecyclerView

    private val notesList = mutableListOf<Note>()
    private lateinit var adapter: NoteAdapter

    private val prefsName = "notes_prefs"
    private val notesKey = "notes_key"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextNote = findViewById(R.id.editTextNote)
        buttonAdd = findViewById(R.id.buttonAdd)
        recyclerViewNotes = findViewById(R.id.recyclerViewNotes)

        loadNotes()

        adapter = NoteAdapter(
            notes = notesList,
            onClick = { position: Int -> showEditDialog(position) },
            onLongClick = { position: Int -> deleteNote(position) }
        )

        recyclerViewNotes.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        buttonAdd.setOnClickListener {
            addNote()
        }
    }

    private fun addNote() {
        val text = editTextNote.text?.toString()?.trim().orEmpty()

        if (text.isEmpty()) {
            Toast.makeText(this, R.string.msg_empty_note, Toast.LENGTH_SHORT).show()
            return
        }

        val currentDate = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            .format(Date())

        notesList.add(Note(text = text, createdAt = currentDate))
        adapter.notifyItemInserted(notesList.size - 1)
        recyclerViewNotes.scrollToPosition(notesList.size - 1)

        saveNotes()
        editTextNote.text?.clear()
        Toast.makeText(this, R.string.msg_note_saved, Toast.LENGTH_SHORT).show()
    }

    private fun deleteNote(position: Int) {
        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_delete_title)
            .setMessage(R.string.dialog_delete_message)
            .setPositiveButton(R.string.btn_delete) { _, _ ->
                notesList.removeAt(position)
                adapter.notifyItemRemoved(position)
                saveNotes()
                Toast.makeText(this, R.string.msg_note_deleted, Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.btn_cancel, null)
            .setCancelable(true)
            .show()
    }

    private fun showEditDialog(position: Int) {
        val editText = TextInputEditText(this).apply {
            setText(notesList[position].text)
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_edit_title)
            .setView(editText)
            .setPositiveButton(R.string.btn_save) { _, _ ->
                val newText = editText.text?.toString()?.trim().orEmpty()
                if (newText.isNotEmpty()) {
                    notesList[position].text = newText
                    adapter.notifyItemChanged(position)
                    saveNotes()
                    Toast.makeText(this, R.string.msg_note_saved, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, R.string.msg_empty_text, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.btn_cancel, null)
            .setCancelable(true)
            .show()
    }

    private fun saveNotes() {
        val sharedPreferences = getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val jsonArray = JSONArray()
        for (note in notesList) {
            val jsonObject = JSONObject().apply {
                put("text", note.text)
                put("createdAt", note.createdAt)
            }
            jsonArray.put(jsonObject)
        }

        editor.putString(notesKey, jsonArray.toString())
        editor.apply()
    }

    private fun loadNotes() {
        val sharedPreferences = getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val jsonString = sharedPreferences.getString(notesKey, null) ?: return

        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val text = jsonObject.getString("text")
                val createdAt = jsonObject.getString("createdAt")
                notesList.add(Note(text, createdAt))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

