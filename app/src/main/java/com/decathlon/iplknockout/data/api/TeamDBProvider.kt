package com.decathlon.iplknockout.data.api

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.decathlon.iplknockout.data.model.TeamEntity

@Database(entities = [TeamEntity::class], version = 1)
abstract class TeamDBProvider : RoomDatabase() {
    companion object {
        private val TAG = TeamDBProvider::class.java.name

        private var instance: TeamDBProvider? = null

        @Synchronized
        fun getInstance(ctx: Context): TeamDBProvider {
            if(instance == null)
                instance = Room.databaseBuilder(ctx.applicationContext, TeamDBProvider::class.java,
                    "team_database.db")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build()

            return instance!!
        }

        private val roomCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
            }
        }
    }

    abstract fun teamDao(): TeamDao
}