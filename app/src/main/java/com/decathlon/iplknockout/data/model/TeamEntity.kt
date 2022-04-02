package com.decathlon.iplknockout.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = TeamEntity.TABLE_NAME)
data class TeamEntity(
    @ColumnInfo(name = _ID)
    @PrimaryKey(autoGenerate = false)
    var id : Int = 0,

    @ColumnInfo(name = WIN_LEVEL)
    var win_level: Int = -1,

    @ColumnInfo(name = NAME)
    var name: String,

    @ColumnInfo(name = OWNER)
    var owner: String = "UnKnown",

    @ColumnInfo(name = COACH)
    var coach: String = "UnKnown"
) {
    companion object Contract {
        const val TABLE_NAME: String = "ipl_team"

        const val _ID: String = "_id"

        const val WIN_LEVEL: String = "win_level"

        const val NAME: String = "name"

        const val OWNER: String = "owner"

        const val COACH: String = "coach"
    }
}

data class PairTeams(
    var first: TeamEntity?,

    var second: TeamEntity?
)