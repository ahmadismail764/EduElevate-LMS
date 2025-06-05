# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------

# ----------------------------------------------------------------------------
# Apache Maven Wrapper startup PowerShell script, version 3.3.2
#
# Optional ENV vars
#   MVNW_REPOURL - repo url base for downloading maven distribution
#   MVNW_USERNAME/MVNW_PASSWORD - user and password for downloading maven
#   MVNW_VERBOSE - true: enable verbose log; others: silence the output
# ----------------------------------------------------------------------------

param(
    [Parameter(ValueFromRemainingArguments=$true)]
    [string[]]$MavenArgs
)

$ErrorActionPreference = "Stop"

if ($env:MVNW_VERBOSE -eq "true") {
    $VerbosePreference = "Continue"
}

# Get script directory
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$script = Split-Path -Leaf $MyInvocation.MyCommand.Path

# Calculate distributionUrl, requires .mvn/wrapper/maven-wrapper.properties
$wrapperPropertiesPath = Join-Path $scriptDir ".mvn/wrapper/maven-wrapper.properties"
if (!(Test-Path $wrapperPropertiesPath)) {
    Write-Error "Maven wrapper properties file not found: $wrapperPropertiesPath"
}

$properties = Get-Content -Raw $wrapperPropertiesPath | ConvertFrom-StringData
$distributionUrl = $properties.distributionUrl

if (!$distributionUrl) {
    Write-Error "Cannot read distributionUrl property in $wrapperPropertiesPath"
}

# Determine if using Maven Daemon (mvnd) or regular Maven
$distributionUrlName = $distributionUrl -replace '^.*/', ''
switch -wildcard -casesensitive ($distributionUrlName) {
    "maven-mvnd-*" {
        $USE_MVND = $true
        $distributionUrl = $distributionUrl -replace '-bin\.[^.]*$', "-windows-amd64.zip"
        $MVN_CMD = "mvnd.cmd"
        break
    }
    default {
        $USE_MVND = $false
        $MVN_CMD = $script -replace '^mvnw', 'mvn'
        $MVN_CMD = $MVN_CMD -replace '\.ps1$', '.cmd'
        break
    }
}

# Apply MVNW_REPOURL and calculate MAVEN_HOME
if ($env:MVNW_REPOURL) {
    $MVNW_REPO_PATTERN = if ($USE_MVND) { "/org/apache/maven/" } else { "/maven/mvnd/" }
    $distributionUrl = "$env:MVNW_REPOURL$MVNW_REPO_PATTERN$($distributionUrl -replace '^.*' + $MVNW_REPO_PATTERN, '')"
}

$distributionUrlName = $distributionUrl -replace '^.*/', ''
$distributionUrlNameMain = $distributionUrlName -replace '\.[^.]*$', '' -replace '-bin$', ''

# Set MAVEN_HOME_PARENT
$MAVEN_HOME_PARENT = Join-Path $HOME ".m2/wrapper/dists/$distributionUrlNameMain"
if ($env:MAVEN_USER_HOME) {
    $MAVEN_HOME_PARENT = Join-Path $env:MAVEN_USER_HOME "wrapper/dists/$distributionUrlNameMain"
}

# Calculate hash for Maven home directory name
$md5 = [System.Security.Cryptography.MD5]::Create()
$hash = $md5.ComputeHash([System.Text.Encoding]::UTF8.GetBytes($distributionUrl))
$MAVEN_HOME_NAME = ($hash | ForEach-Object { $_.ToString("x2") }) -join ''
$MAVEN_HOME = Join-Path $MAVEN_HOME_PARENT $MAVEN_HOME_NAME

# Check if Maven is already installed
if (Test-Path -Path $MAVEN_HOME -PathType Container) {
    Write-Verbose "Found existing MAVEN_HOME at $MAVEN_HOME"
    $mavenCmd = Join-Path $MAVEN_HOME "bin/$MVN_CMD"
    if (Test-Path $mavenCmd) {
        Write-Verbose "Executing: $mavenCmd $($MavenArgs -join ' ')"
        & $mavenCmd @MavenArgs
        exit $LASTEXITCODE
    }
}

# Validate distribution URL
if (!$distributionUrlNameMain -or ($distributionUrlName -eq $distributionUrlNameMain)) {
    Write-Error "distributionUrl is not valid, must end with *-bin.zip, but found $distributionUrl"
}

# Prepare temporary directory
Write-Verbose "Creating temporary directory for Maven download..."
$TMP_DOWNLOAD_DIR = New-Item -ItemType Directory -Path ([System.IO.Path]::GetTempPath()) -Name "mvnw-$(Get-Random)"

# Cleanup function
$cleanup = {
    if (Test-Path $TMP_DOWNLOAD_DIR) {
        try { 
            Remove-Item $TMP_DOWNLOAD_DIR -Recurse -Force -ErrorAction SilentlyContinue
            Write-Verbose "Cleaned up temporary directory: $TMP_DOWNLOAD_DIR"
        }
        catch { 
            Write-Warning "Cannot remove temporary directory: $TMP_DOWNLOAD_DIR" 
        }
    }
}

# Register cleanup on exit
trap { & $cleanup }

# Create Maven home parent directory
New-Item -ItemType Directory -Path $MAVEN_HOME_PARENT -Force | Out-Null

# Download and Install Apache Maven
Write-Host "Downloading Maven from: $distributionUrl"
Write-Verbose "Downloading to: $TMP_DOWNLOAD_DIR/$distributionUrlName"

try {
    # Create WebClient for download
    $webclient = New-Object System.Net.WebClient
    
    # Set credentials if provided
    if ($env:MVNW_USERNAME -and $env:MVNW_PASSWORD) {
        $credentials = New-Object System.Net.NetworkCredential($env:MVNW_USERNAME, $env:MVNW_PASSWORD)
        $webclient.Credentials = $credentials
    }
    
    # Set security protocol
    [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
    
    # Download the file
    $downloadPath = Join-Path $TMP_DOWNLOAD_DIR $distributionUrlName
    $webclient.DownloadFile($distributionUrl, $downloadPath)
    Write-Host "Download completed successfully"
}
catch {
    Write-Error "Failed to download Maven distribution: $_"
}
finally {
    if ($webclient) { $webclient.Dispose() }
}

# Validate SHA-256 checksum if specified
$distributionSha256Sum = $properties.distributionSha256Sum
if ($distributionSha256Sum) {
    if ($USE_MVND) {
        Write-Error "Checksum validation is not supported for maven-mvnd. Please disable validation by removing 'distributionSha256Sum' from your maven-wrapper.properties."
    }
    
    Write-Verbose "Validating SHA-256 checksum..."
    $fileHash = Get-FileHash $downloadPath -Algorithm SHA256
    if ($fileHash.Hash.ToLower() -ne $distributionSha256Sum.ToLower()) {
        Write-Error "Error: Failed to validate Maven distribution SHA-256. Your Maven distribution might be compromised. If you updated your Maven version, you need to update the specified distributionSha256Sum property."
    }
    Write-Verbose "SHA-256 checksum validation passed"
}

# Extract and install Maven
Write-Host "Extracting Maven distribution..."
try {
    # Extract the archive
    Expand-Archive $downloadPath -DestinationPath $TMP_DOWNLOAD_DIR -Force
    
    # Find the extracted directory
    $extractedDir = Get-ChildItem $TMP_DOWNLOAD_DIR -Directory | Where-Object { $_.Name -like "*$distributionUrlNameMain*" } | Select-Object -First 1
    if (!$extractedDir) {
        Write-Error "Could not find extracted Maven directory"
    }
    
    # Move to final location
    Move-Item -Path $extractedDir.FullName -Destination $MAVEN_HOME -Force
    Write-Host "Maven installed successfully to: $MAVEN_HOME"
}
catch {
    if (!(Test-Path -Path $MAVEN_HOME -PathType Container)) {
        Write-Error "Failed to install Maven: $_"
    }
}

# Cleanup temporary directory
& $cleanup

# Execute Maven with provided arguments
$mavenCmd = Join-Path $MAVEN_HOME "bin/$MVN_CMD"
if (Test-Path $mavenCmd) {
    Write-Verbose "Executing: $mavenCmd $($MavenArgs -join ' ')"
    & $mavenCmd @MavenArgs
    exit $LASTEXITCODE
} else {
    Write-Error "Maven command not found: $mavenCmd"
}
