package com.decathlon.iplknockout.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.decathlon.iplknockout.data.api.TeamsService
import com.decathlon.iplknockout.data.repository.TeamsRepository

class ViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            return MainActivityViewModel(TeamsRepository(TeamsService(application = application))) as T
        } else if (modelClass.isAssignableFrom(TeamCreationActivityViewModel::class.java)) {
            return TeamCreationActivityViewModel(TeamsRepository(TeamsService(application = application))) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}