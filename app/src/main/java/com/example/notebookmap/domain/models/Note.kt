package com.example.notebookmap.domain.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.notebookmap.utils.Constants.DEFAULT_LATITUDE
import com.example.notebookmap.utils.Constants.DEFAULT_LONGITUDE

@Entity(tableName = "note_table")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "note_title")
    val noteTitle: String,
    @ColumnInfo(name = "note_text")
    val noteText: String? = "", // почему null
    @ColumnInfo(name = "note_latitude")
    val noteLatitude: Double? = DEFAULT_LATITUDE,
    @ColumnInfo(name = "note_longitude")
    val noteLongitude: Double? = DEFAULT_LONGITUDE,
    @ColumnInfo(name = "note_date")
    val noteDate: Long? = 1, // можно использовать dd.MM.yyyy
    @ColumnInfo(name = "note_time")
    val noteTime: Long? = 1
)
