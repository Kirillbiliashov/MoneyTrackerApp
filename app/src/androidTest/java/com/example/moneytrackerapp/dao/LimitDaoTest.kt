package com.example.moneytrackerapp.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.moneytrackerapp.data.dao.LimitDao
import com.example.moneytrackerapp.data.db.AppDatabase
import com.example.moneytrackerapp.data.entity.Category
import com.example.moneytrackerapp.data.entity.Expense
import com.example.moneytrackerapp.data.entity.Limit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class LimitDaoTest {

    private lateinit var appDb: AppDatabase
    private lateinit var limitDao: LimitDao

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        appDb = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        limitDao = appDb.getLimitDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        appDb.close()
    }

    @Test
    @Throws(Exception::class)
    fun limitDaoInsert_insertItemsIntoDb() = runBlocking {
        val limit1 = Limit(id = 1, sum = 1000.0, startDate = 1000, endDte = 2000)
        val limit2 = Limit(id = 2, sum = 1000.0, startDate = 500, endDte = 1500)
        limitDao.saveLimit(limit1)
        limitDao.saveLimit(limit2)
        val limits = limitDao.getAllOrdered().first()
        Assert.assertEquals(limit1, limits[1])
        Assert.assertEquals(limit2, limits[0])
    }

}