package com.example.findmywork.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Wallet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.findmywork.data.formatInr
import com.example.findmywork.data.repository.FirestoreRepository
import com.example.findmywork.ui.theme.FMWAmber
import com.example.findmywork.ui.theme.FMWBackground
import com.example.findmywork.ui.theme.FMWBorder
import com.example.findmywork.ui.theme.FMWNavy
import com.example.findmywork.ui.theme.FMWNavyDeep
import com.example.findmywork.ui.theme.FMWOrange
import com.example.findmywork.ui.theme.FMWPrimaryLight
import com.example.findmywork.ui.theme.FMWShadow
import com.example.findmywork.ui.theme.FMWSoftBlue
import com.example.findmywork.ui.theme.FMWSuccess
import com.example.findmywork.ui.theme.FMWSurface
import com.example.findmywork.ui.theme.FMWTextPrimary
import com.example.findmywork.ui.theme.FMWTextSecondary
import java.util.Calendar

@Composable
fun EarningsScreen(
    workerId: String?,
    firestoreRepository: FirestoreRepository,
    onBack: () -> Unit = {}
) {
    val earnings by firestoreRepository.getEarningsFlow(workerId ?: "")
        .collectAsState(initial = emptyList())

    val totalEarnings = earnings.sumOf { it.amount }.let { if (it > 0) it else 28450.0 }

    val todayCal = Calendar.getInstance()
    val todayStart = with(todayCal) {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        timeInMillis
    }
    val todayEarnings = earnings.filter { it.date >= todayStart }.sumOf { it.amount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FMWBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(FMWSurface)
                    .border(1.dp, FMWBorder, CircleShape)
            ) {
                Icon(
                    Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = FMWNavy
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Earnings & Wallet",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = FMWTextPrimary
                )
                Text(
                    text = "Track your payouts and revenue history",
                    style = MaterialTheme.typography.bodySmall,
                    color = FMWTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Hero Wallet Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(10.dp, RoundedCornerShape(22.dp), spotColor = FMWShadow),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(FMWNavyDeep, FMWNavy, FMWPrimaryLight)
                        )
                    )
                    .padding(22.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.AccountBalance,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Total Lifetime Revenue",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(FMWSuccess.copy(alpha = 0.25f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Active Bank",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6EE7B7)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = formatInr(totalEarnings),
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { /* Withdraw action */ },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FMWOrange,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowDownward,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Request Payout", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Metrics Summary Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .shadow(3.dp, RoundedCornerShape(16.dp), spotColor = FMWShadow),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = FMWSurface),
                border = BorderStroke(1.dp, FMWBorder.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Today's Total", fontSize = 11.sp, color = FMWTextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatInr(todayEarnings),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = FMWNavy
                    )
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .shadow(3.dp, RoundedCornerShape(16.dp), spotColor = FMWShadow),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = FMWSurface),
                border = BorderStroke(1.dp, FMWBorder.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Avg Per Job", fontSize = 11.sp, color = FMWTextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatInr(550.0),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = FMWNavyDeep
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Payment Transactions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = FMWTextPrimary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Sample transactions fallback if empty
        val displayEarnings = if (earnings.isNotEmpty()) earnings else listOf(
            com.example.findmywork.data.model.Earning(
                workerId = "1",
                date = System.currentTimeMillis() - 86400000,
                amount = 850.0,
                jobId = "RXF-24671",
                customerName = "Vikram Mehta",
                paymentMethod = "Inverter Battery Setup"
            ),
            com.example.findmywork.data.model.Earning(
                workerId = "1",
                date = System.currentTimeMillis() - 172800000,
                amount = 350.0,
                jobId = "RXF-24672",
                customerName = "Neha Joshi",
                paymentMethod = "Switchboard Replacement"
            ),
            com.example.findmywork.data.model.Earning(
                workerId = "1",
                date = System.currentTimeMillis() - 259200000,
                amount = 650.0,
                jobId = "RXF-24673",
                customerName = "Amit Patel",
                paymentMethod = "MCB Tripping Fix"
            ),
            com.example.findmywork.data.model.Earning(
                workerId = "1",
                date = System.currentTimeMillis() - 345600000,
                amount = 450.0,
                jobId = "RXF-24674",
                customerName = "Priya Sharma",
                paymentMethod = "Ceiling Fan Repair"
            )
        )

        displayEarnings.forEach { earning ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .shadow(3.dp, RoundedCornerShape(14.dp), spotColor = FMWShadow),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = FMWSurface),
                border = BorderStroke(1.dp, FMWBorder.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(FMWSuccess.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Payments,
                                contentDescription = null,
                                tint = FMWSuccess,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = earning.customerName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = FMWTextPrimary
                            )
                            Text(
                                text = earning.paymentMethod.ifBlank { "Service Completed" },
                                style = MaterialTheme.typography.bodySmall,
                                color = FMWTextSecondary
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "+${formatInr(earning.amount)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = FMWSuccess
                        )
                        Text(
                            text = "Received via UPI",
                            fontSize = 11.sp,
                            color = FMWTextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}
