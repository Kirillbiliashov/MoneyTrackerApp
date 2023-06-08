package com.example.moneytrackerapp.viewmodel.repository

import com.example.moneytrackerapp.data.entity.ExpenseTuple
import com.example.moneytrackerapp.data.repo.SaveFileRepository

class TestSaveFileRepository: SaveFileRepository {
    var saved = false
    private set
    override fun saveExpensesToFile(expenses: List<ExpenseTuple>) {
        saved = true
    }
}