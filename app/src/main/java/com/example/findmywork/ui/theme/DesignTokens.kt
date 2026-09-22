package com.example.findmywork.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

object FMWSpacing {
    val xxs = 4.dp
    val xs = 8.dp
    val s = 12.dp
    val m = 16.dp
    val l = 20.dp
    val xl = 24.dp
    val xxl = 32.dp
    val screenHorizontal = 20.dp
}

object FMWRadius {
    val small = 10.dp
    val medium = 14.dp
    val large = 18.dp
    val hero = 24.dp
    val search = 18.dp
    val button = 16.dp
    val chip = 12.dp
    val pill = 50.dp
}

object FMWShapes {
    val small = RoundedCornerShape(FMWRadius.small)
    val medium = RoundedCornerShape(FMWRadius.medium)
    val large = RoundedCornerShape(FMWRadius.large)
    val hero = RoundedCornerShape(FMWRadius.hero)
    val search = RoundedCornerShape(FMWRadius.search)
    val button = RoundedCornerShape(FMWRadius.button)
    val chip = RoundedCornerShape(FMWRadius.chip)
    val pill = RoundedCornerShape(FMWRadius.pill)
}

object FMWElevation {
    val none = 0.dp
    val low = 2.dp
    val medium = 4.dp
    val high = 8.dp
}
