package com.example.findmywork.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.findmywork.ui.theme.CategoryColor
import com.example.findmywork.ui.components.marketplace.MarketplaceSearchBar
import com.example.findmywork.ui.theme.*

data class ServiceCategoryItem(
    val id: String,
    val name: String,
    val description: String,
    val proCount: Int,
    val icon: ImageVector,
    val colorPair: CategoryColor
)

@Composable
fun CategoriesScreen(
    onNavigateBack: () -> Unit,
    onSelectCategory: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    val allCategories = remember {
        listOf(
            ServiceCategoryItem("cleaning", "Home Cleaning", "Deep cleaning, bathrooms, kitchen degreasing & sofa shampoo", 38, Icons.Rounded.CleaningServices, CategoryColors["cleaning"] ?: CategoryColor(Color(0xFFEFF6FF), Color(0xFF2563EB))),
            ServiceCategoryItem("plumbing", "Plumbing", "Leakage repair, pipe fittings, taps, valves & drainage", 42, Icons.Rounded.Plumbing, CategoryColors["plumbing"] ?: CategoryColor(Color(0xFFECFEFF), Color(0xFF0891B2))),
            ServiceCategoryItem("electrical", "Electrical", "Wiring, switches, inverter battery setups & fan repair", 56, Icons.Rounded.Bolt, CategoryColors["electrical"] ?: CategoryColor(Color(0xFFFFFBEB), Color(0xFFD97706))),
            ServiceCategoryItem("ac_repair", "AC & Appliance Repair", "AC jet service, gas charging, fridge & washing machine", 34, Icons.Rounded.AcUnit, CategoryColors["ac_repair"] ?: CategoryColor(Color(0xFFF0F9FF), Color(0xFF0284C7))),
            ServiceCategoryItem("carpentry", "Carpentry", "Furniture repair, custom modular fittings, doors & locks", 29, Icons.Rounded.Carpenter, CategoryColors["carpentry"] ?: CategoryColor(Color(0xFFFFF7ED), Color(0xFFEA580C))),
            ServiceCategoryItem("painting", "Painting", "Interior & exterior wall painting, waterproof texture & polish", 31, Icons.Rounded.FormatPaint, CategoryColors["painting"] ?: CategoryColor(Color(0xFFFAF5FF), Color(0xFF7C3AED))),
            ServiceCategoryItem("pest_control", "Pest Control", "Eco-friendly treatment for termites, cockroaches & bugs", 22, Icons.Rounded.PestControl, CategoryColors["pest_control"] ?: CategoryColor(Color(0xFFF0FDF4), Color(0xFF16A34A))),
            ServiceCategoryItem("cctv", "CCTV & Security", "HD security camera setup, smart locks & video doorbells", 18, Icons.Rounded.Videocam, CategoryColors["cctv"] ?: CategoryColor(Color(0xFFEEF2FF), Color(0xFF4F46E5))),
            ServiceCategoryItem("laundry", "Laundry & Dry Clean", "Doorstep pickup, steam ironing & suit dry cleaning", 25, Icons.Rounded.LocalLaundryService, CategoryColors["laundry"] ?: CategoryColor(Color(0xFFFDF2F8), Color(0xFFDB2777))),
            ServiceCategoryItem("car_wash", "Car Wash & Detailing", "Waterless foam cleaning, polish & interior deep vacuum", 27, Icons.Rounded.DirectionsCar, CategoryColors["car_wash"] ?: CategoryColor(Color(0xFFEEF2FF), Color(0xFF4338CA))),
            ServiceCategoryItem("bike_repair", "Bike/Scooter Repair", "Doorstep regular service, oil change, brakes & breakdown", 19, Icons.Rounded.TwoWheeler, CategoryColors["bike_repair"] ?: CategoryColor(Color(0xFFFFF1F2), Color(0xFFE11D48))),
            ServiceCategoryItem("driver", "Driver on Demand", "Verified drivers for city commute, night trips & outstation", 45, Icons.Rounded.DriveEta, CategoryColors["driver"] ?: CategoryColor(Color(0xFFF5F3FF), Color(0xFF6366F1))),
            ServiceCategoryItem("care", "Baby & Elderly Care", "Certified nurses, patient assistance & trusted babysitters", 16, Icons.Rounded.VolunteerActivism, CategoryColors["care"] ?: CategoryColor(Color(0xFFFEF2F2), Color(0xFFDC2626))),
            ServiceCategoryItem("fitness", "Yoga & Fitness", "Personal fitness trainers, yoga coaches at your home", 20, Icons.Rounded.FitnessCenter, CategoryColors["fitness"] ?: CategoryColor(Color(0xFFECFDF5), Color(0xFF059669))),
            ServiceCategoryItem("tutor", "Home Tutor", "Qualified tutors for school curriculum, Math, Science & English", 33, Icons.Rounded.School, CategoryColors["tutor"] ?: CategoryColor(Color(0xFFFFFBEB), Color(0xFFCA8A04))),
            ServiceCategoryItem("event", "Event Help & Decor", "Birthday balloons, anniversary setups, lighting & helper hands", 15, Icons.Rounded.Celebration, CategoryColors["event"] ?: CategoryColor(Color(0xFFFDF4FF), Color(0xFFC026D3)))
        )
    }

    val filteredCategories = remember(allCategories, searchQuery) {
        if (searchQuery.isBlank()) allCategories
        else allCategories.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FMWSurface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = FMWTextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = "All Services & Categories",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = FMWTextPrimary
                        )
                        Text(
                            text = "Find verified professionals for every need",
                            style = MaterialTheme.typography.bodySmall,
                            color = FMWTextSecondary
                        )
                    }
                }
                MarketplaceSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Search services...",
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                HorizontalDivider(color = FMWBorder.copy(alpha = 0.5f))
            }
        },
        containerColor = FMWBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredCategories) { category ->
                Surface(
                    onClick = { onSelectCategory(category.id) },
                    shape = RoundedCornerShape(18.dp),
                    color = FMWSurface,
                    border = BorderStroke(1.dp, FMWBorder),
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val isDark = LocalDarkTheme.current
                        val iconContainerBg = if (isDark) category.colorPair.icon.copy(alpha = 0.18f) else category.colorPair.bg
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(iconContainerBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = category.icon,
                                contentDescription = category.name,
                                tint = category.colorPair.icon,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = category.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = FMWTextPrimary
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(FMWSoftBlue)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${category.proCount} Pros",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = FMWBlue
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = category.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = FMWTextSecondary,
                                maxLines = 2,
                                lineHeight = 16.sp
                            )
                        }

                        Icon(
                            imageVector = Icons.Rounded.ChevronRight,
                            contentDescription = "Open",
                            tint = FMWTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
