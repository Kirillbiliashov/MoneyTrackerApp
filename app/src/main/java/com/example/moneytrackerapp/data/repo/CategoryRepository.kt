package com.example.moneytrackerapp.data.repo

import com.example.moneytrackerapp.data.dao.CategoryDao
import com.example.moneytrackerapp.data.entity.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {

    fun getAllCategoriesFlow(): Flow<List<Category>>
}

class CategoryRepositoryImpl(private val categoryDao: CategoryDao) : CategoryRepository {

    override fun getAllCategoriesFlow() = categoryDao.getAll()

}