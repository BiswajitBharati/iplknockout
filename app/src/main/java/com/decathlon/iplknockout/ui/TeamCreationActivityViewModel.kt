package com.decathlon.iplknockout.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.decathlon.iplknockout.data.model.TeamEntity
import com.decathlon.iplknockout.data.repository.TeamsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TeamCreationActivityViewModel(private val teamsRepository: TeamsRepository) : ViewModel() {
    companion object {
        private val TAG = TeamCreationActivityViewModel::class.java.name
    }
    private val teamsMap: HashMap<String, TeamEntity> = HashMap()

    private val _addTeamStatus = MutableLiveData<Pair<Boolean, Int>>()
    val addTeamStatus: LiveData<Pair<Boolean, Int>> = _addTeamStatus

    private val _saveStatus = MutableLiveData<Boolean>()
    val saveStatus: LiveData<Boolean> = _saveStatus

    private val _resetStatus = MutableLiveData<Boolean>()
    val resetStatus: LiveData<Boolean> = _resetStatus

    fun addNewTeam(teamName: String) {
        Log.d(TAG, "addNewTeam()")
        if (teamsMap.containsKey(teamName)) {
            _addTeamStatus.postValue(Pair(false, teamsMap.size))
        } else {
            teamsMap[teamName] = TeamEntity(id = teamsMap.size + 1, name = teamName)
            _addTeamStatus.postValue(Pair(true, teamsMap.size))
        }
    }

    fun saveTeams() {
        Log.d(TAG, "saveTeams() == teamsMap: $teamsMap")
        CoroutineScope(Dispatchers.IO).launch {
            val status = teamsRepository.saveTeams(teamsList = ArrayList(teamsMap.values))
            _saveStatus.postValue(status)
        }
    }

    fun clear() {
        Log.d(TAG, "clear()")
        CoroutineScope(Dispatchers.IO).launch {
            val status = teamsRepository.clear()
            _resetStatus.postValue(status)
        }
    }
}