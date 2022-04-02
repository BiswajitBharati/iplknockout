package com.decathlon.iplknockout.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.decathlon.iplknockout.data.model.PairTeams
import com.decathlon.iplknockout.data.model.TeamEntity
import com.decathlon.iplknockout.data.repository.TeamsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivityViewModel(private val teamsRepository: TeamsRepository) : ViewModel() {
    companion object {
        private val TAG = MainActivityViewModel::class.java.name
    }

    private val _saveStatus = MutableLiveData<Boolean>()
    val saveStatus: LiveData<Boolean> = _saveStatus

    private val _resetStatus = MutableLiveData<Boolean>()
    val resetStatus: LiveData<Boolean> = _resetStatus

    private val _teamsListByLevel = MutableLiveData<ArrayList<TeamEntity>>()
    val teamsListByLevel: LiveData<ArrayList<TeamEntity>> = _teamsListByLevel

    private val _defaultTeamsList = MutableLiveData<ArrayList<TeamEntity>>()
    val defaultTeamsList: LiveData<ArrayList<TeamEntity>> = _defaultTeamsList

    private val _teamsList = MutableLiveData<ArrayList<TeamEntity>>()
    val teamsList: LiveData<ArrayList<TeamEntity>> = _teamsList

    private val _winningTeamsList = MutableLiveData<ArrayList<TeamEntity>?>()
    val winningTeamsList: LiveData<ArrayList<TeamEntity>?> = _winningTeamsList

    private val _pairedTeamsList = MutableLiveData<ArrayList<PairTeams>?>()
    val pairedTeamsList: LiveData<ArrayList<PairTeams>?> = _pairedTeamsList

    fun getTeamsByLevel(level: Int) {
        Log.d(TAG, "saveTeams() == level: $level")
        CoroutineScope(Dispatchers.IO).launch {
            val teamsList = teamsRepository.getTeamsByLevel(level)
            _teamsListByLevel.postValue(teamsList)
        }
    }

    fun getTeams(isDefault: Boolean) {
        Log.d(TAG, "saveTeams() == isDefault: $isDefault")
        CoroutineScope(Dispatchers.IO).launch {
            val teamsList = if (isDefault) teamsRepository.getDefaultTeams() else teamsRepository.getStoredTeams()
            if (isDefault) _defaultTeamsList.postValue(teamsList) else _teamsList.postValue(teamsList)
        }
    }

    fun saveTeams(teamsList: ArrayList<TeamEntity>) {
        Log.d(TAG, "saveTeams() == teamsList: $teamsList")
        CoroutineScope(Dispatchers.IO).launch {
            val status = teamsRepository.saveTeams(teamsList = teamsList)
            _saveStatus.postValue(status)
        }
    }

    fun getPairedTeams(teamsList: ArrayList<TeamEntity>) {
        Log.d(TAG, "getPairedTeams() == teamsList: $teamsList")
        CoroutineScope(Dispatchers.IO).launch {
            val pairTeamList = teamsRepository.getPairedTeams(teamsList)
            _pairedTeamsList.postValue(pairTeamList)
        }
    }

    fun getWinningTeams(pairTeamList: ArrayList<PairTeams>?) {
        Log.d(TAG, "getWinningTeams() == pairTeamList: $pairTeamList")
        CoroutineScope(Dispatchers.IO).launch {
            val teamsList = teamsRepository.getWinningTeams(pairTeamList)
            _winningTeamsList.postValue(teamsList)
        }
    }

    fun reset() {
        Log.d(TAG, "reset()")
        CoroutineScope(Dispatchers.IO).launch {
            val status = teamsRepository.reset()
            _resetStatus.postValue(status)
        }
    }
}