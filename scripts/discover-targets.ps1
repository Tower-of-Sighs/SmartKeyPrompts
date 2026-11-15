[CmdletBinding()]
param(
    [string] $TargetsRoot = (Join-Path (Split-Path -Parent $PSScriptRoot) 'targets'),

    [AllowEmptyCollection()]
    [string[]] $ChangedFiles
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function Read-CiProperties {
    param([Parameter(Mandatory = $true)][string] $Path)

    $properties = @{}
    $lineNumber = 0
    foreach ($line in Get-Content -LiteralPath $Path) {
        $lineNumber++
        $trimmed = $line.Trim()
        if ($trimmed.Length -eq 0 -or $trimmed.StartsWith('#') -or $trimmed.StartsWith(';')) {
            continue
        }

        $separator = $trimmed.IndexOf('=')
        if ($separator -lt 1) {
            throw "Invalid ci.properties entry at ${Path}:$lineNumber. Use key=value."
        }

        $key = $trimmed.Substring(0, $separator).Trim()
        $value = $trimmed.Substring($separator + 1).Trim()
        if ($properties.ContainsKey($key)) {
            throw "Duplicate ci.properties key '$key' in $Path"
        }
        $properties[$key] = $value
    }

    return $properties
}

if (-not (Test-Path -LiteralPath $TargetsRoot -PathType Container)) {
    throw "Targets directory not found: $TargetsRoot"
}

$entries = @()
foreach ($directory in Get-ChildItem -LiteralPath $TargetsRoot -Directory | Sort-Object Name) {
    $descriptor = Join-Path $directory.FullName 'ci.properties'
    $wrapper = Join-Path $directory.FullName 'gradlew.bat'

    if (-not (Test-Path -LiteralPath $descriptor -PathType Leaf)) {
        throw "Target '$($directory.Name)' is missing required ci.properties"
    }
    if (-not (Test-Path -LiteralPath $wrapper -PathType Leaf)) {
        throw "Target '$($directory.Name)' is missing gradlew.bat"
    }

    $properties = Read-CiProperties -Path $descriptor
    $enabled = if ($properties.ContainsKey('ci.enabled')) { $properties['ci.enabled'].ToLowerInvariant() } else { 'true' }
    if ($enabled -notin @('true', 'false')) {
        throw "Target '$($directory.Name)' has invalid ci.enabled '$enabled'; use true or false"
    }
    if ($enabled -eq 'false') {
        Write-Host "Skipping disabled target: $($directory.Name)"
        continue
    }

    $javaVersion = 0
    if (-not $properties.ContainsKey('ci.java') -or
        $properties['ci.java'] -notmatch '^[1-9][0-9]*$' -or
        -not [int]::TryParse($properties['ci.java'], [ref] $javaVersion)) {
        throw "Target '$($directory.Name)' requires a positive integer ci.java value"
    }

    $attempts = 3
    if ($properties.ContainsKey('ci.attempts')) {
        if ($properties['ci.attempts'] -notmatch '^[1-5]$') {
            throw "Target '$($directory.Name)' has invalid ci.attempts '$($properties['ci.attempts'])'; use 1 through 5"
        }
        $attempts = [int] $properties['ci.attempts']
    }

    $entries += [PSCustomObject]@{
        target = $directory.Name
        java = $properties['ci.java']
        attempts = $attempts
    }
}

if ($entries.Count -eq 0) {
    throw 'No enabled targets found. Set ci.enabled=true for at least one target.'
}

if ($PSBoundParameters.ContainsKey('ChangedFiles')) {
    $sharedChanges = @($ChangedFiles | Where-Object {
        $_ -notmatch '^targets/' -or $_ -match '^targets/[^/]+/ci\.properties$'
    })
    if ($sharedChanges.Count -eq 0) {
        $changedTargets = @($ChangedFiles | ForEach-Object {
            if ($_ -match '^targets/([^/]+)/') { $Matches[1] }
        } | Sort-Object -Unique)
        $entries = @($entries | Where-Object { $_.target -in $changedTargets })
    }
}

@{ include = @($entries) } | ConvertTo-Json -Compress -Depth 3
