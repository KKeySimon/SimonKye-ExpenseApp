package com.example.simonkye_expenseapp

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity
data class Expense(
    @PrimaryKey val id: UUID,
    val name: String,
    val amount: Int,
    val date: Date,
    val category: String
)
