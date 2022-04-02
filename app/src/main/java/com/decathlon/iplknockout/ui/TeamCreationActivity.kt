package com.decathlon.iplknockout.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.decathlon.iplknockout.R
import com.decathlon.iplknockout.databinding.ActivityTeamCreationBinding

class TeamCreationActivity : AppCompatActivity() {
    companion object {
        private val TAG = TeamCreationActivity::class.java.name
    }

    private lateinit var binding: ActivityTeamCreationBinding
    private var viewModel: TeamCreationActivityViewModel? = null

    private var nextValidTeamsSize = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate()")

        binding = ActivityTeamCreationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBindings()
        initViewModels()
        initObservers()

        viewModel?.clear()
    }

    private fun initBindings() {
        Log.d(TAG, "initBindings()")
        supportActionBar?.hide()
        binding.addOwn.setOnClickListener {
            Log.d(TAG, "initBindings() == Action Add Own")
            val teamName = binding.createTeamEdittext.text.toString()
            binding.createTeamEdittext.setText("")
            if (teamName.trim().isEmpty()) {
                Toast.makeText(this, getString(R.string.error_1), Toast.LENGTH_SHORT).show()
            } else {
                viewModel?.addNewTeam(teamName)
            }
        }

        binding.continueOwn.setOnClickListener {
            Log.d(TAG, "initBindings() == Action Continue Own")
            val teamCount = binding.teamCounter.text.toString().toInt()
            if (teamCount == 0 || teamCount.mod(2) != 0) {
                Toast.makeText(this, getString(R.string.error_2), Toast.LENGTH_SHORT).show()
            } else {
                viewModel?.saveTeams()
            }
        }

        binding.continueDefault.setOnClickListener {
            Log.d(TAG, "initBindings() == Action Continue Default")
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("Is_Default_Teams", true)
            startActivity(intent)
            finish()
        }
    }

    private fun initViewModels() {
        Log.d(TAG, "initViewModels()")
        if (null == viewModel) {
            viewModel = ViewModelProvider(this, ViewModelFactory(application))[TeamCreationActivityViewModel::class.java]
        }
    }

    private fun initObservers() {
        Log.d(TAG, "initObservers() == viewModel: $viewModel")
        viewModel?.addTeamStatus?.observe(this, this::onAddTeamStatus)
        viewModel?.saveStatus?.observe(this, this::onTeamsSaveStatus)
        viewModel?.resetStatus?.observe(this, this::onTeamsResetStatus)
    }

    private fun onAddTeamStatus(status: Pair<Boolean, Int>) {
        Log.d(TAG, "onAddTeamStatus() == status: $status, nextValidTeamsSize: $nextValidTeamsSize")
        if (status.first) {
            binding.teamCounter.text = status.second.toString()
            if (status.second == nextValidTeamsSize) {
                nextValidTeamsSize *= 2
                binding.continueOwn.alpha = 1.0f
                binding.continueOwn.isEnabled = true
            } else {
                binding.continueOwn.alpha = 0.5f
                binding.continueOwn.isEnabled = false
            }
            Toast.makeText(this, getString(R.string.success_1), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, getString(R.string.error_3), Toast.LENGTH_SHORT).show()
        }
    }

    private fun onTeamsSaveStatus(status: Boolean) {
        Log.d(TAG, "onTeamsSaveStatus() == status: $status")

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("Is_Default_Teams", false)
        startActivity(intent)
        finish()
    }

    private fun onTeamsResetStatus(status: Boolean) {
        Log.d(TAG, "onTeamsResetStatus() == status: $status")
        binding.addOwn.alpha = 1.0f
        binding.addOwn.isEnabled = true

        binding.continueDefault.alpha = 1.0f
        binding.continueDefault.isEnabled = true
    }
}