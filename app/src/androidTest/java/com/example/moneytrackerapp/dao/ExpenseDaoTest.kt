package com.example.moneytrackerapp.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.moneytrackerapp.data.dao.CategoryDao
import com.example.moneytrackerapp.data.dao.ExpenseDao
import com.example.moneytrackerapp.data.db.AppDatabase
import com.example.moneytrackerapp.data.entity.Category
import com.example.moneytrackerapp.data.entity.Expense
import com.example.moneytrackerapp.data.entity.ExpenseTuple
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ExpenseDaoTest {

    private lateinit var expenseDao: ExpenseDao
    private lateinit var categoryDao: CategoryDao
    private lateinit var appDb: AppDatabase

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        appDb = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        expenseDao = appDb.getExpenseDao()
        categoryDao = appDb.getCategoryDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        appDb.close()
    }

    @Test
    @Throws(Exception::class)
    fun expenseDaoInsert_insertItemsIntoDb() = runBlocking {
        val category = Category(id = 1, name = "Games")
        val expense1 = Expense(
            id = 1, name = "God of war",
            19.99, 1000, categoryId = category.id
        )
        val expense2 = Expense(
            id = 2, name = "Red dead redemption 2",
            25.99, 2000, categoryId = category.id
        )
        categoryDao.saveCategory(category)
        expenseDao.saveExpense(expense1)
        expenseDao.saveExpense(expense2)
        val expenses = expenseDao.getAll().first()
        Assert.assertTrue(expenses[0].isEqualToExpense(expense1, category))
        Assert.assertTrue(expenses[1].isEqualToExpense(expense2, category))

    }

}

private fun ExpenseTuple.isEqualToExpense(expense: Expense, relatedCategory: Category) =
    id == expense.id && name == expense.name && sum == expense.sum &&
            date == expense.date && note == expense.note &&
            categoryName == relatedCategory.name