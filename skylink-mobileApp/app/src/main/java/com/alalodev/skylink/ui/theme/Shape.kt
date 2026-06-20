package com.alalodev.skylink.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// "Aviation Consumer Experience" Shapes: Extra-large corner radii (Pill-shaped)
val Shapes = Shapes(
    small = RoundedCornerShape(8.dp),      // Chips, Tooltips
    medium = RoundedCornerShape(16.dp),    // Buttons, Input Fields
    large = RoundedCornerShape(24.dp),     // Cards
    extraLarge = RoundedCornerShape(32.dp) // Modals, Bottom Sheets
)
