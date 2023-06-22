package com.example.moneytrackerapp.data.repo

import com.example.moneytrackerapp.data.dao.CategoryDao
import com.example.moneytrackerapp.data.entity.Category
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface CategoryRepository {

    fun getAllCategoriesFlow(): Flow<List<Category>>
    suspend fun saveCategory(category: Category)
}

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun getAllCategoriesFlow() = categoryDao.getAll()
    override suspend fun saveCategory(category: Category) {
        categoryDao.saveCategory(category)
    }

}