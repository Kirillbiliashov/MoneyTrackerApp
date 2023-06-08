package com.example.moneytrackerapp.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.moneytrackerapp.data.dao.CategoryDao
import com.example.moneytrackerapp.data.db.AppDatabase
import com.example.moneytrackerapp.data.entity.Category
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class CategoryDaoTest {

    private lateinit var categoryDao: CategoryDao
    private lateinit var appDb: AppDatabase

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        appDb = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        categoryDao = appDb.getCategoryDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        appDb.close()
    }

    @Test
    @Throws(Exception::class)
    fun categoryDaoInsert_insertItemsIntoDb() = runBlocking {
        val category1 = Category(id = 1, name = "Education")
        val category2 = Category(id = 2, name = "Travelling")
        categoryDao.saveCategory(category1)
        categoryDao.saveCategory(category2)
        val categories = categoryDao.getAll().first()
        Assert.assertEquals(category1, categories[0])
        Assert.assertEquals(category2, categories[1])
    }

}