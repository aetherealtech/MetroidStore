package aetherealtech.metroidstore.customerclient.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun MilestoneProgressView(
    milestones: List<String>,
    activeMilestoneIndex: Int
) {
    if(milestones.size < 2)
        throw IllegalArgumentException("Milestone Progress View needs at least 2 milestones")

    Column(
        horizontalAlignment = Alignment.Start
    ) {
        milestones.indices.forEach { index ->
            if (index == 0) {
                LabeledProgressStartSegment(
                    text = milestones[index],
                    active = index == activeMilestoneIndex,
                    enabled = index <= activeMilestoneIndex
                )
            } else if (index < milestones.lastIndex) {
                LabeledProgressMiddleSegment(
                    text = milestones[index],
                    active = index == activeMilestoneIndex,
                    enabled = index <= activeMilestoneIndex
                )
            } else {
                LabeledProgressEndSegment(
                    text = milestones[index],
                    active = index == activeMilestoneIndex,
                    enabled = index <= activeMilestoneIndex
                )
            }
        }
    }
}

@Composable
fun LabeledProgressStartSegment(
    text: String,
    active: Boolean = false,
    enabled: Boolean
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ProgressStartSegment(
            enabled = enabled
        )
        Box(
            modifier = Modifier
                .height(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontWeight = if (active) FontWeight.Bold else null
            )
        }
    }
}

@Composable
fun LabeledProgressMiddleSegment(
    text: String,
    active: Boolean = false,
    enabled: Boolean
) {
    Row(
        modifier = Modifier
            .height(intrinsicSize = IntrinsicSize.Max),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ProgressMiddleSegment(
            enabled = enabled
        )
        Column {
            Spacer(modifier = Modifier.weight(1.0f))

            Box(
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = text,
                    fontWeight = if (active) FontWeight.Bold else null
                )
            }

            Spacer(modifier = Modifier.weight(1.0f))
        }
    }
}

@Composable
fun LabeledProgressEndSegment(
    text: String,
    active: Boolean = false,
    enabled: Boolean
) {
    Row(
        modifier = Modifier
            .height(intrinsicSize = IntrinsicSize.Max),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ProgressEndSegment(
            enabled = enabled
        )
        Column {
            Spacer(modifier = Modifier.weight(1.0f))

            Box(
                modifier = Modifier
                    .height(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    fontWeight = if (active) FontWeight.Bold else null
                )
            }
        }
    }
}

@Composable
fun ProgressStartSegment(
    enabled: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProgressMilestone(
            enabled = enabled
        )
        ProgressLeg(
            enabled = enabled
        )
    }
}

@Composable
fun ProgressMiddleSegment(
    enabled: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProgressLeg(
            enabled = enabled
        )
        ProgressMilestone(
            enabled = enabled
        )
        ProgressLeg(
            enabled = enabled
        )
    }
}

@Composable
fun ProgressEndSegment(
    enabled: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProgressLeg(
            enabled = enabled
        )
        ProgressMilestone(
            enabled = enabled
        )
    }
}

val disabledColor = Color(0xFFAAAAAA)
val enabledColor = Color(0xFF0000CC)

@Composable
fun ProgressMilestone(
    enabled: Boolean
) {
    var modifier = Modifier
        .size(32.dp)

    if(enabled) {
        modifier = modifier
            .background(
                color = enabledColor,
                shape = CircleShape
            )
    } else {
        modifier = modifier
            .border(
                width = 8.dp,
                color = disabledColor,
                shape = CircleShape
            )
    }

    Box(
        modifier = modifier
    )
}

@Composable
fun ProgressLeg(
    enabled: Boolean
) {
    Divider(
        color = if (enabled) enabledColor else disabledColor,
        modifier = Modifier
            .height(32.dp)
            .width(8.dp)
    )
}