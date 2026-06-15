package com.example.agrodata

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CardModuloAcessivel(
    titulo: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    corDestaque: Color,
    descricaoRapida: String,
    content: @Composable () -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.5.dp, if (expandido) corDestaque else Color(0xFFE0E0E0)),
        elevation = CardDefaults.cardElevation(defaultElevation = if (expandido) 4.dp else 1.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .clickable { expandido = !expandido }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(corDestaque.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = corDestaque, modifier = Modifier.size(24.dp))
                }

                Spacer(Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = titulo,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = Color(0xFF1A1A1A)
                    )
                    Text(
                        text = descricaoRapida,
                        fontSize = 12.sp,
                        color = Color(0xFF666666)
                    )
                }

                Icon(
                    imageVector = if (expandido) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color(0xFF999999),
                    modifier = Modifier.size(28.dp)
                )
            }

            if (expandido) {
                HorizontalDivider(thickness = 1.dp, color = Color(0xFFEEEEEE))
                Box(modifier = Modifier.padding(16.dp)) {
                    content()
                }
            }
        }
    }
}