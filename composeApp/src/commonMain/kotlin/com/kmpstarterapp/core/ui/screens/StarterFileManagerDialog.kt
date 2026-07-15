/*
 *
 *  *
 *  *  * Copyright (c) 2026
 *  *  *
 *  *  * Author: Athar Gul
 *  *  * GitHub: https://github.com/DevAtrii/Kmp-Starter-Template
 *  *  * YouTube: https://www.youtube.com/@devatrii/videos
 *  *  *
 *  *  * All rights reserved.
 *  *
 *  *
 *
 */

package com.kmpstarterapp.core.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmpstarter.ui_utils.popups.dialogs.BaseDialog
import com.kmpstarter.ui_utils.files.rememberStarterFileManager
import com.kmpstarter.ui_utils.theme.Dimens
import com.kmpstarter.utils.files.StarterFile
import com.kmpstarter.utils.starter.ExperimentalStarterApi
import kotlinx.coroutines.launch

private const val SAMPLE_FILE_NAME = "starter_sample"
private const val SAMPLE_EXTENSION = "txt"
private const val SAMPLE_MIME_TYPE = "text/plain"
private const val SAMPLE_FOLDER = "KmpStarter"
private const val SAMPLE_REPEAT_COUNT = 1000
private const val SAMPLE_TEXT = "Starter Template"

@OptIn(ExperimentalStarterApi::class, ExperimentalLayoutApi::class)
@Composable
fun StarterFileManagerDialog(
    onDismiss: () -> Unit,
) {
    val starterFileManager = rememberStarterFileManager()
    val scope = rememberCoroutineScope()
    val logs = remember { mutableListOf<String>().toMutableStateList() }
    val listState = rememberLazyListState()
    val sampleContent = remember {
        SAMPLE_TEXT.repeat(SAMPLE_REPEAT_COUNT).encodeToByteArray()
    }

    fun appendLog(message: String) {
        val line = "[${logs.size + 1}] $message"
        logs.add(line)
        scope.launch {
            if (logs.isNotEmpty()) {
                listState.animateScrollToItem(logs.lastIndex)
            }
        }
    }

    fun logResult(operation: String, result: Result<*>) {
        result.fold(
            onSuccess = { value ->
                when (value) {
                    is List<*> -> {
                        val files = value.filterIsInstance<StarterFile>()
                        appendLog("$operation → success (${files.size} file(s))")
                        files.forEach { file ->
                            appendLog(
                                "  • ${file.name}.${file.extension} | " +
                                    "size=${file.sizeBytes ?: "?"}B | path=${file.path}",
                            )
                        }
                        if (files.isEmpty()) {
                            appendLog("  (empty)")
                        }
                    }

                    is Pair<*, *> -> {
                        val file = value.first as? StarterFile
                        val content = value.second as? ByteArray
                        if (file != null && content != null) {
                            appendLog(
                                "$operation → success (${content.size} bytes read from ${file.name}.${file.extension})",
                            )
                            appendLog("  path=${file.path}")
                            appendLog("  preview: ${content.decodeToString().take(80)}...")
                        } else {
                            appendLog("$operation → success")
                        }
                    }

                    is ByteArray -> {
                        appendLog("$operation → success (${value.size} bytes read)")
                        appendLog("  preview: ${value.decodeToString().take(80)}...")
                    }

                    else -> appendLog("$operation → success")
                }
            },
            onFailure = { error ->
                appendLog("$operation → failure: ${error.message ?: error::class.simpleName}")
            },
        )
    }

    fun runOperation(operation: String, block: suspend () -> Result<*>) {
        scope.launch {
            appendLog("$operation → started")
            logResult(operation, block())
        }
    }

    BaseDialog(onDismiss = onDismiss) {
        Column(
            modifier = Modifier.padding(Dimens.paddingLarge),
            verticalArrangement = Arrangement.spacedBy(Dimens.paddingMedium),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "StarterFileManager",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Payload: \"$SAMPLE_TEXT\" × $SAMPLE_REPEAT_COUNT (${sampleContent.size} bytes)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.paddingSmall),
                verticalArrangement = Arrangement.spacedBy(Dimens.paddingSmall),
            ) {
                FilledTonalButton(
                    onClick = {
                        runOperation("saveFileIn") {
                            starterFileManager.saveFileIn(
                                suggestedName = SAMPLE_FILE_NAME,
                                extension = SAMPLE_EXTENSION,
                                content = sampleContent,
                                mimeType = SAMPLE_MIME_TYPE,
                            )
                        }
                    },
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                    Text("Save As")
                }

                FilledTonalButton(
                    onClick = {
                        runOperation("saveFileIntoDownloads") {
                            starterFileManager.saveFileIntoDownloads(
                                file = SAMPLE_FILE_NAME,
                                folderPath = SAMPLE_FOLDER,
                                extension = SAMPLE_EXTENSION,
                                content = sampleContent,
                                mimeType = SAMPLE_MIME_TYPE,
                            )
                        }
                    },
                ) {
                    Icon(Icons.Default.SaveAlt, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                    Text("Downloads")
                }

                OutlinedButton(
                    onClick = {
                        runOperation("getFilesFromDownloads(root)") {
                            starterFileManager.getFilesFromDownloads(path = null)
                        }
                    },
                ) {
                    Text("List DL Root")
                }

                OutlinedButton(
                    onClick = {
                        runOperation("getFilesFromDownloads($SAMPLE_FOLDER)") {
                            starterFileManager.getFilesFromDownloads(path = SAMPLE_FOLDER)
                        }
                    },
                ) {
                    Text("List DL Folder")
                }

                OutlinedButton(
                    onClick = {
                        runOperation("readFromDownloads") {
                            starterFileManager.getFilesFromDownloads(path = SAMPLE_FOLDER).fold(
                                onSuccess = { files ->
                                    val target = files.firstOrNull {
                                        it.name == SAMPLE_FILE_NAME && it.extension == SAMPLE_EXTENSION
                                    } ?: return@fold Result.failure(
                                        IllegalStateException("Sample file not found in Downloads/$SAMPLE_FOLDER"),
                                    )
                                    starterFileManager.readFromDownloads(path = target.path)
                                },
                                onFailure = { Result.failure(it) },
                            )
                        }
                    },
                ) {
                    Text("Read DL")
                }

                FilledTonalButton(
                    onClick = {
                        runOperation("saveInCache") {
                            starterFileManager.saveInCache(
                                file = SAMPLE_FILE_NAME,
                                folderPath = SAMPLE_FOLDER,
                                extension = SAMPLE_EXTENSION,
                                content = sampleContent,
                                mimeType = SAMPLE_MIME_TYPE,
                            )
                        }
                    },
                ) {
                    Icon(Icons.Default.Storage, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                    Text("Cache Save")
                }

                OutlinedButton(
                    onClick = {
                        runOperation("getFilesFromCache($SAMPLE_FOLDER)") {
                            starterFileManager.getFilesFromCache(path = SAMPLE_FOLDER)
                        }
                    },
                ) {
                    Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                    Text("List Cache")
                }

                OutlinedButton(
                    onClick = {
                        runOperation("readFromCache") {
                            starterFileManager.readFromCache(
                                path = "$SAMPLE_FOLDER/$SAMPLE_FILE_NAME.$SAMPLE_EXTENSION",
                            )
                        }
                    },
                ) {
                    Text("Read Cache")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.paddingSmall),
            ) {
                OutlinedButton(
                    onClick = {
                        logs.clear()
                        appendLog("logs cleared")
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                    Text("Clear Logs")
                }
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
                ) {
                    Text("Close")
                }
            }

            Text(
                text = "terminal",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(
                        color = Color(0xFF0D1117),
                        shape = RoundedCornerShape(12.dp),
                    )
                    .padding(Dimens.paddingMedium),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                if (logs.isEmpty()) {
                    item {
                        Text(
                            text = "$ starter-file-manager --ready",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = Color(0xFF3FB950),
                        )
                    }
                    item {
                        Text(
                            text = "> tap an operation to run StarterFileManager APIs",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = Color(0xFF8B949E),
                        )
                    }
                } else {
                    itemsIndexed(logs) { _, line ->
                        val color = when {
                            "failure" in line -> Color(0xFFFF7B72)
                            "success" in line -> Color(0xFF3FB950)
                            "started" in line -> Color(0xFF79C0FF)
                            else -> Color(0xFFC9D1D9)
                        }
                        Text(
                            text = line,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            lineHeight = 14.sp,
                            color = color,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.paddingSmall))
        }
    }
}
