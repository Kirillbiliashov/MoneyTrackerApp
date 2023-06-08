package com.example.moneytrackerapp.viewmodel.repository

import com.example.moneytrackerapp.data.entity.Category
import com.example.moneytrackerapp.data.repo.CategoryRepository
import com.example.moneytrackerapp.viewmodel.TestDatasource
import kotlinx.coroutines.flow.MutableStateFlow

class TestCategoriesRepository : CategoryRepository {
    var saveMethodCalled: Boolean = false
        private set

    override fun getAllCategoriesFlow() =
        MutableStateFlow(TestDatasource.categories)

    override suspend fun saveCategory(category: Category) {
        saveMethodCalled = true
    }

}