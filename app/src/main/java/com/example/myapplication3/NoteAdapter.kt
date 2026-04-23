package com.example.myapp

import com.example.myapp.R
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class NoteAdapter(
    private val notes: MutableList<Note>,
    private val onClick: (Int) -> Unit,
    private val onLongClick: (Int) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(itemView: MaterialCardView) : RecyclerView.ViewHolder(itemView) {
        val textViewNote: TextView = itemView.findViewById(R.id.textViewNote)
        val textViewDate: TextView = itemView.findViewById(R.id.textViewDate)

        fun bind(note: Note, position: Int) {
            textViewNote.text = note.text
            textViewDate.text = "📅 ${note.createdAt}"

            itemView.setOnClickListener { onClick(position) }
            itemView.setOnLongClickListener {
                onLongClick(position)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false) as MaterialCardView
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(notes[position], position)
    }

    override fun getItemCount(): Int = notes.size
}
