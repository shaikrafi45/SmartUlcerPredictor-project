package com.example.smartulcerpredictor.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrecautionsScreen(onBack: () -> Unit = {}) {
    val uriHandler = LocalUriHandler.current

    Scaffold(
        containerColor = Color(0xFFFBFBFB),
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Precautions",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        )
                    }
                },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1976D2)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Back",
                            color = Color(0xFF1976D2),
                            fontSize = 18.sp
                        )
                    }
                },
                actions = {
                    // Empty box to balance the title centering
                    Box(modifier = Modifier.width(80.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFBFBFB))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Precautions & Recovery Tips",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🟢", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Recovery Tips:",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            val tips = listOf(
                "Continue eating balanced meals with fruits, vegetables, and lean protein.",
                "Stay hydrated—drink at least 2 liters of water daily.",
                "Include vitamin C (oranges, guava) and vitamin A (carrots, sweet potatoes).",
                "Keep the wound clean and monitor for changes.",
                "Avoid strenuous activity until fully healed.",
                "Get enough sleep to support immune function.",
                "Wear loose, breathable clothing around the wound.",
                "Avoid scratching or picking at scabs.",
                "Use mild soap and clean towels for hygiene.",
                "Practice gratitude or light meditation to support recovery."
            )

            tips.forEach { tip ->
                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(text = "•", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = tip,
                        fontSize = 15.sp,
                        color = Color.Black,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Movie,
                    contentDescription = null,
                    tint = Color.DarkGray,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Helpful Video Guides",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            VideoGuideItem(
                icon = "▶️",
                title = "How to Change a Sterile Wound Dressing",
                onClick = { uriHandler.openUri("https://youtu.be/0vRFW2bsKpM?si=RjGJb6KYsKutsf_v") }
            )
            VideoGuideItem(
                icon = "🍃",
                title = "Foods That Help You Heal Faster",
                onClick = { uriHandler.openUri("https://youtu.be/5Bvveo4MQxg?si=3OxlYbV_Az1jDNrw") }
            )
            VideoGuideItem(
                icon = "🧼",
                title = "Hand Washing for Wound Care",
                onClick = { uriHandler.openUri("https://youtu.be/9TvIbXlb8LU?si=m27S0tvwHGvGRlGS") }
            )
            VideoGuideItem(
                icon = "🏃",
                title = "How to Elevate a Swollen Limb",
                onClick = { uriHandler.openUri("https://youtu.be/GqbKehAG648?si=5IwI98xIJJ9D2mf9") }
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun VideoGuideItem(icon: String, title: String, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = icon, fontSize = 24.sp)
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            color = Color(0xFF66BB6A), // Greenish text
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PrecautionsScreenPreview() {
    PrecautionsScreen()
}
