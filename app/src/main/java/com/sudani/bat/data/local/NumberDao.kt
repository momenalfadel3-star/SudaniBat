package com.sudani.bat.data.local

import androidx.room.*
import com.sudani.bat.data.model.SudaniNumber
import kotlinx.coroutines.flow.Flow

@Dao
interface NumberDao {
    @Query("SELECT * FROM numbers")
    fun getAllNumbers(): Flow<List<SudaniNumber>>

    @Query("SELECT * FROM numbers WHERE msisdn = :msisdn")
    suspend fun getNumberByMsisdn(msisdn: String): SudaniNumber?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNumber(number: SudaniNumber)

    @Update
    suspend fun updateNumber(number: SudaniNumber)

    @Delete
    suspend fun deleteNumber(number: SudaniNumber)

    @Query("SELECT * FROM numbers")
    suspend fun getAllNumbersList(): List<SudaniNumber>
}
