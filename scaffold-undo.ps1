# ============================================================
# scaffold-undo.ps1 - Remove boilerplate created by scaffold.ps1
#
# Usage:
#   .\scaffold-undo.ps1 -FeatureName Dashboard
#   .\scaffold-undo.ps1 -FeatureName Checkout
#
# Deletes:
#   src/test/java/com/automation/pages/<Name>Page.java
#   src/test/java/com/automation/steps/<Name>Steps.java
#   src/test/resources/features/<name>.feature
#   Removes the placeholder block from locators.properties
# ============================================================

param(
    [Parameter(Mandatory=$true)]
    [string]$FeatureName
)

$packageBase  = "src/test/java/com/automation"
$resourceBase = "src/test/resources"
$name         = $FeatureName
$nameLower    = $FeatureName.ToLower()

$pagePath     = "$packageBase/pages/${name}Page.java"
$stepsPath    = "$packageBase/steps/${name}Steps.java"
$featurePath  = "$resourceBase/features/${nameLower}.feature"
$locatorsPath = "$resourceBase/locators.properties"

$anyFound = $false

# ── Delete Page class ─────────────────────────────────────────
if (Test-Path $pagePath) {
    Remove-Item $pagePath -Force
    Write-Host "Deleted: $pagePath" -ForegroundColor Green
    $anyFound = $true
} else {
    Write-Host "Not found (skipped): $pagePath" -ForegroundColor Yellow
}

# ── Delete Steps class ────────────────────────────────────────
if (Test-Path $stepsPath) {
    Remove-Item $stepsPath -Force
    Write-Host "Deleted: $stepsPath" -ForegroundColor Green
    $anyFound = $true
} else {
    Write-Host "Not found (skipped): $stepsPath" -ForegroundColor Yellow
}

# ── Delete feature file ───────────────────────────────────────
if (Test-Path $featurePath) {
    Remove-Item $featurePath -Force
    Write-Host "Deleted: $featurePath" -ForegroundColor Green
    $anyFound = $true
} else {
    Write-Host "Not found (skipped): $featurePath" -ForegroundColor Yellow
}

# ── Remove locators.properties block ─────────────────────────
if (Test-Path $locatorsPath) {
    $lines        = Get-Content $locatorsPath
    $blockStart   = "# $name Page"
    $inBlock      = $false
    $filtered     = [System.Collections.Generic.List[string]]::new()
    $blockRemoved = $false

    foreach ($line in $lines) {
        if ($line.Trim() -eq $blockStart) {
            $inBlock      = $true
            $blockRemoved = $true
            # Also drop the blank line that immediately preceded the block
            if ($filtered.Count -gt 0 -and $filtered[-1].Trim() -eq "") {
                $filtered.RemoveAt($filtered.Count - 1)
            }
            continue
        }
        if ($inBlock) {
            # Block ends at the next non-comment, non-blank line that doesn't
            # belong to this feature (i.e. a new section or real property)
            $isBlockLine = ($line.Trim() -eq "") -or
                           ($line.Trim().StartsWith("# $nameLower.")) -or
                           ($line.Trim().StartsWith("# TODO:")) -or
                           ($line.Trim().StartsWith("$nameLower."))
            if ($isBlockLine) { continue }
            $inBlock = $false
        }
        $filtered.Add($line)
    }

    if ($blockRemoved) {
        Set-Content $locatorsPath $filtered
        Write-Host "Removed '$name Page' block from: $locatorsPath" -ForegroundColor Green
        $anyFound = $true
    } else {
        Write-Host "No '$name Page' block found in: $locatorsPath (skipped)" -ForegroundColor Yellow
    }
}

Write-Host ""
if ($anyFound) {
    Write-Host "Undo complete for feature: $name" -ForegroundColor Green
    Write-Host ""
    Write-Host "Reminder: if you added a '$name' sheet to testdata.xlsx, remove that manually." -ForegroundColor Yellow
} else {
    Write-Host "Nothing to undo - no files found for feature: $name" -ForegroundColor Red
}
