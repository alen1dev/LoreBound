package com.pauls.lorebound

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.pauls.lorebound.data.seed.DatabaseSeeder
import com.pauls.lorebound.domain.chronicle.ChronicleSeasonManager
import com.pauls.lorebound.domain.chronicle.ChronicleStarsPreference
import com.pauls.lorebound.domain.repository.CharacterRepository
import com.pauls.lorebound.navigation.LoreBoundNavGraph
import com.pauls.lorebound.navigation.Screen
import com.pauls.lorebound.ui.theme.LoreBoundTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var databaseSeeder: DatabaseSeeder

    @Inject
    lateinit var characterRepository: CharacterRepository

    @Inject
    lateinit var chronicleSeasonManager: ChronicleSeasonManager

    @Inject
    lateinit var chronicleStarsPreference: ChronicleStarsPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoreBoundTheme {
                val navController = rememberNavController()
                var startDestination by remember { mutableStateOf<String?>(null) }

                LaunchedEffect(Unit) {
                    databaseSeeder.seedIfEmpty()
                    val hasCharacter = characterRepository.characterExists()
                    startDestination = if (hasCharacter) {
                        Screen.Home.route
                    } else {
                        Screen.CharacterCreation.route
                    }
                }

                startDestination?.let { destination ->
                    LoreBoundNavGraph(
                        navController = navController,
                        startDestination = destination,
                        chronicleSeasonManager = chronicleSeasonManager,
                        chronicleStarsPreference = chronicleStarsPreference
                    )
                }
            }
        }
    }
}