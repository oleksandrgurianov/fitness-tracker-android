package com.example.fitnesstracker.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.fitnesstracker.R

val FuturaExtraBlackCondensedItalic = FontFamily(Font(R.font.futura_extra_black_condensed_italic))

// Set of Material typography styles to start with
val Typography = Typography(
    h1 = TextStyle(
        color = Color.White,
        fontSize = 44.sp,
        fontFamily = FuturaExtraBlackCondensedItalic,
    ),
    h2 = TextStyle(
        color = Color.Gray,
        fontSize = 44.sp,
        fontFamily = FuturaExtraBlackCondensedItalic
    ),
    h3 = TextStyle(
        color = Color.Gray,
        fontSize = 22.sp,
        fontFamily = FuturaExtraBlackCondensedItalic
    )
)