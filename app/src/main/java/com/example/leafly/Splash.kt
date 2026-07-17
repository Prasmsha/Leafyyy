package com.example.leafly

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.leafly.ui.theme.LeaflyGreenBottom
import com.example.leafly.ui.theme.LeaflyGreenCircle
import com.example.leafly.ui.theme.LeaflyGreenDark
import com.example.leafly.ui.theme.LeaflyGreenLight
import com.example.leafly.ui.theme.LeaflyGreenPale
import com.example.leafly.ui.theme.LeaflyGreenTag
import com.example.leafly.ui.theme.LeaflyTheme
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    onGetStarted: () -> Unit = {},
    onAlreadyHaveAccount: () -> Unit = {},
    onAutoLogin: () -> Unit = {}
) {
    // Auto-login: if user is already logged in, go directly to dashboard
    LaunchedEffect(Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            onAutoLogin()
        }
    }

    Surface(modifier = modifier.fillMaxSize(), color = LeaflyGreenLight) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier.size(140.dp).clip(CircleShape).background(LeaflyGreenCircle),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_leaf),
                        contentDescription = null,
                        modifier = Modifier.size(70.dp),
                        tint = LeaflyGreenDark
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                Text(
                    text = "your plants,\nalways thriving",
                    fontSize = 34.sp, fontWeight = FontWeight.Bold,
                    color = LeaflyGreenDark, textAlign = TextAlign.Center, lineHeight = 40.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "track care schedules and never lose a plant again",
                    fontSize = 18.sp, color = LeaflyGreenDark,
                    textAlign = TextAlign.Center, lineHeight = 26.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    FeatureTag(label = "water", iconResId = R.drawable.ic_water_drop)
                    FeatureTag(label = "sunlight", iconResId = R.drawable.ic_sunlight)
                }

                Spacer(modifier = Modifier.height(12.dp))
                FeatureTag(label = "reminders", iconResId = R.drawable.ic_reminders)
                Spacer(modifier = Modifier.height(48.dp))
                DotsIndicator(totalDots = 3, selectedIndex = 0)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = LeaflyGreenBottom, shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                    .padding(horizontal = 32.dp, vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onGetStarted,
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.25f))
                ) {
                    Text("get started", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onAlreadyHaveAccount,
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text("i already have an account", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun FeatureTag(label: String, iconResId: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(LeaflyGreenTag)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(painter = painterResource(id = iconResId), contentDescription = null,
            modifier = Modifier.size(18.dp), tint = LeaflyGreenDark)
        Text(text = label, color = LeaflyGreenDark, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun DotsIndicator(totalDots: Int, selectedIndex: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        repeat(totalDots) { index ->
            if (index == selectedIndex) {
                Box(modifier = Modifier.width(28.dp).height(8.dp).clip(RoundedCornerShape(4.dp)).background(LeaflyGreenDark))
            } else {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(LeaflyGreenPale))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    LeaflyTheme { SplashScreen() }
}
