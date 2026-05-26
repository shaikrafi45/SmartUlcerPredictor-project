package com.example.smartulcerpredictor.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartulcerpredictor.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun InitialScreen(onTimeout: () -> Unit = {}) {
    // Animation states
    val offsetX = remember { Animatable(-500f) }
    val offsetY = remember { Animatable(-800f) }
    val opacity = remember { Animatable(0f) }
    val scale = remember { Animatable(0.5f) }

    LaunchedEffect(Unit) {
        // Run animations in parallel
        launch {
            offsetX.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 3000, easing = LinearOutSlowInEasing)
            )
        }
        launch {
            offsetY.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 3000, easing = LinearOutSlowInEasing)
            )
        }
        launch {
            opacity.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 2000)
            )
        }
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 3000, easing = LinearOutSlowInEasing)
            )
        }

        delay(4000) // Total time before moving to next screen
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBFBFB))
    ) {
        // Decorative background circles
        Box(
            modifier = Modifier
                .size(450.dp)
                .offset(x = (-50).dp, y = (-180).dp)
                .clip(CircleShape)
                .background(Color(0xFFE8F0FE))
        )

        Box(
            modifier = Modifier
                .size(400.dp)
                .align(Alignment.BottomCenter)
                .offset(y = 150.dp)
                .clip(CircleShape)
                .background(Color(0xFFF1F5F0))
        )

        // Animated Central Content
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .size(260.dp)
                    .offset { IntOffset(offsetX.value.toInt(), offsetY.value.toInt()) }
                    .alpha(opacity.value)
                    .scale(scale.value),
                shape = RoundedCornerShape(32.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.app_logo),
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .size(150.dp)
                            .padding(8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Text(
                        text = "SMART ULCER\nPREDICTOR",
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        color = Color(0xFF1D3557),
                        fontWeight = FontWeight.Bold,
                        lineHeight = 22.sp
                    )
                }
            }
        }

        // Bottom Text (Static or could be animated too)
        Text(
            text = "Check your feet\nStop ulcers early",
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            color = Color(0xFF1D3557),
            fontWeight = FontWeight.Bold,
            lineHeight = 32.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InitialScreenPreview() {
    InitialScreen()
}
