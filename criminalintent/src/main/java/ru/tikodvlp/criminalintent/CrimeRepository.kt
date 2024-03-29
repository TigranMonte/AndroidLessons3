package ru.tikodvlp.criminalintent

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import ru.tikodvlp.criminalintent.database.CrimeDao
import ru.tikodvlp.criminalintent.database.CrimeDatabase
import ru.tikodvlp.criminalintent.database.migration_2_3
import java.io.File
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

private const val DATABASE_NAME = "crime-database"

class CrimeRepository private constructor(context: Context) {
    private val database: CrimeDatabase =
        Room.databaseBuilder(context.applicationContext,
            CrimeDatabase::class.java,
            DATABASE_NAME
        ).addMigrations(migration_2_3)
            .build()

    private val crimeDao: CrimeDao = database.crimeDao()

    private val executor: Executor = Executors.newSingleThreadExecutor()

    private val filesDir = context.applicationContext.filesDir

    fun getCrimes(): LiveData<List<Crime>> = crimeDao.getCrimes()

    fun getCrime(id: UUID): LiveData<Crime?> = crimeDao.getCrime(id)

    fun updateCrime(crime: Crime) {
        executor.execute {
            crimeDao.updateCrime(crime)
        }
    }

    fun addCrime(crime: Crime) {
        executor.execute {
            crimeDao.addCrime(crime)
        }
    }

    fun getPhotoFile(crime: Crime): File = File(filesDir, crime.photoFileName)

    companion object {
        private var INSTANCE: CrimeRepository? = null
        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = CrimeRepository(context)
            }
        }

        fun get(): CrimeRepository {
            return INSTANCE ?: throw IllegalStateException("CrimeRepository must be initialized")
        }
    }
}