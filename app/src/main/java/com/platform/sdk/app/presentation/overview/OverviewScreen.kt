package com.platform.sdk.app.presentation.overview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.platform.sdk.AsyncOperation
import com.platform.sdk.app.domain.useCase.FetchPigmentsUseCase
import com.platform.sdk.loading

@Composable
fun OverviewScreen(
    viewModel: OverviewViewModel = hiltViewModel()
) {

    val pigments by viewModel.pigments.collectAsState()

    if (pigments.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Is loading: ${pigments.isLoading}",
                textAlign = TextAlign.Center,
                fontSize = 33.sp
            )
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = pigments.data?.firstOrNull()?.name ?: pigments.error?.message.orEmpty(),
                textAlign = TextAlign.Center,
                fontSize = 33.sp
            )
        }
    }
}
