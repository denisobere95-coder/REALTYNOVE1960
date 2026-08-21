package com.denis.realtynova.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.denis.realtynova.core.designsystem.theme.ChampagneGold
import com.denis.realtynova.core.designsystem.theme.DeepEmerald
import com.denis.realtynova.core.designsystem.theme.RealtyNovaIcons
import com.denis.realtynova.core.designsystem.theme.RealtyNovaTextStyles
import com.denis.realtynova.core.domain.model.TrustLevel
import com.denis.realtynova.core.domain.model.TrustScore

@Composable
fun TrustScoreBadge(
    score: TrustScore,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (score.level) {
        TrustLevel.EXCEPTIONAL -> DeepEmerald.copy(alpha = 0.15f)
        TrustLevel.HIGH -> DeepEmerald.copy(alpha = 0.1f)
        TrustLevel.MODERATE -> ChampagneGold.copy(alpha = 0.15f)
        TrustLevel.LOW -> Color.Red.copy(alpha = 0.1f)
    }

    val contentColor = when (score.level) {
        TrustLevel.EXCEPTIONAL, TrustLevel.HIGH -> DeepEmerald
        TrustLevel.MODERATE -> ChampagneGold
        TrustLevel.LOW -> Color.Red
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, contentColor.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = RealtyNovaIcons.Trust,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = "TRUST SCORE",
                    style = RealtyNovaTextStyles.PremiumLabel.copy(
                        color = contentColor.copy(alpha = 0.8f),
                        fontSize = 8.sp,
                        letterSpacing = 0.5.sp
                    )
                )
                Text(
                    text = "${score.overallScore}/100",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = contentColor,
                        fontSize = 12.sp
                    )
                )
            }
        }
    }
}
