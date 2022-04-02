package com.decathlon.iplknockout.data.api

import android.app.Application
import android.util.Log
import com.decathlon.iplknockout.data.model.TeamEntity

class TeamsService(val application: Application) {
    companion object {
        private val TAG = TeamsService::class.java.name
    }

    private var teamDao: TeamDao

    private val database = TeamDBProvider.getInstance(application)

    init {
        teamDao = database.teamDao()
    }

    suspend fun getTeamsByLevelFromDB(level: Int): ArrayList<TeamEntity> {
        Log.d(TAG, "getTeamsByLevelFromDB() == level: $level")
        return teamDao.getTeamByWinLevel(level)?.let { ArrayList(it) } ?: ArrayList()
    }

    suspend fun getTeamByName(name : String): TeamEntity? {
        Log.d(TAG, "getTeamByName() == name: $name")
        return teamDao.getTeamByName(name)
    }

    suspend fun saveTeamToDB(teamEntity: TeamEntity?): Boolean {
        Log.d(TAG, "saveTeamToDB() == teamEntity: $teamEntity")
        teamEntity?.let {
            val count = teamDao.getTeamById(it.id)?.let {
                teamDao.update(it)
            } ?: run {
                teamDao.insert(it)
            }
            Log.d(TAG, "saveTeamToDB() == count: $count")
        }
        return true
    }

    suspend fun saveTeamsToDB(teamsList: ArrayList<TeamEntity>?): Boolean {
        Log.d(TAG, "saveTeamsToDB() == teamsList: $teamsList")
        teamsList?.forEach { saveTeamToDB(it) }

        return true
    }

    suspend fun getStoredTeamsFromDB(): ArrayList<TeamEntity> {
        Log.d(TAG, "getStoredTeamsFromDB()")
        return teamDao.getAllTeams()?.let { ArrayList(it) } ?: ArrayList()
    }

    suspend fun updateWinningLevelToDB(id: Int, level: Int): Boolean {
        Log.d(TAG, "updateWinningLevelToDB() == id: $id, level: $level")
        val count = teamDao.getTeamById(id)?.let {
            it.win_level = level
            teamDao.update(it)
        }
        Log.d(TAG, "updateWinningLevelToDB() == count: $count")
        return true
    }

    suspend fun clearAllTeamFromDB(): Boolean {
        Log.d(TAG, "clearAllTeamFromDB()")
        val count = teamDao.deleteAll()
        Log.d(TAG, "clearAllTeamFromDB() == count: $count")
        return true
    }

    suspend fun resetAllTeamToDB(): Boolean {
        Log.d(TAG, "resetAllTeamToDB()")
        teamDao.getAllTeams()?.forEach {
            it.win_level = -1
            teamDao.update(it)
        }
        return true
    }
}