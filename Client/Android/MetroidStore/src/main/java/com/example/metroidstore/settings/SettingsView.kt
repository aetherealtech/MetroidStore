package com.example.metroidstore.settings

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel

@Composable
fun SettingsView(
    viewModel: SettingsViewModel
) {
    Text("This floor is under construction")
}

class SettingsViewModel: ViewModel() {

}