package com.decathlon.iplknockout.data.api

import androidx.room.*
import com.decathlon.iplknockout.data.model.TeamEntity

@Dao
interface TeamDao {
    @Insert
    suspend fun insert(teamEntity: TeamEntity) : Long

    @Update
    suspend fun update(teamEntity: TeamEntity) : Int

    @Delete
    suspend fun delete(teamEntity: TeamEntity) : Int

    @Query("DELETE FROM ${TeamEntity.TABLE_NAME}")
    suspend fun deleteAll() : Int

    @Transaction
    @Query("SELECT * FROM ${TeamEntity.TABLE_NAME}")
    suspend fun getAllTeams(): List<TeamEntity>?

    @Transaction
    @Query("SELECT * FROM ${TeamEntity.TABLE_NAME} WHERE ${TeamEntity._ID}= :id")
    suspend fun getTeamById(id : Int): TeamEntity?

    @Transaction
    @Query("SELECT * FROM ${TeamEntity.TABLE_NAME} WHERE ${TeamEntity.NAME}= :name")
    suspend fun getTeamByName(name : String): TeamEntity?

    @Transaction
    @Query("SELECT * FROM ${TeamEntity.TABLE_NAME} WHERE ${TeamEntity.WIN_LEVEL}= :level")
    suspend fun getTeamByWinLevel(level : Int): List<TeamEntity>?
}