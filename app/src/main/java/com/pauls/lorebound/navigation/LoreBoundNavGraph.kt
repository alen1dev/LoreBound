package com.pauls.lorebound.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.pauls.lorebound.domain.chronicle.ChronicleSeasonManager
import com.pauls.lorebound.domain.chronicle.ChronicleStarsPreference
import com.pauls.lorebound.domain.chronicle.ChronicleAvailabilityState
import com.pauls.lorebound.ui.character.CharacterCreationScreen
import com.pauls.lorebound.ui.charactersheet.CharacterSheetScreen
import com.pauls.lorebound.ui.chronicle.ChronicleExperienceScreen
import com.pauls.lorebound.ui.components.StarFieldBackground
import com.pauls.lorebound.ui.developer.DeveloperMenuScreen
import com.pauls.lorebound.ui.home.HomeScreen
import com.pauls.lorebound.ui.lore.LoreEntryDetailScreen
import com.pauls.lorebound.ui.lore.LoreJournalScreen
import com.pauls.lorebound.ui.lore.CreatePersonalLoreScreen
import com.pauls.lorebound.ui.quest.QuestDetailScreen
import com.pauls.lorebound.ui.showcase.StyleShowcaseScreen
import com.pauls.lorebound.ui.welcome.WelcomeSplashScreen
import com.pauls.lorebound.AppConfig
import com.pauls.lorebound.ui.theme.BrutalAccent
import com.pauls.lorebound.ui.theme.BrutalBlack
import com.pauls.lorebound.ui.theme.BrutalMuted
import com.pauls.lorebound.ui.theme.BrutalSurfaceVariant

private data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
)

private val bottomNavItems = buildList {
    add(BottomNavItem(Screen.Home, "Quests", Icons.Rounded.Explore))
    add(BottomNavItem(Screen.LoreJournal, "Lore", Icons.Rounded.AutoStories))
    add(BottomNavItem(Screen.CharacterSheet, "Character", Icons.Rounded.Person))
    add(BottomNavItem(Screen.StyleShowcase, "Settings", Icons.Rounded.Tune))
}

private val bottomNavRoutes = bottomNavItems.map { it.screen.route }.toSet()

@Composable
fun LoreBoundNavGraph(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
    chronicleSeasonManager: ChronicleSeasonManager? = null,
    chronicleStarsPreference: ChronicleStarsPreference? = null
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in bottomNavRoutes && currentRoute != Screen.ChronicleExperience.route

    // Determine current tab index for star rotation
    val currentTabIndex = remember(currentRoute) {
        bottomNavItems.indexOfFirst { it.screen.route == currentRoute }.coerceAtLeast(0).toFloat()
    }
    val animatedTabProgress by animateFloatAsState(
        targetValue = currentTabIndex,
        animationSpec = tween(600),
        label = "tabStarRotation"
    )

    // Check if seasonal stars should show
    val showSeasonalStars = remember(currentRoute) {
        if (chronicleSeasonManager == null || chronicleStarsPreference == null) return@remember false
        val state = chronicleSeasonManager.getCurrentState()
        if (state is ChronicleAvailabilityState.Ready) {
            chronicleStarsPreference.hasViewedChronicle(state.year)
        } else false
    }
    // Don't show on chronicle screen itself (it has its own)
    val starsVisible = showSeasonalStars && currentRoute != Screen.ChronicleExperience.route

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Fade gradient above nav bar
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        BrutalBlack.copy(alpha = 0.95f)
                                    )
                                )
                            )
                    )
                    // Floating nav pill
                    NavigationBar(
                        modifier = Modifier
                            .height(56.dp)
                            .clip(RoundedCornerShape(28.dp)),
                        containerColor = BrutalSurfaceVariant,
                        tonalElevation = 0.dp,
                        windowInsets = WindowInsets(0, 0, 0, 0)
                    ) {
                        bottomNavItems.forEach { item ->
                            NavigationBarItem(
                                selected = currentRoute == item.screen.route,
                                onClick = {
                                    if (currentRoute != item.screen.route) {
                                        navController.navigate(item.screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label
                                    )
                                },
                                label = null,
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    unselectedIconColor = BrutalMuted,
                                    indicatorColor = BrutalAccent.copy(alpha = 0.15f)
                                )
                            )
                        }
                    }
                    // Fade gradient below nav bar + respect system nav insets
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.navigationBars)
                            .height(8.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        BrutalBlack.copy(alpha = 0.95f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
            enterTransition = { fadeIn(tween(300)) + scaleIn(tween(300), initialScale = 0.96f) },
            exitTransition = { fadeOut(tween(200)) },
            popEnterTransition = { fadeIn(tween(300)) + scaleIn(tween(300), initialScale = 0.96f) },
            popExitTransition = { fadeOut(tween(200)) }
        ) {
            composable(Screen.CharacterCreation.route) {
                CharacterCreationScreen(
                    onCharacterCreated = {
                        navController.navigate(Screen.WelcomeSplash.route) {
                            popUpTo(Screen.CharacterCreation.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                Screen.WelcomeSplash.route,
                enterTransition = { fadeIn(tween(800)) },
                exitTransition = { fadeOut(tween(600)) }
            ) {
                WelcomeSplashScreen(
                    onContinue = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.WelcomeSplash.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    onQuestClick = { dailyQuestId ->
                        navController.navigate(Screen.QuestDetail.createRoute(dailyQuestId))
                    },
                    onViewChronicle = {
                        navController.navigate(Screen.ChronicleExperience.route)
                    }
                )
            }

            composable(
                route = Screen.QuestDetail.route,
                arguments = listOf(navArgument("questId") { type = NavType.LongType }),
                enterTransition = { slideInHorizontally(tween(350)) { it / 3 } + fadeIn(tween(350)) },
                exitTransition = { fadeOut(tween(200)) },
                popExitTransition = { slideOutHorizontally(tween(300)) { it / 3 } + fadeOut(tween(300)) }
            ) {
                QuestDetailScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.LoreJournal.route) {
                LoreJournalScreen(
                    onEntryClick = { entryId ->
                        navController.navigate(Screen.LoreEntryDetail.createRoute(entryId))
                    },
                    onCreatePersonalLore = {
                        navController.navigate(Screen.CreatePersonalLore.route)
                    }
                )
            }

            composable(
                Screen.CreatePersonalLore.route,
                enterTransition = { slideInHorizontally(tween(350)) { it / 3 } + fadeIn(tween(350)) },
                popExitTransition = { slideOutHorizontally(tween(300)) { it / 3 } + fadeOut(tween(300)) }
            ) {
                CreatePersonalLoreScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.LoreEntryDetail.route,
                arguments = listOf(navArgument("entryId") { type = NavType.LongType }),
                enterTransition = { slideInHorizontally(tween(350)) { it / 3 } + fadeIn(tween(350)) },
                popExitTransition = { slideOutHorizontally(tween(300)) { it / 3 } + fadeOut(tween(300)) }
            ) {
                LoreEntryDetailScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.CharacterSheet.route) {
                CharacterSheetScreen()
            }

            composable(Screen.Titles.route) {
                PlaceholderScreen("Titles")
            }

            composable(Screen.Feats.route) {
                PlaceholderScreen("Feats")
            }

            composable(Screen.Settings.route) {
                PlaceholderScreen("Settings")
            }

            composable(Screen.StyleShowcase.route) {
                StyleShowcaseScreen(
                    onNavigateToDeveloperMenu = {
                        navController.navigate(Screen.DeveloperMenu.route)
                    },
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.DeveloperMenu.route) {
                DeveloperMenuScreen(
                    onBack = { navController.popBackStack() },
                    onFreshStart = {
                        navController.navigate(Screen.CharacterCreation.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onRestartHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                Screen.ChronicleExperience.route,
                enterTransition = { fadeIn(tween(800)) },
                exitTransition = { fadeOut(tween(400)) },
                popExitTransition = { fadeOut(tween(400)) }
            ) {
                // Mark chronicle as viewed for seasonal stars
                val seasonState = chronicleSeasonManager?.getCurrentState()
                if (seasonState is ChronicleAvailabilityState.Ready) {
                    chronicleStarsPreference?.markChronicleViewed(seasonState.year)
                }

                val homeViewModel: com.pauls.lorebound.ui.home.HomeViewModel =
                    androidx.hilt.navigation.compose.hiltViewModel()
                val homeState by homeViewModel.state.collectAsStateWithLifecycle()
                val chronicle = homeState.chronicleReady
                if (chronicle != null) {
                    ChronicleExperienceScreen(
                        chronicle = chronicle,
                        onDismiss = { navController.popBackStack() }
                    )
                } else {
                    // Fallback: no chronicle available
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No Chronicle available yet.\nGenerate one from Developer Menu.",
                            color = BrutalMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
            // Seasonal star field overlay (Dec 15 - Jan 15, after viewing chronicle)
            // Rendered on top — Canvas doesn't intercept touch
            if (starsVisible) {
                StarFieldBackground(
                    scrollProgress = animatedTabProgress,
                    starCount = 50,
                    rotationPerUnit = 20f
                )
            }
        } // end Box
    }
}

@Composable
private fun PlaceholderScreen(name: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Text(text = name, style = MaterialTheme.typography.headlineMedium)
    }
}

