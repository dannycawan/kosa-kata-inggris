package com.kosakata.inggris

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.kosakata.inggris.ads.InterstitialAdManager
import com.kosakata.inggris.audio.TtsManager
import com.kosakata.inggris.data.local.AppDatabase
import com.kosakata.inggris.data.preferences.UserPreferences
import com.kosakata.inggris.data.repository.VocabRepository
import com.kosakata.inggris.navigation.VocabNavigation
import com.kosakata.inggris.ui.VocabViewModel
import com.kosakata.inggris.ui.VocabViewModelFactory
import com.kosakata.inggris.ui.theme.VocabTheme

class MainActivity : ComponentActivity() {
    private lateinit var ttsManager: TtsManager
    private lateinit var interstitialAdManager: InterstitialAdManager

    private val viewModel: VocabViewModel by viewModels {
        val database = AppDatabase.getInstance(applicationContext)
        val repository = VocabRepository(
            database = database,
            preferences = UserPreferences(applicationContext),
            context = applicationContext
        )
        VocabViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ttsManager = TtsManager(applicationContext)
        interstitialAdManager = InterstitialAdManager(this).also { it.load() }

        setContent {
            VocabTheme {
                VocabNavigation(
                    viewModel = viewModel,
                    ttsManager = ttsManager,
                    interstitialAdManager = interstitialAdManager
                )
            }
        }
    }

    override fun onDestroy() {
        ttsManager.shutdown()
        super.onDestroy()
    }
}
