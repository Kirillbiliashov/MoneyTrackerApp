package com.example.moneytrackerapp.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.moneytrackerapp.data.dao.CategoryDao
import com.example.moneytrackerapp.data.dao.IncomeDao
import com.example.moneytrackerapp.data.db.AppDatabase
import com.example.moneytrackerapp.data.entity.Category
import com.example.moneytrackerapp.data.entity.Income
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class IncomeDaoTest {

    private lateinit var incomeDao: IncomeDao
    private lateinit var appDb: AppDatabase

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        appDb = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        incomeDao = appDb.getIncomeDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        appDb.close()
    }


    @Test
    @Throws(Exception::class)
    fun incomeDaoInsert_insertItemsIntoDb() = runBlocking {
        val income1 = Income(id = 1, year = 2022, sum = 5000.0, month = 9)
        val income2 = Income(id = 2, year = 2023, sum = 6000.0, month = 6)
        incomeDao.saveIncome(income1)
        incomeDao.saveIncome(income2)
        val incomeHistory = incomeDao.getAllOrdered().first()
        Assert.assertEquals(income2, incomeHistory[0])
        Assert.assertEquals(income1, incomeHistory[1])
    }

}