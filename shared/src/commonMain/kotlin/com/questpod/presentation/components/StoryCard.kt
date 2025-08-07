package com.questpod.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.questpod.domain.model.Story
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

/**
 * Size variants for the StoryCard component
 */
enum class StoryCardSize {
    SMALL,  // For carousels
    MEDIUM, // For lists
    LARGE   // For featured items
}

/**
 * A reusable story card component that displays story information
 * with different size variants and states.
 */
@Composable
fun StoryCard(
    story: Story,
    size: StoryCardSize = StoryCardSize.MEDIUM,
    completionPercentage: Float? = null,
    isPremium: Boolean = false,
    onClick: () -> Unit = {}
) {
    // Animation for hover/press effect
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        label = "Card scale animation"
    )
    
    // Card dimensions based on size variant
    val cardModifier = when (size) {
        StoryCardSize.SMALL -> Modifier
            .width(150.dp)
            .height(220.dp)
        StoryCardSize.MEDIUM -> Modifier
            .width(180.dp)
            .height(260.dp)
        StoryCardSize.LARGE -> Modifier
            .width(300.dp)
            .height(360.dp)
    }
    
    // Image height based on size variant
    val imageHeight = when (size) {
        StoryCardSize.SMALL -> 100.dp
        StoryCardSize.MEDIUM -> 130.dp
        StoryCardSize.LARGE -> 200.dp
    }
    
    // Text style based on size variant
    val titleStyle = when (size) {
        StoryCardSize.SMALL -> MaterialTheme.typography.titleSmall
        StoryCardSize.MEDIUM -> MaterialTheme.typography.titleMedium
        StoryCardSize.LARGE -> MaterialTheme.typography.titleLarge
    }
    
    val descriptionStyle = when (size) {
        StoryCardSize.SMALL -> MaterialTheme.typography.bodySmall
        StoryCardSize.MEDIUM -> MaterialTheme.typography.bodyMedium
        StoryCardSize.LARGE -> MaterialTheme.typography.bodyLarge
    }
    
    // Main card
    Card(
        modifier = cardModifier
            .padding(8.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                onClick = {
                    onClick()
                },
                onPress = { isPressed = true },
                onRelease = { isPressed = false }
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 8.dp
        )
    ) {
        Column {
            // Cover image with loading state
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(imageHeight)
            ) {
                // Image with loading state
                KamelImage(
                    resource = asyncPainterResource(story.coverImageUrl ?: ""),
                    contentDescription = "Cover image for ${story.title}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    onLoading = { progress ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    onFailure = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AccessTime,
                                contentDescription = "Failed to load image",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
                
                // Premium badge if applicable
                if (isPremium) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Premium content",
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Premium",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
            
            // Content area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                // Title
                Text(
                    text = story.title,
                    style = titleStyle,
                    maxLines = if (size == StoryCardSize.SMALL) 1 else 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Description (except for small cards)
                if (size != StoryCardSize.SMALL) {
                    Text(
                        text = story.description ?: "",
                        style = descriptionStyle,
                        maxLines = if (size == StoryCardSize.MEDIUM) 2 else 3,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                // Story duration
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccessTime,
                        contentDescription = "Duration",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${story.estimatedDuration ?: "5"} min",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Progress bar for in-progress stories
                if (completionPercentage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        LinearProgressIndicator(
                            progress = { completionPercentage },
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${(completionPercentage * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

/**
 * Preview loading state of the story card
 */
@Composable
fun StoryCardLoadingPlaceholder(
    size: StoryCardSize = StoryCardSize.MEDIUM
) {
    // Card dimensions based on size variant
    val cardModifier = when (size) {
        StoryCardSize.SMALL -> Modifier
            .width(150.dp)
            .height(220.dp)
        StoryCardSize.MEDIUM -> Modifier
            .width(180.dp)
            .height(260.dp)
        StoryCardSize.LARGE -> Modifier
            .width(300.dp)
            .height(360.dp)
    }
    
    // Image height based on size variant
    val imageHeight = when (size) {
        StoryCardSize.SMALL -> 100.dp
        StoryCardSize.MEDIUM -> 130.dp
        StoryCardSize.LARGE -> 200.dp
    }
    
    Card(
        modifier = cardModifier
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            // Cover image placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(imageHeight)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            
            // Content area with placeholders
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                // Title placeholder
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(4.dp)
                ) {}
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Description placeholder (except for small cards)
                if (size != StoryCardSize.SMALL) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(4.dp)
                    ) {}
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(4.dp)
                    ) {}
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                // Duration placeholder
                Surface(
                    modifier = Modifier
                        .width(60.dp)
                        .height(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(4.dp)
                ) {}
            }
        }
    }
}
