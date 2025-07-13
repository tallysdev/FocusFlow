package com.example.focusflow.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.focusflow.ui.screens.trophies.Trophy
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrophiesRepository
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("trophies_prefs", Context.MODE_PRIVATE)

        private val TROPHIESKEY = "trophies_list"

        suspend fun saveTrophies(trophies: List<Trophy>) {
            val jsonArray = JSONArray()
            trophies.forEach { trophy ->
                val trophyJson =
                    JSONObject().apply {
                        put("id", trophy.id)
                        put("name", trophy.name)
                        put("description", trophy.description)
                        put("dateAchieved", trophy.dateAchieved ?: JSONObject.NULL)
                        put("imageUrl", trophy.imageUrl)
                    }
                jsonArray.put(trophyJson)
            }

            sharedPreferences.edit { putString(TROPHIESKEY, jsonArray.toString()) }
        }

        suspend fun getTrophies(): List<Trophy> {
            val trophiesJson = sharedPreferences.getString(TROPHIESKEY, null) ?: return emptyList()

            val trophiesList = mutableListOf<Trophy>()
            val jsonArray = JSONArray(trophiesJson)

            for (i in 0 until jsonArray.length()) {
                val trophyJson = jsonArray.getJSONObject(i)
                val dateAchieved =
                    if (trophyJson.isNull("dateAchieved")) {
                        null
                    } else {
                        trophyJson.getString("dateAchieved")
                    }

                trophiesList.add(
                    Trophy(
                        id = trophyJson.getInt("id"),
                        name = trophyJson.getString("name"),
                        description = trophyJson.getString("description"),
                        dateAchieved = dateAchieved,
                        imageUrl = trophyJson.getInt("imageUrl"),
                    ),
                )
            }

            return trophiesList
        }
    }
