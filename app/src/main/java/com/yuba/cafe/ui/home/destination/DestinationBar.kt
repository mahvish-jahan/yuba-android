package com.yuba.cafe.ui.home.destination

import android.app.Application
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yuba.cafe.R
import com.yuba.cafe.ui.components.JetsnackDivider
import com.yuba.cafe.ui.theme.AlphaNearOpaque
import com.yuba.cafe.ui.theme.JetsnackTheme

@Composable
fun DestinationBar(
    application: Application,
    modifier: Modifier = Modifier,
    viewModel: DestinationViewModelModel = viewModel(
        factory = DestinationViewModelModel.provideFactory(
            application
        )
    )
) {

    val currentAddress by viewModel.currentAddress.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.getCurrentAddress()
    }

    fun getCurrentAddress(): String {
        if (currentAddress.isEmpty()) return "No address";

        var currentAddressStr = ""
        val address = currentAddress[0]
        currentAddressStr = "${address.addressDetail}, Pincode: ${address.pinCode}"
        return currentAddressStr
    }

    Column(modifier = modifier.statusBarsPadding()) {
        TopAppBar(
            backgroundColor = JetsnackTheme.colors.uiBackground.copy(alpha = AlphaNearOpaque),
            contentColor = JetsnackTheme.colors.textSecondary,
            elevation = 0.dp
        ) {
            Text(
                text = getCurrentAddress(),
                style = MaterialTheme.typography.subtitle1,
                color = JetsnackTheme.colors.textSecondary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            )
            IconButton(
                onClick = { /* todo */ },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ExpandMore,
                    tint = JetsnackTheme.colors.brand,
                    contentDescription = stringResource(R.string.label_select_delivery)
                )
            }
        }
        JetsnackDivider()
    }
}
