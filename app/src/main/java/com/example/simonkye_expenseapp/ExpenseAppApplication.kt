package com.example.simonkye_expenseapp

import android.app.Application

class ExpenseAppApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ExpenseRepository.initialize(this)
    }
}