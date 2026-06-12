package com.kosakata.inggris.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kosakata.inggris.ads.InterstitialAdManager
import com.kosakata.inggris.audio.TtsManager
import com.kosakata.inggris.ui.VocabViewModel
import com.kosakata.inggris.ui.screens.BookmarkScreen
import com.kosakata.inggris.ui.screens.CategoryScreen
import com.kosakata.inggris.ui.screens.HomeScreen
import com.kosakata.inggris.ui.screens.LearningSessionScreen
import com.kosakata.inggris.ui.screens.ListeningScreen
import com.kosakata.inggris.ui.screens.OnboardingScreen
import com.kosakata.inggris.ui.screens.ProfileScreen
import com.kosakata.inggris.ui.screens.QuizResultScreen
import com.kosakata.inggris.ui.screens.QuizScreen
import com.kosakata.inggris.ui.screens.ReviewScreen
import com.kosakata.inggris.ui.screens.SettingsScreen
import kotlinx.coroutines.flow.first

private data class Tab(val route: String, val label: String, val icon: ImageVector)

@Composable
fun VocabNavigation(
    viewModel: VocabViewModel,
    ttsManager: TtsManager,
    interstitialAdManager: InterstitialAdManager
) {
    val repository = viewModel.repository
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val mainRoutes = setOf("home", "category", "review", "profile")
    val tabs = listOf(
        Tab("home", "Beranda", Icons.Default.Home),
        Tab("category", "Belajar", Icons.Default.PlayArrow),
        Tab("review", "Review", Icons.Default.Refresh),
        Tab("profile", "Profil", Icons.Default.Person)
    )

    Scaffold(
        bottomBar = {
            if (currentRoute != null && currentRoute in mainRoutes) {
                NavigationBar {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = currentRoute == tab.route,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo("home") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "gate",
            modifier = Modifier.padding(padding)
        ) {
            composable("gate") {
                val firstOpenDone by produceState<Boolean?>(initialValue = null) {
                    value = repository.preferences.firstOpenDone.first()
                }
                LaunchedEffect(firstOpenDone) {
                    val destination = firstOpenDone ?: return@LaunchedEffect
                    navController.navigate(if (destination) "home" else "onboarding") {
                        popUpTo("gate") { inclusive = true }
                    }
                }
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            composable("onboarding") {
                OnboardingScreen(
                    repository = repository,
                    onDone = {
                        navController.navigate("home") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }
                )
            }
            composable("home") {
                HomeScreen(
                    repository = repository,
                    onStartLearn = { navController.navigate("learn") },
                    onListen = { navController.navigate("listening") },
                    onReview = { navController.navigate("review") }
                )
            }
            composable("category") {
                CategoryScreen(repository) { navController.navigate("learn") }
            }
            composable("learn") {
                LearningSessionScreen(
                    repository = repository,
                    ttsManager = ttsManager,
                    onWordsLoaded = viewModel::setCurrentSession,
                    onOpenQuiz = {
                        interstitialAdManager.showIfReady {
                            navController.navigate("quiz")
                        }
                    }
                )
            }
            composable("listening") {
                ListeningScreen(repository, ttsManager)
            }
            composable("quiz") {
                val sessionWords by viewModel.currentSessionWords.collectAsState()
                QuizScreen(
                    repository = repository,
                    sessionWords = sessionWords,
                    onDone = { correct, wrong ->
                        viewModel.setQuizResult(correct, wrong)
                        navController.navigate("quiz-result") {
                            popUpTo("quiz") { inclusive = true }
                        }
                    }
                )
            }
            composable("quiz-result") {
                val result by viewModel.quizResult.collectAsState()
                QuizResultScreen(
                    correct = result.correct,
                    wrong = result.wrong,
                    onContinue = {
                        interstitialAdManager.showIfReady {
                            navController.navigate("learn") {
                                popUpTo("quiz-result") { inclusive = true }
                            }
                        }
                    },
                    onReview = {
                        interstitialAdManager.showIfReady {
                            navController.navigate("review") {
                                popUpTo("quiz-result") { inclusive = true }
                            }
                        }
                    },
                    onHome = {
                        interstitialAdManager.showIfReady {
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                            }
                        }
                    }
                )
            }
            composable("review") { ReviewScreen(repository, ttsManager) }
            composable("bookmark") { BookmarkScreen(repository, ttsManager) }
            composable("settings") { SettingsScreen(repository) }
            composable("profile") {
                ProfileScreen(
                    repository,
                    onBookmarks = { navController.navigate("bookmark") },
                    onSettings = { navController.navigate("settings") }
                )
            }
        }
    }
}
