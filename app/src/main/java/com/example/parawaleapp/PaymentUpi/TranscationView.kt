package com.example.parawaleapp.PaymentUpi

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//data class Transaction(
//    val id: String,
//    val amount: Double,
//    val status: String,
//    val date: Long
//)
//
////@Dao
////interface TransactionDao {
////    @Insert(onConflict = OnConflictStrategy.IGNORE)
////    suspend fun insert(transaction: TransactionEntity)
////
////    @Query("SELECT * FROM transactionentity ORDER BY date DESC")
////    fun getAllTransactions(): LiveData<List<TransactionEntity>>
////}
//
//
////@Database(entities = [TransactionEntity::class], version = 1)
////abstract class AppDatabase : RoomDatabase() {
////    abstract fun transactionDao(): TransactionDao
////}
//
//
//class TransactionRepository(private val transactionDao: TransactionDao) {
//    val allTransactions: LiveData<List<TransactionEntity>> = transactionDao.getAllTransactions()
//
//    suspend fun insert(transaction: TransactionEntity) {
//        transactionDao.insert(transaction)
//    }
//}
//
//class TransactionViewModel(application: Application) : AndroidViewModel(application) {
//    private val repository: TransactionRepository
//    val allTransactions: LiveData<List<TransactionEntity>>
//
//    init {
//        val transactionDao = AppDatabase.getDatabase(application).transactionDao()
//        repository = TransactionRepository(transactionDao)
//        allTransactions = repository.allTransactions
//    }
//
//    fun addTransaction(transaction: Transaction) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val transactionEntity = TransactionEntity(
//                id = transaction.id,
//                amount = transaction.amount,
//                status = transaction.status,
//                date = transaction.date
//            )
//            repository.insert(transactionEntity)
//        }
//    }
//}
//
//
//
//@Database(entities = [TransactionEntity::class], version = 1)
//abstract class AppDatabase : RoomDatabase() {
//    abstract fun transactionDao(): TransactionDao
//
//    companion object {
//        @Volatile
//        private var INSTANCE: AppDatabase? = null
//
//        fun getDatabase(context: Context): AppDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    AppDatabase::class.java,
//                    "transaction_database"
//                ).build()
//                INSTANCE = instance
//                instance
//            }
//        }
//    }
//}
//
//
//
//@Entity
//data class TransactionEntity(
//    @PrimaryKey val id: String,
//    val amount: Double,
//    val status: String,
//    val date: Long
//)
