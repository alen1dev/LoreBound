package com.pauls.lorebound.ui.quest

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import coil.compose.AsyncImage
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.pauls.lorebound.domain.model.Quest
import com.pauls.lorebound.ui.theme.BrutalAccent
import com.pauls.lorebound.ui.theme.BrutalMuted
import com.pauls.lorebound.ui.theme.BrutalSubtle
import com.pauls.lorebound.ui.theme.BrutalSurfaceVariant
import com.pauls.lorebound.ui.theme.QuestGreen

@Composable
fun QuestCompletionDialog(
    quest: Quest,
    state: QuestDetailState,
    onDismiss: () -> Unit,
    onConquer: () -> Unit,
    onLoreNotesChange: (String) -> Unit,
    onPhotoSelected: (Uri?) -> Unit,
    onLinkChange: (String) -> Unit,
    onLocationCaptured: (Double, Double) -> Unit,
    onLocationLoading: (Boolean) -> Unit
) {
    val vType = quest.verificationType

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BrutalSurfaceVariant,
        titleContentColor = Color.White,
        textContentColor = BrutalMuted,
        title = {
            Text(
                text = "CONQUER THIS QUEST",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                // Verification requirement label
                if (!vType.isOptional) {
                    Text(
                        text = vType.requirementLabel.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = BrutalAccent
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Validation error
                state.validationError?.let { error ->
                    Text(
                        text = error,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // ── PHOTO INPUT ──────────────────────────
                if (vType.requiresPhoto) {
                    PhotoPickerSection(
                        photoUri = state.photoUri,
                        onPhotoSelected = onPhotoSelected,
                        required = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // ── GPS INPUT ────────────────────────────
                if (vType.requiresGps) {
                    LocationCaptureSection(
                        captured = state.locationCaptured,
                        loading = state.locationLoading,
                        onLocationCaptured = onLocationCaptured,
                        onLocationLoading = onLocationLoading
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // ── LINK INPUT ───────────────────────────
                if (vType.requiresLink) {
                    OutlinedTextField(
                        value = state.link,
                        onValueChange = onLinkChange,
                        label = { Text("LINK (REQUIRED)", color = BrutalMuted) },
                        placeholder = { Text("https://...", color = BrutalMuted) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // ── TEXT / LORE ENTRY ────────────────────
                val isTextRequired = vType.requiresText
                val showPhotoOption = vType == com.pauls.lorebound.domain.model.VerificationType.PHOTO_OR_TEXT

                if (showPhotoOption && !vType.requiresPhoto) {
                    // For PHOTO_OR_TEXT — show photo picker if not already shown above
                    PhotoPickerSection(
                        photoUri = state.photoUri,
                        onPhotoSelected = onPhotoSelected,
                        required = false
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "— OR —",
                        style = MaterialTheme.typography.labelSmall,
                        color = BrutalMuted,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                OutlinedTextField(
                    value = state.loreNotes,
                    onValueChange = onLoreNotesChange,
                    label = {
                        Text(
                            if (isTextRequired) "YOUR LORE ENTRY (REQUIRED)"
                            else "YOUR LORE ENTRY (OPTIONAL)",
                            color = BrutalMuted
                        )
                    },
                    placeholder = { Text("Record your adventure...", color = BrutalMuted) },
                    minLines = 3,
                    maxLines = 6,
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConquer,
                enabled = !state.isSubmitting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(0.dp)
            ) {
                if (state.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = Color.Black
                    )
                } else {
                    Text("CONQUER", fontWeight = FontWeight.Black)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = BrutalMuted)
            }
        }
    )
}

@Composable
private fun PhotoPickerSection(
    photoUri: Uri?,
    onPhotoSelected: (Uri?) -> Unit,
    required: Boolean
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onPhotoSelected(uri)
    }

    Column {
        Text(
            text = if (required) "PHOTO ${if (photoUri != null) "✓" else "(REQUIRED)"}"
            else "PHOTO (OPTIONAL)",
            style = MaterialTheme.typography.labelSmall,
            color = if (photoUri != null) QuestGreen else BrutalMuted
        )
        Spacer(modifier = Modifier.height(6.dp))

        if (photoUri != null) {
            // Show selected photo thumbnail
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, QuestGreen, RoundedCornerShape(8.dp))
                    .clickable { photoPickerLauncher.launch("image/*") }
            ) {
                AsyncImage(
                    model = photoUri,
                    contentDescription = "Selected photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                // Checkmark overlay
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
            // Photo picker button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, BrutalSubtle, RoundedCornerShape(8.dp))
                    .background(Color.Black)
                    .clickable { photoPickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Photo,
                        contentDescription = "Pick photo",
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
}

@Composable
private fun LocationCaptureSection(
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
            captureLocation(context, onLocationCaptured, onLocationLoading)
        }
    }

    Column {
        Text(
            text = if (captured) "LOCATION ✓" else "LOCATION (REQUIRED)",
            style = MaterialTheme.typography.labelSmall,
            color = if (captured) QuestGreen else BrutalMuted
        )
        Spacer(modifier = Modifier.height(6.dp))

        if (captured) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, QuestGreen, RoundedCornerShape(8.dp))
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Location captured",
                        tint = QuestGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "LOCATION CAPTURED",
                        style = MaterialTheme.typography.labelMedium,
                        color = QuestGreen
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, BrutalSubtle, RoundedCornerShape(8.dp))
                    .background(Color.Black)
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
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = BrutalAccent
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Capture location",
                            tint = BrutalMuted,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "TAP TO CHECK IN",
                            style = MaterialTheme.typography.labelMedium,
                            color = BrutalMuted
                        )
                    }
                }
            }
        }
    }
}

@Suppress("MissingPermission")
private fun captureLocation(
    context: android.content.Context,
    onLocationCaptured: (Double, Double) -> Unit,
    onLocationLoading: (Boolean) -> Unit
) {
    onLocationLoading(true)
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val cancellationToken = CancellationTokenSource()

    fusedLocationClient.getCurrentLocation(
        Priority.PRIORITY_HIGH_ACCURACY,
        cancellationToken.token
    ).addOnSuccessListener { location ->
        if (location != null) {
            onLocationCaptured(location.latitude, location.longitude)
        } else {
            onLocationLoading(false)
        }
    }.addOnFailureListener {
        onLocationLoading(false)
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    unfocusedContainerColor = Color.Black,
    focusedContainerColor = Color.Black,
    unfocusedBorderColor = BrutalSubtle,
    focusedBorderColor = BrutalAccent,
    cursorColor = BrutalAccent,
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White
)

