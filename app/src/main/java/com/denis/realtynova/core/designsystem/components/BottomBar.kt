
package com.denis.realtynova.core.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.denis.realtynova.core.designsystem.theme.ChampagneGold


/*
 * ================================================================
 * REALTYNOVA — NOVA FLOATING DOCK
 * ================================================================
 *
 * Design principles:
 *
 * 1. Floating premium surface
 * 2. Strong selected-state hierarchy
 * 3. Spring-based motion
 * 4. Minimal visual noise
 * 5. Large touch targets
 * 6. Accessible semantics
 * 7. Optional AI center action
 */


/*
 * ================================================================
 * MAIN NAVIGATION
 * ================================================================
 */

@Composable
fun CreativeBottomBar(
    tabs: List<BottomTab>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    showLabels: Boolean = true
) {

    if (tabs.isEmpty()) return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(
                horizontal = 18.dp,
                vertical = 10.dp
            )
            .height(78.dp)
    ) {

        /*
         * --------------------------------------------------------
         * Outer shadow / depth
         * --------------------------------------------------------
         */

        Box(
            modifier = Modifier
                .fillMaxSize()
                .shadow(
                    elevation = 18.dp,
                    shape = RoundedCornerShape(30.dp),
                    clip = false
                )
        )

        /*
         * --------------------------------------------------------
         * Glass shell
         * --------------------------------------------------------
         */

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .clip(
                    RoundedCornerShape(30.dp)
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.65f),
                            Color.White.copy(alpha = 0.12f)
                        )
                    ),
                    shape = RoundedCornerShape(30.dp)
                ),

            color = MaterialTheme
                .colorScheme
                .surface
                .copy(alpha = 0.92f),

            tonalElevation = 8.dp
        ) {

            /*
             * ----------------------------------------------------
             * Subtle inner highlight
             * ----------------------------------------------------
             */

            Box(
                modifier = Modifier.fillMaxSize()
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.White.copy(
                                        alpha = 0.65f
                                    ),
                                    Color.Transparent
                                )
                            )
                        )
                )

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            horizontal = 7.dp,
                            vertical = 7.dp
                        ),

                    horizontalArrangement =
                        Arrangement.SpaceEvenly,

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    tabs.forEachIndexed { index, tab ->

                        val selected =
                            selectedTabIndex == index

                        BottomTabItem(
                            tab = tab,
                            isSelected = selected,
                            showLabel = showLabels,
                            onClick = {
                                onTabSelected(index)
                            },
                            modifier = Modifier
                                .weight(1f)
                        )
                    }
                }
            }
        }
    }
}


/*
 * ================================================================
 * INDIVIDUAL TAB
 * ================================================================
 */

@Composable
private fun BottomTabItem(
    tab: BottomTab,
    isSelected: Boolean,
    showLabel: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val selectedColor =
        ChampagneGold

    val inactiveColor =
        MaterialTheme
            .colorScheme
            .onSurface
            .copy(alpha = 0.48f)

    val iconColor by animateColorAsState(
        targetValue =
            if (isSelected)
                selectedColor
            else
                inactiveColor,

        animationSpec =
            tween(
                durationMillis = 220,
                easing = FastOutSlowInEasing
            ),

        label = "BottomTabColor"
    )

    val iconScale by animateFloatAsState(
        targetValue =
            if (isSelected)
                1.08f
            else
                1f,

        animationSpec =
            spring(
                dampingRatio = 0.58f,
                stiffness = Spring.StiffnessMedium
            ),

        label = "BottomTabScale"
    )

    val indicatorWidth by animateDpAsState(
        targetValue =
            if (isSelected)
                34.dp
            else
                0.dp,

        animationSpec =
            spring(
                dampingRatio = 0.75f,
                stiffness = Spring.StiffnessMedium
            ),

        label = "IndicatorWidth"
    )

    /*
     * Accessibility + proper touch semantics.
     */

    Column(
        modifier = modifier
            .height(64.dp)
            .clip(
                RoundedCornerShape(20.dp)
            )
            .clickable(
                interactionSource =
                    remember {
                        MutableInteractionSource()
                    },

                indication = null,
                role = Role.Tab,
                onClick = onClick
            )
            .semantics {

                selected =
                    isSelected

                contentDescription =
                    if (isSelected)
                        "${tab.label}, selected"
                    else
                        tab.label

                role = Role.Tab
            }
            .animateContentSize(
                animationSpec =
                    spring(
                        dampingRatio = 0.8f
                    )
            ),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Center
    ) {

        /*
         * --------------------------------------------------------
         * Selected icon capsule
         * --------------------------------------------------------
         */

        Box(
            modifier = Modifier
                .size(
                    if (isSelected)
                        40.dp
                    else
                        36.dp
                )
                .clip(CircleShape)
                .background(
                    if (isSelected)
                        selectedColor.copy(
                            alpha = 0.13f
                        )
                    else
                        Color.Transparent
                )
                .graphicsLayer {
                    scaleX = iconScale
                    scaleY = iconScale
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = tab.icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(21.dp)
            )
        }

                    /*
                     * --------------------------------------------------------
                     * Label
                     * --------------------------------------------------------
                     */

                    AnimatedVisibility(
                        visible =
                            isSelected && showLabel,

                        enter =
                            fadeIn(
                                animationSpec =
                                    tween(180)
                            ) +
                                    scaleIn(
                                        animationSpec =
                                            spring(
                                                dampingRatio = 0.75f
                                            )
                                    ),

                        exit =
                            fadeOut(
                                animationSpec =
                                    tween(100)
                            )
                    ) {

                        Text(
                            text =
                                tab.label.uppercase(),

                            color =
                                selectedColor,

                            fontSize =
                                8.sp,

                            letterSpacing =
                                0.65.sp
                        )
                    }

                    /*
                     * --------------------------------------------------------
                     * Gold active indicator
                     * --------------------------------------------------------
                     */

                    Box(
                        modifier = Modifier
                            .padding(top = 3.dp)
                            .width(indicatorWidth)
                            .height(2.dp)
                            .clip(CircleShape)
                            .background(
                                selectedColor
                            )
                    )
                }
    }


    /*
     * ================================================================
     * SPECIAL AI CENTER ACTION
     * ================================================================
     *
     * Optional component for a 5-tab navigation architecture:
     *
     * Home | Search | AI | Saved | Profile
     *
     * Use this instead of putting AI inside a normal tab.
     */

    @Composable
    fun NovaAiAction(
        onClick: () -> Unit,
        modifier: Modifier = Modifier
    ) {

        val transition =
            rememberInfiniteTransition(
                label = "NovaAiGlow"
            )

        val glowAlpha by transition.animateFloat(
            initialValue = 0.35f,
            targetValue = 0.75f,

            animationSpec =
                infiniteRepeatable(
                    animation =
                        tween(
                            durationMillis = 1800,
                            easing =
                                FastOutSlowInEasing
                        ),

                    repeatMode =
                        RepeatMode.Reverse
                ),

            label = "NovaAiGlowAlpha"
        )

        Box(
            modifier = modifier
                .size(58.dp)
                .shadow(
                    elevation = 14.dp,
                    shape = CircleShape
                )
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF171B22),
                            Color(0xFF272018)
                        )
                    )
                )
                .border(
                    width = 1.5.dp,
                    color =
                        ChampagneGold.copy(
                            alpha = glowAlpha
                        ),
                    shape = CircleShape
                )
                .clickable(
                    interactionSource =
                        remember {
                            MutableInteractionSource()
                        },

                    indication = null,
                    onClick = onClick
                )
                .semantics {
                    contentDescription =
                        "Open RealtyNova AI assistant"
                },

            contentAlignment =
                Alignment.Center
        ) {

            Icon(
                imageVector =
                    Icons.Default.AutoAwesome,

                contentDescription =
                    null,

                tint =
                    ChampagneGold,

                modifier =
                    Modifier.size(24.dp)
            )
        }
    }


    /*
     * ================================================================
     * DATA MODEL
     * ================================================================
     */

    data class BottomTab(
        val route: Any,
        val icon: ImageVector,
        val label: String
    )