package com.decathlon.iplknockout.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.decathlon.iplknockout.R
import com.decathlon.iplknockout.data.model.PairTeams
import com.decathlon.iplknockout.data.model.TeamEntity
import com.decathlon.iplknockout.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG = MainActivity::class.java.name
    }

    private lateinit var binding: ActivityMainBinding
    private var viewModel: MainActivityViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate()")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBindings()
        initViewModels()
        initObservers()

        val isDefaultTeams = intent.getBooleanExtra("Is_Default_Teams", true)
        Log.d(TAG, "onCreate() == isDefaultTeams: $isDefaultTeams")
        viewModel?.getTeams(isDefaultTeams)
    }

    private fun initBindings() {
        Log.d(TAG, "initBindings()")
        val linearLayoutManager = LinearLayoutManager(this)
        binding.teamsList.run {
            setHasFixedSize(true)
            layoutManager = linearLayoutManager
        }

        binding.actionButton.setOnClickListener {
            Log.d(TAG, "initBindings() == Action Button")
            val teamsList = (binding.teamsList.adapter as TeamsAdapter).getTeamsList()
            teamsList?.let { viewModel?.getPairedTeams(teamsList = it) }
            val pairTeamsList = (binding.teamsList.adapter as TeamsAdapter).getPairTeamsList()
            pairTeamsList?.let { viewModel?.getWinningTeams(pairTeamList = it) }

            if (null == teamsList && null == pairTeamsList) {
                Log.d(TAG, "initBindings() == Action Button Reset")
                viewModel?.reset()
            }
        }

        binding.ok.setOnClickListener {
            Log.d(TAG, "initBindings() == Ok Button")
            binding.thirdPositionNote.visibility = View.GONE
            binding.ok.visibility = View.GONE
            binding.ignore.visibility = View.GONE
            
            viewModel?.getTeamsByLevel(2)
        }

        binding.ignore.setOnClickListener {
            Log.d(TAG, "initBindings() == Ignore Button")
            binding.thirdPositionNote.visibility = View.GONE
            binding.ok.visibility = View.GONE
            binding.ignore.visibility = View.GONE
        }
    }

    private fun initViewModels() {
        Log.d(TAG, "initViewModels()")
        if (null == viewModel) {
            viewModel = ViewModelProvider(this, ViewModelFactory(application))[MainActivityViewModel::class.java]
        }
    }

    private fun initObservers() {
        Log.d(TAG, "initObservers() == viewModel: $viewModel")
        viewModel?.saveStatus?.observe(this, this::onTeamsSaveStatus)
        viewModel?.resetStatus?.observe(this, this::onTeamsResetStatus)
        viewModel?.teamsListByLevel?.observe(this, this::onLevelByTeamsList)
        viewModel?.defaultTeamsList?.observe(this, this::onDefaultTeamsList)
        viewModel?.teamsList?.observe(this, this::loadTeamsList)
        viewModel?.winningTeamsList?.observe(this, this::onWinningTeamsList)
        viewModel?.pairedTeamsList?.observe(this, this::loadPairedTeamsList)
    }

    private fun onTeamsSaveStatus(status: Boolean) {
        Log.d(TAG, "onTeamsSaveStatus() == status: $status")

        viewModel?.getTeams(false)
    }

    private fun onTeamsResetStatus(status: Boolean) {
        Log.d(TAG, "onTeamsResetStatus() == status: $status")

        binding.teamsList.visibility = View.VISIBLE
        binding.thirdPositionNote.visibility = View.VISIBLE
        binding.ok.visibility = View.VISIBLE
        binding.ignore.visibility = View.VISIBLE
        binding.finalListLayout.first_position_team.text = ""
        binding.finalListLayout.second_position_team.text = ""
        binding.finalListLayout.third_position_team.text = ""
        binding.finalListLayout.third_position_title.visibility = View.GONE
        binding.finalListLayout.third_position_team.visibility = View.GONE
        binding.logoMain.visibility = View.VISIBLE
        binding.logo.visibility = View.GONE
        binding.finalListLayout.visibility = View.GONE

        viewModel?.getTeams(false)
    }

    private fun onDefaultTeamsList(teamsList: ArrayList<TeamEntity>) {
        Log.d(TAG, "onDefaultTeamsList() == teamsList: $teamsList")

        viewModel?.saveTeams(teamsList)
    }

    private fun loadTeamsList(teamsList: ArrayList<TeamEntity>) {
        Log.d(TAG, "loadTeamsList() == teamsList: $teamsList, adapter: ${binding.teamsList.adapter}")

        if (null != binding.teamsList.adapter) { // Restart case.
            binding.teamsList.adapter = null
        }

        with(binding.teamsList) {
            adapter = TeamsAdapter(teamsList = teamsList, pairTeamsList = null)
        }
        binding.actionButton.text = getString(R.string.start_ipl)
    }

    private fun onLevelByTeamsList(teamsList: ArrayList<TeamEntity>) {
        Log.d(TAG, "onLevelByTeamsList() == teamsList: $teamsList")

        teamsList.forEach { item ->
            when (item.win_level) {
                1 -> binding.finalListLayout.second_position_team.text = item.name
                2 -> simulateFor3rdPosition(teamsList)
                else -> Log.d(TAG, "onLevelByTeamsList() == ${item.win_level} teams are ignored!")
            }
        }
    }

    private fun simulateFor3rdPosition(teamsList: ArrayList<TeamEntity>) {
        Log.d(TAG, "simulateFor3rdPosition() == teamsList: $teamsList")
        if (teamsList.size != 2) return
        val pairTeams = PairTeams(first = teamsList[0], second = teamsList[1])
        val pairTeamList: ArrayList<PairTeams> = ArrayList()
        pairTeamList.add(pairTeams)

        val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle(getString(R.string.simulate_for_third_position));
        builder.setMessage("${teamsList[0].name} \n \n ${getString(R.string.vs)} \n \n ${teamsList[1].name}")
        builder.setCancelable(false);

        builder.setPositiveButton(getString(R.string.simulate)) { dialog, which ->
            viewModel?.getWinningTeams(pairTeamList = pairTeamList)
        }

        builder.setNegativeButton(getString(R.string.cancel)) { dialog, which ->
            dialog.cancel()
        }

        val alertDialog = builder.create()
        alertDialog.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onWinningTeamsList(teamsList: ArrayList<TeamEntity>?) {
        Log.d(TAG, "onWinningTeamsList() == teamsList: $teamsList")

        teamsList?.let { it ->
            if (it.size % 2 == 0) {
                viewModel?.getPairedTeams(teamsList = it)
            } else if (it[0].win_level == 0) {
                (binding.teamsList.adapter as TeamsAdapter).setTeamsList(teamsList = null)
                (binding.teamsList.adapter as TeamsAdapter).setPairTeamsList(pairTeamsList = null)
                binding.teamsList.adapter?.notifyDataSetChanged()

                binding.actionButton.text = getString(R.string.restart)
                binding.logoMain.visibility = View.GONE
                binding.logo.visibility = View.VISIBLE
                binding.finalListLayout.visibility = View.VISIBLE
                binding.finalListLayout.first_position_team.text = it[0].name

                viewModel?.getTeamsByLevel(1)
            } else {
                binding.finalListLayout.third_position_title.visibility = View.VISIBLE
                binding.finalListLayout.third_position_team.visibility = View.VISIBLE
                binding.finalListLayout.third_position_team.text = it[0].name
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadPairedTeamsList(pairTeamList: ArrayList<PairTeams>?) {
        Log.d(TAG, "loadPairedTeamsList() == pairTeamList: $pairTeamList, adapter: ${binding.teamsList.adapter}")
        if (pairTeamList.isNullOrEmpty()) return

        (binding.teamsList.adapter as TeamsAdapter).setTeamsList(teamsList = null)
        (binding.teamsList.adapter as TeamsAdapter).setPairTeamsList(pairTeamsList = pairTeamList)
        if (null == binding.teamsList.adapter) {
            with(binding.teamsList) {
                adapter = TeamsAdapter(teamsList = null, pairTeamsList = pairTeamList)
            }
        } else {
            binding.teamsList.scrollToPosition(0)
            binding.teamsList.adapter?.notifyDataSetChanged()
        }
        if (pairTeamList.size == 1) binding.actionButton.text = getString(R.string.simulate_and_end)
        else binding.actionButton.text = getString(R.string.simulate)
    }
}