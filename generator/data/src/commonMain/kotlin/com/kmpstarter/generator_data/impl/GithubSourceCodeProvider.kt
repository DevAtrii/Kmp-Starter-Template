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

package com.kmpstarter.generator_data.impl

import com.kmpstarter.generator_data.interfaces.SourceCode
import com.kmpstarter.generator_data.interfaces.StarterProjectFileManager
import com.kmpstarter.generator_data.interfaces.StarterProjectSourceCodeProvider
import com.kmpstarter.generator_data.interfaces.StarterProjectSourceCodeProvider.Companion.MAX_VERSION
import com.kmpstarter.generator_data.interfaces.StarterProjectSourceCodeProvider.Companion.MIN_VERSION
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsBytes
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Fetches starter source code from GitHub releases of
 * [DevAtrii/Kmp-Starter-Template](https://github.com/DevAtrii/Kmp-Starter-Template).
 *
 * When [version] is provided, fetches that exact release.
 * Otherwise selects the newest release within [MIN_VERSION]..[MAX_VERSION]. If a newer release
 * exists beyond [MAX_VERSION], logs a yellow warning that the CLI only supports up to [MAX_VERSION].
 *
 * GitHub zipballs nest content under a single root folder (`repo.zip/repo-sha/...`).
 * This provider normalizes that to root entries (`repo.zip/...`) via [StarterProjectFileManager].
 */
class GithubSourceCodeProvider(
    private val fileManager: StarterProjectFileManager,
    private val httpClient: HttpClient = defaultHttpClient(),
    private val owner: String = DEFAULT_OWNER,
    private val repo: String = DEFAULT_REPO,
) : StarterProjectSourceCodeProvider {

    override suspend fun getSourceCode(version: String?): Result<SourceCode> = runCatching {
        val releases = fetchReleases()
        val versioned = releases.mapNotNull { release ->
            val parsed = SemanticVersion.parse(release.tagName) ?: return@mapNotNull null
            parsed to release
        }

        if (versioned.isEmpty()) {
            error("No semantic-versioned releases found for $owner/$repo")
        }

        val selected = if (version != null) {
            selectExactVersion(versioned = versioned, requestedVersion = version)
        } else {
            selectSupportedVersion(versioned = versioned)
        }

        val zipBytes = downloadZip(selected.second.zipballUrl)
        val normalizedZip = normalizeZipRoot(version = selected.first.toString(), zipBytes = zipBytes)

        SourceCode(
            version = selected.first.toString(),
            content = normalizedZip,
        )
    }

    private fun selectExactVersion(
        versioned: List<Pair<SemanticVersion, GitHubRelease>>,
        requestedVersion: String,
    ): Pair<SemanticVersion, GitHubRelease> {
        val target = SemanticVersion.parse(requestedVersion)
            ?: error("Invalid version: $requestedVersion")

        return versioned.find { it.first == target }
            ?: error("No GitHub release found for version $requestedVersion ($owner/$repo)")
    }

    private fun selectSupportedVersion(
        versioned: List<Pair<SemanticVersion, GitHubRelease>>,
    ): Pair<SemanticVersion, GitHubRelease> {
        val minVersion = SemanticVersion.parse(MIN_VERSION)
            ?: error("Invalid MIN_VERSION: $MIN_VERSION")
        val maxVersion = SemanticVersion.parse(MAX_VERSION)
            ?: error("Invalid MAX_VERSION: $MAX_VERSION")

        val newestAvailable = versioned.maxBy { it.first }
        val supported = versioned.filter { (parsed, _) ->
            parsed in minVersion..maxVersion
        }

        val selected = supported.maxByOrNull { it.first }
            ?: error(
                "No GitHub release found between $MIN_VERSION and $MAX_VERSION for $owner/$repo",
            )

        if (newestAvailable.first > maxVersion) {
            logYellow(
                "A newer starter release is available (${newestAvailable.first}), " +
                    "but this CLI supports up to $MAX_VERSION. Using ${selected.first}.",
            )
        }

        return selected
    }

    private suspend fun fetchReleases(): List<GitHubRelease> {
        val response = httpClient.get("$GITHUB_API/repos/$owner/$repo/releases") {
            header(HttpHeaders.Accept, "application/vnd.github+json")
            header("X-GitHub-Api-Version", "2022-11-28")
        }
        if (!response.status.isSuccess()) {
            error("GitHub releases request failed: HTTP ${response.status.value}")
        }
        return response.body<List<GitHubRelease>>()
            .filter { !it.draft && !it.prerelease }
    }

    private suspend fun downloadZip(zipballUrl: String): ByteArray {
        val response = httpClient.get(zipballUrl) {
            header(HttpHeaders.Accept, "application/vnd.github+json")
            header("X-GitHub-Api-Version", "2022-11-28")
        }
        if (!response.status.isSuccess()) {
            error("GitHub zipball download failed: HTTP ${response.status.value}")
        }
        return response.bodyAsBytes()
    }

    /**
     * GitHub zipballs look like `zip/Repo-sha/...`. Re-zip so entries are at the zip root.
     */
    private suspend fun normalizeZipRoot(version: String, zipBytes: ByteArray): ByteArray {
        val workDir = "${fileManager.getCurrentDir()}/.starter/github_source/$version"
        val zipPath = "$workDir/source.zip"
        val extractDir = "$workDir/extracted"

        try {
            fileManager.delete(workDir)
            fileManager.writeFile(path = zipPath, content = zipBytes).getOrThrow()
            fileManager.extractZip(path = zipPath, output = extractDir).getOrThrow()

            val contentRoot = resolveContentRoot(extractDir)
            return fileManager.createZip(path = contentRoot).getOrThrow()
        } finally {
            fileManager.delete(workDir)
        }
    }

    private suspend fun resolveContentRoot(extractDir: String): String {
        val normalizedExtract = extractDir.trimEnd('/')
        val topLevelDirs = fileManager.getDirectoriesRecursively(extractDir)
            .filter { dir ->
                val relative = dir.removePrefix(normalizedExtract).trimStart('/')
                relative.isNotEmpty() && !relative.contains('/')
            }

        return if (topLevelDirs.size == 1) topLevelDirs.single() else extractDir
    }

    private fun logYellow(message: String) {
        println("\u001B[33m$message\u001B[0m")
    }

    companion object {
        const val DEFAULT_OWNER = "DevAtrii"
        const val DEFAULT_REPO = "Kmp-Starter-Template"
        private const val GITHUB_API = "https://api.github.com"

        fun defaultHttpClient(): HttpClient = HttpClient {
            expectSuccess = false
            followRedirects = true
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    },
                )
            }
        }
    }
}
