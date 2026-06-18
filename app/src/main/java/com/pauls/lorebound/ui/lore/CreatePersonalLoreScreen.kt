package com.pauls.lorebound.ui.lore

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.pauls.lorebound.ui.theme.BrutalAccent
import com.pauls.lorebound.ui.theme.BrutalMuted
import com.pauls.lorebound.ui.theme.BrutalSubtle
import com.pauls.lorebound.ui.theme.BrutalSurfaceVariant
import com.pauls.lorebound.ui.theme.QuestGreen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreatePersonalLoreScreen(
    onBack: () -> Unit,
    viewModel: CreatePersonalLoreViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.savedSuccessfully) {
        if (state.savedSuccessfully) onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "RECORD LORE",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::toggleFavorite) {
                        Icon(
                            imageVector = if (state.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (state.isFavorite) BrutalAccent else BrutalMuted
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // ── TITLE ─────────────────────────────────
            OutlinedTextField(
                value = state.title,
                onValueChange = viewModel::updateTitle,
                label = { Text("TITLE", color = BrutalMuted) },
                placeholder = { Text("What happened?", color = BrutalMuted) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = loreTextFieldColors(),
                isError = state.validationError != null
            )

            state.validationError?.let { error ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = error,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── STORY ─────────────────────────────────
            OutlinedTextField(
                value = state.story,
                onValueChange = viewModel::updateStory,
                label = { Text("YOUR STORY", color = BrutalMuted) },
                placeholder = { Text("Tell the story...", color = BrutalMuted) },
                minLines = 4,
                maxLines = 10,
                modifier = Modifier.fillMaxWidth(),
                colors = loreTextFieldColors()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── TAGS ──────────────────────────────────
            Text(
                text = "TAGS",
                style = MaterialTheme.typography.labelMedium,
                color = BrutalMuted
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CreatePersonalLoreViewModel.SUGGESTED_TAGS.forEach { tag ->
                    val selected = tag in state.selectedTags
                    FilterChip(
                        selected = selected,
                        onClick = { viewModel.toggleTag(tag) },
                        label = {
                            Text(
                                text = tag.uppercase(),
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = BrutalSurfaceVariant,
                            selectedContainerColor = BrutalAccent.copy(alpha = 0.15f),
                            labelColor = BrutalMuted,
                            selectedLabelColor = BrutalAccent
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = BrutalSubtle,
                            selectedBorderColor = BrutalAccent,
                            enabled = true,
                            selected = selected
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── PHOTO ─────────────────────────────────
            Text(
                text = "PHOTO",
                style = MaterialTheme.typography.labelMedium,
                color = BrutalMuted
            )
            Spacer(modifier = Modifier.height(8.dp))
            LorePhotoPicker(
                photoUri = state.photoUri,
                onPhotoSelected = viewModel::updatePhotoUri
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── LOCATION ──────────────────────────────
            Text(
                text = "LOCATION",
                style = MaterialTheme.typography.labelMedium,
                color = BrutalMuted
            )
            Spacer(modifier = Modifier.height(8.dp))
            LoreLocationCapture(
                captured = state.locationCaptured,
                loading = state.locationLoading,
                onLocationCaptured = viewModel::updateLocation,
                onLocationLoading = viewModel::setLocationLoading
            )
            if (state.locationCaptured) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.locationName,
                    onValueChange = viewModel::updateLocationName,
                    label = { Text("LOCATION NAME", color = BrutalMuted) },
                    placeholder = { Text("e.g. The Hidden Café", color = BrutalMuted) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = loreTextFieldColors()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── SAVE BUTTON ───────────────────────────
            Button(
                onClick = viewModel::save,
                enabled = !state.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color.Black
                    )
                } else {
                    Text("RECORD THIS MEMORY", fontWeight = FontWeight.Black)
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
private fun LorePhotoPicker(
    photoUri: Uri?,
    onPhotoSelected: (Uri?) -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> onPhotoSelected(uri) }

    if (photoUri != null) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, QuestGreen, RoundedCornerShape(8.dp))
                .clickable { launcher.launch("image/*") }
        ) {
            AsyncImage(
                model = photoUri,
                contentDescription = "Selected photo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Photo attached",
                tint = QuestGreen,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(20.dp)
            )
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, BrutalSubtle, RoundedCornerShape(8.dp))
                .background(BrutalSurfaceVariant)
                .clickable { launcher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Photo,
                    contentDescription = "Add photo",
                    tint = BrutalMuted,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "TAP TO ATTACH PHOTO",
                    style = MaterialTheme.typography.labelMedium,
                    color = BrutalMuted
                )
            }
        }
    }
}

@Composable
private fun LoreLocationCapture(
    captured: Boolean,
    loading: Boolean,
    onLocationCaptured: (Double, Double) -> Unit,
    onLocationLoading: (Boolean) -> Unit
) {
    val context = LocalContext.current
    var permissionGranted by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (permissionGranted) {
            captureCurrentLocation(context, onLocationCaptured, onLocationLoading)
        }
    }

    if (captured) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, QuestGreen, RoundedCornerShape(8.dp))
                .background(BrutalSurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = QuestGreen, modifier = Modifier.size(20.dp))
                Text("LOCATION CAPTURED", style = MaterialTheme.typography.labelMedium, color = QuestGreen)
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, BrutalSubtle, RoundedCornerShape(8.dp))
                .background(BrutalSurfaceVariant)
                .clickable(enabled = !loading) {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = BrutalAccent)
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = BrutalMuted, modifier = Modifier.size(20.dp))
                    Text("TAP TO CAPTURE LOCATION", style = MaterialTheme.typography.labelMedium, color = BrutalMuted)
                }
            }
        }
    }
}

@Suppress("MissingPermission")
private fun captureCurrentLocation(
    context: android.content.Context,
    onLocationCaptured: (Double, Double) -> Unit,
    onLocationLoading: (Boolean) -> Unit
) {
    onLocationLoading(true)
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val cancellationToken = CancellationTokenSource()
    fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationToken.token)
        .addOnSuccessListener { location ->
            if (location != null) onLocationCaptured(location.latitude, location.longitude)
            else onLocationLoading(false)
        }
        .addOnFailureListener { onLocationLoading(false) }
}

@Composable
private fun loreTextFieldColors() = OutlinedTextFieldDefaults.colors(
    unfocusedContainerColor = BrutalSurfaceVariant,
    focusedContainerColor = BrutalSurfaceVariant,
    unfocusedBorderColor = BrutalSubtle,
    focusedBorderColor = BrutalAccent,
    cursorColor = BrutalAccent,
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White
)
