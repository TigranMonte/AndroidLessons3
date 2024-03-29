package ru.tikodvlp.criminalintent.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ru.tikodvlp.criminalintent.Crime
import java.util.*

@Dao
interface CrimeDao {
    @Query("Select * FROM crime")
    fun getCrimes(): LiveData<List<Crime>>

    @Query("Select * FROM crime WHERE id=(:id)")
    fun getCrime(id: UUID): LiveData<Crime?>

    @Update
    fun updateCrime(crime: Crime)

    @Insert
    fun addCrime(crime: Crime)
}