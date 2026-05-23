package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    medium = RoundedCornerShape(24.dp), // For standard M3 Cards
    large = RoundedCornerShape(50.dp),  // Fully rounded Pills
    extraLarge = RoundedCornerShape(999.dp)
)
