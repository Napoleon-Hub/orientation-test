package com.funnygaytest.ui.components.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.funnygaytest.R
import com.funnygaytest.ui.components.buttons.MainButton
import com.funnygaytest.ui.themes.MainTestTheme

@Composable
fun LaboratoryAccessDialog(
    onConsentAccepted: () -> Unit,
    onDecline: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val privacyPolicyUrl = "https://doc-hosting.flycricket.io/hardcore-gay-test-privacy-policy/d321abd6-7d19-4780-8079-b00305bb3aa9/privacy"

    Dialog(
        onDismissRequest = { /* Не позволяем закрывать кликом мимо */ },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .wrapContentHeight()
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(20.dp),
            color = MainTestTheme.colors.primaryElement,
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    text = stringResource(R.string.access_dialog_title),
                    style = MainTestTheme.typography.heading
                )

                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = stringResource(R.string.access_dialog_warning),
                        style = MainTestTheme.typography.subText,
                        color = Color.Yellow.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.access_dialog_ad_description),
                        style = MainTestTheme.typography.subText.copy(fontSize = 12.sp),
                        color = MainTestTheme.colors.secondaryText
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { uriHandler.openUri(privacyPolicyUrl) },
                        text = stringResource(R.string.access_dialog_privacy_policy),
                        style = MainTestTheme.typography.subText.copy(
                            fontSize = 12.sp,
                            textDecoration = TextDecoration.Underline
                        ),
                        color = MainTestTheme.colors.secondaryText
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MainButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        text = stringResource(R.string.access_dialog_button_continue),
                        onClick = onConsentAccepted
                    )

                    TextButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onDecline
                    ) {
                        Text(
                            text = stringResource(R.string.access_dialog_button_finish),
                            color = MainTestTheme.colors.secondaryText,
                            style = MainTestTheme.typography.subText
                        )
                    }
                }
            }
        }
    }
}