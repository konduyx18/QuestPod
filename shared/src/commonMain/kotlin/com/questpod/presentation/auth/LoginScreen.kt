package com.questpod.presentation.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.questpod.presentation.theme.QuestPodTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Login screen state data class
 */
data class LoginScreenState(
    val email: String = "",
    val isLoading: Boolean = false,
    val isEmailValid: Boolean = true,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val showCheckEmailScreen: Boolean = false,
    val isProcessingDeepLink: Boolean = false
)

/**
 * Login screen for QuestPod app
 * Provides magic link authentication via email
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    onSignInWithMagicLink: suspend (String) -> Result<Unit> = { Result.success(Unit) },
    checkDeepLink: () -> Boolean = { false },
    processDeepLink: suspend () -> Result<Unit> = { Result.success(Unit) }
) {
    var state by remember { mutableStateOf(LoginScreenState()) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    
    // Auto-focus the email field when the screen appears
    LaunchedEffect(Unit) {
        delay(300) // Short delay to ensure the UI is ready
        focusRequester.requestFocus()
        
        // Check for deep link on first composition
        if (checkDeepLink()) {
            state = state.copy(isProcessingDeepLink = true)
            val result = processDeepLink()
            result.fold(
                onSuccess = {
                    onLoginSuccess()
                },
                onFailure = { error ->
                    state = state.copy(
                        isProcessingDeepLink = false,
                        errorMessage = error.message ?: "Failed to process login link"
                    )
                }
            )
        }
    }
    
    // Show success message and transition to check email screen
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            snackbarHostState.showSnackbar("Magic link sent! Check your email.")
            delay(1000) // Give user time to read the message
            state = state.copy(showCheckEmailScreen = true)
        }
    }
    
    // Show error message if any
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (state.isProcessingDeepLink) {
                // Show loading while processing deep link
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Signing you in...",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else if (state.showCheckEmailScreen) {
                // Show check email screen
                CheckEmailScreen(
                    email = state.email,
                    onBackToLogin = {
                        state = state.copy(showCheckEmailScreen = false)
                    }
                )
            } else {
                // Show regular login card
                Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Welcome headline
                    Text(
                        text = "Welcome to QuestPod",
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // App description
                    Text(
                        text = "Embark on interactive story adventures that respond to your choices. Sign in to continue your journey.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Email input field
                    OutlinedTextField(
                        value = state.email,
                        onValueChange = { 
                            state = state.copy(
                                email = it,
                                isEmailValid = isValidEmail(it) || it.isEmpty()
                            )
                        },
                        label = { Text("Email Address") },
                        placeholder = { Text("Enter your email") },
                        leadingIcon = { 
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email"
                            )
                        },
                        trailingIcon = {
                            if (!state.isEmailValid) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = "Error",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        isError = !state.isEmailValid,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                if (isValidEmail(state.email)) {
                                    handleSignIn(
                                        state.email,
                                        onSignInWithMagicLink,
                                        onStateChange = { state = it }
                                    )
                                } else {
                                    state = state.copy(isEmailValid = false)
                                }
                            }
                        ),
                        singleLine = true,
                        supportingText = {
                            if (!state.isEmailValid) {
                                Text(
                                    text = "Please enter a valid email address",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Sign in button
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            if (isValidEmail(state.email)) {
                                handleSignIn(
                                    state.email,
                                    onSignInWithMagicLink,
                                    onStateChange = { state = it }
                                )
                            } else {
                                state = state.copy(isEmailValid = false)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !state.isLoading && state.email.isNotEmpty()
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Send Magic Link",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Success message
                    AnimatedVisibility(
                        visible = state.isSuccess,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Text(
                            text = "Magic link sent! Check your email to continue.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

/**
 * Check email screen shown after sending magic link
 */
@Composable
private fun CheckEmailScreen(
    email: String,
    onBackToLogin: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = "Email sent",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Check your email",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "We've sent a magic link to:\n$email",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Click the link in the email to sign in to QuestPod.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = onBackToLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Back to login",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

/**
 * Handle sign in with magic link
 */
private fun handleSignIn(
    email: String,
    onSignInWithMagicLink: suspend (String) -> Result<Unit>,
    onStateChange: (LoginScreenState) -> Unit
) {
    onStateChange(LoginScreenState(email = email, isLoading = true))
    
    val coroutineScope = rememberCoroutineScope()
    
    coroutineScope.launch {
        val result = onSignInWithMagicLink(email)
        
        result.fold(
            onSuccess = {
                onStateChange(
                    LoginScreenState(
                        email = email,
                        isLoading = false,
                        isSuccess = true
                    )
                )
            },
            onFailure = { error ->
                onStateChange(
                    LoginScreenState(
                        email = email,
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to send magic link. Please try again."
                    )
                )
            }
        )
    }
}

/**
 * Validate email format
 */
private fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
    return email.matches(emailRegex.toRegex())
}

/**
 * Preview for the login screen
 */
@Composable
fun LoginScreenPreview() {
    QuestPodTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LoginScreen()
        }
    }
}
