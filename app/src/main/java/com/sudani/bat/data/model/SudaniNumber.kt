package com.sudani.bat.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "numbers")
data class SudaniNumber(
    @PrimaryKey val msisdn: String,
    val name: String,
    val token: String,
    val userDataJson: String,
    val lastClaimAt: String? = null,
    val lastPointsGained: Int = 0,
    val totalPoints: Int = 0,
    val balance: String = "0",
    val lastUpdate: Long = System.currentTimeMillis()
)
