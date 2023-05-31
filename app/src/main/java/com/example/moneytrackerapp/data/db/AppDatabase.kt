package com.example.moneytrackerapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.moneytrackerapp.data.dao.CategoryDao
import com.example.moneytrackerapp.data.dao.ExpenseDao
import com.example.moneytrackerapp.data.entity.Category
import com.example.moneytrackerapp.data.entity.Expense


@Database(version = 1, entities = [Category::class, Expense::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun getCategoryDao(): CategoryDao
    abstract fun getExpenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "money_tracker")
                    .createFromAsset("money_tracker.db")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }

}