package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.HeartAiDao
import com.example.data.local.entities.*

@Database(
    entities = [
        PatientEntity::class,
        MedicalRecordEntity::class,
        HealthMeasurementEntity::class,
        CarePlanEntity::class,
        UnresolvedSignalEntity::class,
        EmergencyEventEntity::class,
        SymptomAssessmentEntity::class,
        AppSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class HeartAiDatabase : RoomDatabase() {
    abstract fun dao(): HeartAiDao

    companion object {
        @Volatile
        private var INSTANCE: HeartAiDatabase? = null

        fun getDatabase(context: Context): HeartAiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HeartAiDatabase::class.java,
                    "yaqeenallah_heart_ai.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
