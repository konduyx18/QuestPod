package com.questpod.presentation.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.questpod.presentation.theme.QuestPodTheme

/**
 * Data class to hold user preferences collected during onboarding
 */
data class UserPreferences(
    val selectedGenres: List<String> = emptyList(),
    val readingLevel: Float = 0.5f,
    val themePreference: String = "System Default",
    val voiceType: String = "Neutral"
)

/**
 * Main onboarding screen that manages the multi-step flow
 */
@Composable
fun OnboardingScreen(
    onComplete: (UserPreferences) -> Unit = {},
    hasCompletedOnboarding: Boolean = false,
    onSkip: () -> Unit = {}
) {
    val preferences = remember { mutableStateOf(UserPreferences()) }
    var currentStep by remember { mutableStateOf(0) }
    val totalSteps = 4
    
    // Show skip dialog if user has completed onboarding before
    var showSkipDialog by remember { mutableStateOf(false) }
    
    // Check if user has completed onboarding before and show dialog
    LaunchedEffect(Unit) {
        if (hasCompletedOnboarding) {
            showSkipDialog = true
        }
    }
    
    // Skip dialog
    if (showSkipDialog) {
        AlertDialog(
            onDismissRequest = { showSkipDialog = false },
            title = { Text("Skip Onboarding?") },
            text = { Text("You've already completed onboarding. Would you like to skip it this time?") },
            confirmButton = {
                Button(
                    onClick = {
                        showSkipDialog = false
                        onSkip()
                    }
                ) {
                    Text("Skip")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showSkipDialog = false }
                ) {
                    Text("Continue Onboarding")
                }
            }
        )
    }
    
    Scaffold(
        bottomBar = {
            OnboardingBottomBar(
                currentStep = currentStep,
                totalSteps = totalSteps,
                onPreviousClick = { if (currentStep > 0) currentStep-- },
                onNextClick = { 
                    if (currentStep < totalSteps - 1) {
                        currentStep++
                    } else {
                        onComplete(preferences.value)
                    }
                },
                isLastStep = currentStep == totalSteps - 1,
                showSkip = hasCompletedOnboarding,
                onSkip = onSkip
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Progress indicator
            LinearProgressIndicator(
                progress = { (currentStep + 1).toFloat() / totalSteps },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            
            // Content based on current step
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    val direction = if (targetState > initialState) 1 else -1
                    slideInHorizontally(
                        animationSpec = tween(300),
                        initialOffsetX = { fullWidth -> direction * fullWidth }
                    ) + fadeIn(animationSpec = tween(300)) togetherWith
                    slideOutHorizontally(
                        animationSpec = tween(300),
                        targetOffsetX = { fullWidth -> -direction * fullWidth }
                    ) + fadeOut(animationSpec = tween(300))
                },
                modifier = Modifier.weight(1f)
            ) { step ->
                when (step) {
                    0 -> WelcomeScreen()
                    1 -> GenreSelectionScreen(
                        selectedGenres = preferences.value.selectedGenres,
                        onGenresSelected = { newGenres ->
                            preferences.value = preferences.value.copy(selectedGenres = newGenres)
                        }
                    )
                    2 -> ReadingLevelScreen(
                        readingLevel = preferences.value.readingLevel,
                        onReadingLevelChanged = { level ->
                            preferences.value = preferences.value.copy(readingLevel = level)
                        }
                    )
                    3 -> PreferencesScreen(
                        themePreference = preferences.value.themePreference,
                        voiceType = preferences.value.voiceType,
                        onThemePreferenceChanged = { theme ->
                            preferences.value = preferences.value.copy(themePreference = theme)
                        },
                        onVoiceTypeChanged = { voice ->
                            preferences.value = preferences.value.copy(voiceType = voice)
                        }
                    )
                }
            }
        }
    }
}

/**
 * Welcome screen - first step in onboarding
 */
@Composable
fun WelcomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App logo placeholder
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "QP",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Welcome to QuestPod",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Your gateway to interactive storytelling",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 4.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "What is QuestPod?",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "QuestPod is an interactive storytelling platform where your choices shape the narrative. Experience adventures across genres, create your own stories, and share them with others.",
                    style = MaterialTheme.typography.bodyLarge
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Key Features:",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                FeatureItem(text = "Interactive stories that respond to your choices")
                FeatureItem(text = "Create and share your own branching narratives")
                FeatureItem(text = "Voice narration with customizable voices")
                FeatureItem(text = "Personalized recommendations based on your preferences")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Let's set up your preferences to personalize your experience",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Feature item with bullet point
 */
@Composable
fun FeatureItem(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/**
 * Genre selection screen - second step in onboarding
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GenreSelectionScreen(
    selectedGenres: List<String>,
    onGenresSelected: (List<String>) -> Unit
) {
    val genres = listOf(
        "Fantasy", "Science Fiction", "Mystery", "Horror", 
        "Romance", "Adventure", "Historical", "Thriller",
        "Comedy", "Drama", "Dystopian", "Fairy Tale"
    )
    
    val selectedGenresList = remember { mutableStateListOf<String>().apply { addAll(selectedGenres) } }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Select Your Favorite Genres",
            style = MaterialTheme.typography.headlineSmall
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Choose the story genres you enjoy most. This helps us recommend stories you'll love.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            genres.forEach { genre ->
                val isSelected = selectedGenresList.contains(genre)
                
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        if (isSelected) {
                            selectedGenresList.remove(genre)
                        } else {
                            selectedGenresList.add(genre)
                        }
                        onGenresSelected(selectedGenresList.toList())
                    },
                    label = { Text(genre) },
                    leadingIcon = if (isSelected) {
                        {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else null
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (selectedGenresList.isNotEmpty()) {
            Text(
                text = "Your selected genres: ${selectedGenresList.joinToString(", ")}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            Text(
                text = "Please select at least one genre to continue",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

/**
 * Reading level screen - third step in onboarding
 */
@Composable
fun ReadingLevelScreen(
    readingLevel: Float,
    onReadingLevelChanged: (Float) -> Unit
) {
    var sliderPosition by remember { mutableFloatStateOf(readingLevel) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Reading Level Preference",
            style = MaterialTheme.typography.headlineSmall
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Select your preferred reading complexity. This helps us tailor stories to your reading comfort level.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Text(
            text = getReadingLevelDescription(sliderPosition),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = getReadingLevelDetail(sliderPosition),
            style = MaterialTheme.typography.bodyMedium
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Slider(
            value = sliderPosition,
            onValueChange = { 
                sliderPosition = it
                onReadingLevelChanged(it)
            },
            steps = 4,
            valueRange = 0f..1f
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Simple",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Advanced",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Example text
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Example Text:",
                    style = MaterialTheme.typography.titleSmall
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = getExampleText(sliderPosition),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

/**
 * Get reading level description based on slider position
 */
private fun getReadingLevelDescription(position: Float): String {
    return when {
        position < 0.2f -> "Basic"
        position < 0.4f -> "Easy"
        position < 0.6f -> "Intermediate"
        position < 0.8f -> "Advanced"
        else -> "Expert"
    }
}

/**
 * Get reading level detail based on slider position
 */
private fun getReadingLevelDetail(position: Float): String {
    return when {
        position < 0.2f -> "Simple vocabulary and sentence structure. Suitable for young readers or beginners."
        position < 0.4f -> "Straightforward language with occasional complexity. Good for casual reading."
        position < 0.6f -> "Balanced complexity with moderate vocabulary. Suitable for most readers."
        position < 0.8f -> "Rich vocabulary and varied sentence structures. For experienced readers."
        else -> "Sophisticated language with complex themes. For avid readers seeking challenge."
    }
}

/**
 * Get example text based on reading level
 */
private fun getExampleText(position: Float): String {
    return when {
        position < 0.2f -> "The door opened. A tall man walked in. He had a map in his hand. \"I need help,\" he said."
        position < 0.4f -> "The wooden door creaked open, and a tall man stepped into the room. He was holding an old map that looked important. \"I need your help with something unusual,\" he said with concern in his voice."
        position < 0.6f -> "The ancient oak door groaned on its hinges as a towering figure entered the dimly lit room. Clutched in his weathered hands was a parchment map, its edges frayed from years of handling. \"I require your assistance with a matter of some delicacy,\" he announced, his voice resonating with quiet urgency."
        position < 0.8f -> "The centuries-old oak door protested with a sonorous groan as a statuesque individual breached the threshold of the chamber, illuminated only by the dying embers of the hearth. Clasped between his calloused fingers was a cartographic relic, its periphery disintegrating from decades of scrutiny. \"I find myself in need of your expertise regarding a situation of considerable sensitivity,\" he proclaimed, his baritone voice carrying the weight of unspoken consequence."
        else -> "The antediluvian portal of gnarled oak issued forth a lamentable dirge as it yielded passage to a figure of imposing verticality, who transitioned into the penumbral chamber where illumination emanated solely from the moribund remnants of combustion within the hearth. Ensconced within his desiccated digits was a palimpsest of geographical delineation, its perimeter succumbing to the inexorable ravages of temporal progression. \"I am compelled to solicit your erudition concerning a predicament of extraordinary sensitivity and potentially cataclysmic ramifications,\" he articulated, his profound vocalization resonating with the gravitas of unspoken but imminent peril."
    }
}

/**
 * Preferences screen - fourth step in onboarding
 */
@Composable
fun PreferencesScreen(
    themePreference: String,
    voiceType: String,
    onThemePreferenceChanged: (String) -> Unit,
    onVoiceTypeChanged: (String) -> Unit
) {
    val themeOptions = listOf("Light", "Dark", "System Default")
    val voiceOptions = listOf("Masculine", "Feminine", "Neutral")
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "App Preferences",
            style = MaterialTheme.typography.headlineSmall
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Customize your app experience with these final settings.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Theme preferences
        Text(
            text = "Theme Preference",
            style = MaterialTheme.typography.titleMedium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            themeOptions.forEach { theme ->
                val isSelected = theme == themePreference
                
                FilterChip(
                    selected = isSelected,
                    onClick = { onThemePreferenceChanged(theme) },
                    label = { Text(theme) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Voice type preferences
        Text(
            text = "Voice Narration Preference",
            style = MaterialTheme.typography.titleMedium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Select your preferred voice type for story narration.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Column(
            modifier = Modifier.selectableGroup()
        ) {
            voiceOptions.forEach { voice ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .selectable(
                            selected = (voice == voiceType),
                            onClick = { onVoiceTypeChanged(voice) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (voice == voiceType),
                        onClick = null // null because we're handling the click on the row
                    )
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Text(
                        text = voice,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "You're all set! Click 'Get Started' to begin your adventure.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Bottom navigation bar for onboarding flow
 */
@Composable
fun OnboardingBottomBar(
    currentStep: Int,
    totalSteps: Int,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    isLastStep: Boolean,
    showSkip: Boolean = false,
    onSkip: () -> Unit = {}
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Column {
            // Skip button for returning users
            if (showSkip) {
                TextButton(
                    onClick = onSkip,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(end = 16.dp, top = 8.dp)
                ) {
                    Text("Skip Onboarding")
                }
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
            if (currentStep > 0) {
                OutlinedButton(
                    onClick = onPreviousClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Previous"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Back")
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
            
            // Step indicators
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                for (i in 0 until totalSteps) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (i == currentStep) 12.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (i == currentStep) 
                                    MaterialTheme.colorScheme.primary
                                else if (i < currentStep)
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                else
                                    MaterialTheme.colorScheme.surfaceVariant
                            )
                    )
                }
            }
            
            Button(
                onClick = onNextClick,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isLastStep) 
                        MaterialTheme.colorScheme.primary
                    else 
                        MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(if (isLastStep) "Get Started" else "Next")
                if (!isLastStep) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Next"
                    )
                }
            }
        }
    }
}

/**
 * Preview for the onboarding screen
 */
@Composable
fun OnboardingScreenPreview() {
    QuestPodTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            OnboardingScreen()
        }
    }
}
