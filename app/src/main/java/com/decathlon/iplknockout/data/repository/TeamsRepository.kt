package com.decathlon.iplknockout.data.repository

import android.util.Log
import com.decathlon.iplknockout.data.api.TeamsService
import com.decathlon.iplknockout.data.model.PairTeams
import com.decathlon.iplknockout.data.model.TeamEntity

class TeamsRepository(private val teamsService: TeamsService) {
    companion object {
        private val TAG = TeamsRepository::class.java.name
    }

    suspend fun getDefaultTeams(): ArrayList<TeamEntity> {
        Log.d(TAG, "getDefaultTeams()")
        val teamsList: ArrayList<TeamEntity> = ArrayList()
        val teamNames = arrayOf("Mumbai Indians",
            "Sunrisers Hyderabad",
            "Royal Challengers Bangalore",
            "Rajasthan Royals",
            "Chennai Super Kings",
            "Kolkata Knight Riders",
            "Delhi Capitals",
            "Kings XI Punjab")
        for (id in teamNames.indices) {
            teamsList.add(TeamEntity(id = id + 1, name = teamNames[id]))
        }
        return teamsList
    }

    suspend fun getStoredTeams(): ArrayList<TeamEntity> {
        Log.d(TAG, "getStoredTeams()")
        return teamsService.getStoredTeamsFromDB()
    }

    suspend fun getTeamsByLevel(level: Int): ArrayList<TeamEntity> {
        Log.d(TAG, "getTeamsByLevel() == level: $level")
        return teamsService.getTeamsByLevelFromDB(level)
    }

    suspend fun getTeamByName(name : String): TeamEntity? {
        Log.d(TAG, "getTeamByName() == name: $name")
        return teamsService.getTeamByName(name)
    }

    suspend fun saveTeam(teamsEntity: TeamEntity?): Boolean {
        Log.d(TAG, "saveTeam() == teamsEntity: $teamsEntity")
        return teamsService.saveTeamToDB(teamsEntity)
    }

    suspend fun saveTeams(teamsList: ArrayList<TeamEntity>?): Boolean {
        Log.d(TAG, "saveTeams() == teamsList: $teamsList")
        return teamsService.saveTeamsToDB(teamsList)
    }

    suspend fun getWinningTeams(pairTeamList: ArrayList<PairTeams>?): ArrayList<TeamEntity>? {
        Log.d(TAG, "getWinningTeams() == pairTeamList: $pairTeamList")
        if (pairTeamList.isNullOrEmpty()) return null

        val teamsList: ArrayList<TeamEntity> = ArrayList()

        for (pairTeam in pairTeamList) {
            val randomNumber = (0..1).random()
            (if (randomNumber == 0) pairTeam.first else pairTeam.second)?.let { teamsList.add(it) }
        }

        if (teamsList.size == 1) {
            if (teamsList[0].win_level == 1) { // For 1st Position
                teamsList[0].win_level = pairTeamList.size / 2
            } else { // For 3rd Position
                teamsList[0].win_level = 3
            }
            teamsService.updateWinningLevelToDB(teamsList[0].id, teamsList[0].win_level)
        }
        return teamsList
    }

    suspend fun getPairedTeams(teamsList: ArrayList<TeamEntity>?): ArrayList<PairTeams>? {
        Log.d(TAG, "getPairedTeams() == teamsList: $teamsList")
        if (teamsList.isNullOrEmpty()) return null

        val pairListSize = teamsList.size.div(2)
        val pairTeamList: ArrayList<PairTeams> = ArrayList()

        var teamFirstPair: TeamEntity? = null
        var teamSecondPair: TeamEntity? = null

        while (teamsList.size != 0 && pairListSize != pairTeamList.size) {
            val randomNumber = (0 until teamsList.size).random()

            if (null == teamFirstPair) {
                teamFirstPair = teamsList.removeAt(randomNumber)
            } else {
                teamSecondPair = teamsList.removeAt(randomNumber)
                val pairTeam = PairTeams(first = teamFirstPair, second = teamSecondPair)

                pairTeam.first?.win_level = pairListSize
                pairTeam.first?.let { teamsService.updateWinningLevelToDB(it.id, it.win_level) }
                pairTeam.second?.win_level = pairListSize
                pairTeam.second?.let { teamsService.updateWinningLevelToDB(it.id, it.win_level) }

                pairTeamList.add(pairTeam)

                teamFirstPair = null
                teamSecondPair = null
            }
        }
        return pairTeamList
    }

    suspend fun reset(): Boolean {
        Log.d(TAG, "reset()")
        return teamsService.resetAllTeamToDB()
    }

    suspend fun clear(): Boolean {
        Log.d(TAG, "clear()")
        return teamsService.clearAllTeamFromDB()
    }
}