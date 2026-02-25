package com.sudani.bat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.room.Room
import com.sudani.bat.data.local.AppDatabase
import com.sudani.bat.data.remote.SudaniApi
import com.sudani.bat.data.repository.SudaniRepository
import com.sudani.bat.ui.screens.HomeScreen
import com.sudani.bat.ui.theme.SudaniBatTheme
import com.sudani.bat.ui.viewmodel.MainViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "sudani-bat-db"
        ).build()
        
        val retrofit = Retrofit.Builder()
            .baseUrl("https://mapp.sudani.sd/prod")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            
        val api = retrofit.create(SudaniApi::class.java)
        val repository = SudaniRepository(api, db.numberDao())
        val viewModel = MainViewModel(repository)

        setContent {
            SudaniBatTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen(viewModel)
                }
            }
        }
    }
}
