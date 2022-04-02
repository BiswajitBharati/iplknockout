package com.decathlon.iplknockout.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.decathlon.iplknockout.data.model.PairTeams
import com.decathlon.iplknockout.data.model.TeamEntity
import com.decathlon.iplknockout.databinding.RowTeamsBinding

class TeamsAdapter(private var teamsList: ArrayList<TeamEntity>?,
                   private var pairTeamsList: ArrayList<PairTeams>?) : RecyclerView.Adapter<TeamsAdapter.TeamsViewHolder>() {
    companion object {
        private val TAG = TeamsAdapter::class.java.name
    }

    fun setTeamsList(teamsList: ArrayList<TeamEntity>?) {
        this.teamsList = teamsList
    }

    fun getTeamsList() = teamsList

    fun setPairTeamsList(pairTeamsList: ArrayList<PairTeams>?) {
        this.pairTeamsList = pairTeamsList
    }

    fun getPairTeamsList() = pairTeamsList

    class TeamsViewHolder(private val rowTeamsBinding: RowTeamsBinding) : RecyclerView.ViewHolder (rowTeamsBinding.root) {
        fun bind(teamEntity: TeamEntity?, pairTeams: PairTeams?, isLastRow: Boolean) {
            Log.d(TAG, "bind() == teamEntity: $teamEntity, pairTeams: $pairTeams, isLastRow: $isLastRow")
            teamEntity?.let {
                rowTeamsBinding.firstTeam.text = it.name
            }
            pairTeams?.let {
                rowTeamsBinding.firstTeam.text = it.first?.name
                rowTeamsBinding.secondTeam.text = it.second?.name
                rowTeamsBinding.secondTeam.visibility = View.VISIBLE
                rowTeamsBinding.firstVsSecond.visibility = View.VISIBLE
            }
            rowTeamsBinding.lastView.visibility = if (isLastRow) View.VISIBLE else View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamsViewHolder {
        Log.d(TAG, "onCreateViewHolder()")
        val binding = RowTeamsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TeamsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TeamsViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder()")
        holder.bind(teamsList?.get(position), pairTeamsList?.get(position), itemCount - 1 == position)
    }

    override fun getItemCount() = teamsList?.size ?: pairTeamsList?.size ?: 0
}