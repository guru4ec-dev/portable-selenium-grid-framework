# ============================================================
# scaffold.ps1 - Generate boilerplate for a new feature
#
# Usage:
#   .\scaffold.ps1 -FeatureName Dashboard
#   .\scaffold.ps1 -FeatureName Checkout
#
# Creates:
#   src/test/java/com/automation/pages/<Name>Page.java
#   src/test/java/com/automation/steps/<Name>Steps.java
#   src/test/resources/features/<name>.feature
#   Adds placeholder section to locators.properties
# ============================================================

param(
    [Parameter(Mandatory=$true)]
    [string]$FeatureName
)

$packageBase  = "src/test/java/com/automation"
$resourceBase = "src/test/resources"
$name         = $FeatureName                          # e.g. Dashboard
$nameLower    = $FeatureName.ToLower()               # e.g. dashboard

# ── Paths ────────────────────────────────────────────────────
$pagePath    = "$packageBase/pages/${name}Page.java"
$stepsPath   = "$packageBase/steps/${name}Steps.java"
$featurePath = "$resourceBase/features/${nameLower}.feature"
$locatorsPath = "$resourceBase/locators.properties"

# ── Guard: skip if files already exist ───────────────────────
foreach ($path in @($pagePath, $stepsPath, $featurePath)) {
    if (Test-Path $path) {
        Write-Host "ERROR: $path already exists. Aborting." -ForegroundColor Red
        exit 1
    }
}

# ── Page class ───────────────────────────────────────────────
$pageContent = @"
package com.automation.pages;

import org.openqa.selenium.WebDriver;

public class ${name}Page extends BasePage {

    public ${name}Page(WebDriver driver) { super(driver); }

    public void open() { open("${nameLower}.url"); }

    // TODO: add page action methods using BasePage keywords:
    // type("${nameLower}.fieldName", value)
    // click("${nameLower}.buttonName")
    // getText("${nameLower}.elementName")
    // isDisplayed("${nameLower}.elementName")
}
"@

# ── Steps class ──────────────────────────────────────────────
$stepsContent = @"
package com.automation.steps;

import io.cucumber.java.en.*;

import com.automation.core.DriverManager;
import com.automation.pages.${name}Page;
import com.automation.utils.ExcelUtils;

import org.testng.Assert;

import java.util.Map;

public class ${name}Steps {

    private static final String DATA_FILE  = "testdata/testdata.xlsx";
    private static final String SHEET_NAME = "${name}";  // must match sheet name in testdata.xlsx

    @Given("user is on ${nameLower} page")
    public void open${name}() {
        new ${name}Page(DriverManager.getDriver()).open();
    }

    @When("user performs ${nameLower} action with test case {string}")
    public void perform${name}Action(String testCaseId) {
        Map<String, String> data = ExcelUtils.getTestDataById(DATA_FILE, SHEET_NAME, testCaseId);
        // TODO: call page methods with data values
        // new ${name}Page(DriverManager.getDriver()).someAction(data.get("ColumnName"));
    }

    @Then("${nameLower} result should be {string}")
    public void verify${name}Result(String expectedResult) {
        // TODO: add assertion
        // String actual = new ${name}Page(DriverManager.getDriver()).getResult();
        // Assert.assertEquals(actual, expectedResult);
    }
}
"@

# ── Feature file ─────────────────────────────────────────────
$featureContent = @"
Feature: ${name} functionality

  Scenario Outline: ${name} - <testCaseId>
    Given user is on ${nameLower} page
    When user performs ${nameLower} action with test case "<testCaseId>"
    Then ${nameLower} result should be "<expectedResult>"

    Examples:
      | testCaseId    | expectedResult |
      | TestCase001   | expected       |
"@

# ── locators.properties entry ────────────────────────────────
$locatorEntry = @"

# ${name} Page
${nameLower}.url = https://your-app.com/${nameLower}
# TODO: add locators below (format: ${nameLower}.elementName = locatorType:locatorValue)
# ${nameLower}.someField = id:field-id
# ${nameLower}.someButton = css:.button-class
"@

# ── Write files ───────────────────────────────────────────────
New-Item -ItemType File -Path $pagePath    -Force | Out-Null ; Set-Content $pagePath    $pageContent
New-Item -ItemType File -Path $stepsPath   -Force | Out-Null ; Set-Content $stepsPath   $stepsContent
New-Item -ItemType File -Path $featurePath -Force | Out-Null ; Set-Content $featurePath $featureContent
Add-Content -Path $locatorsPath -Value $locatorEntry

Write-Host ""
Write-Host "Scaffolding complete for feature: $name" -ForegroundColor Green
Write-Host ""
Write-Host "Files created:" -ForegroundColor Cyan
Write-Host "  $pagePath"
Write-Host "  $stepsPath"
Write-Host "  $featurePath"
Write-Host ""
Write-Host "Next steps:" -ForegroundColor Yellow
Write-Host "  1. Fill in locators in $locatorsPath"
Write-Host "  2. Add '$name' sheet to src/test/resources/testdata/testdata.xlsx"
Write-Host "  3. Add page actions in ${name}Page.java"
Write-Host "  4. Add assertions in ${name}Steps.java"
Write-Host "  5. Update testCaseId values in ${nameLower}.feature"
