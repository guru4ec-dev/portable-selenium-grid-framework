# Developer Guide — Adding a New Feature

Follow these 5 steps in order. Use the Login feature as a reference implementation.

---

## Quick Start — Scaffold Script

Run once from the project root to auto-generate all 3 Java/feature files:

```powershell
# First time only — allow PowerShell scripts to run
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser

# Generate boilerplate for a new feature
.\scaffold.ps1 -FeatureName Dashboard
```

Creates:
- `src/test/java/com/automation/pages/DashboardPage.java`
- `src/test/java/com/automation/steps/DashboardSteps.java`
- `src/test/resources/features/dashboard.feature`
- Placeholder section in `locators.properties`

Then complete Steps 1 and 2 below manually (locators + test data).

---

## Step 1: Add Locators to `locators.properties`

File: `src/test/resources/locators.properties`

Use **Playwright Codegen** to find locators from a live URL:
```powershell
npx playwright codegen https://your-app.com/dashboard
```

Add a section for the new page:
```properties
# Dashboard Page
dashboard.searchBox  = id:search-input
dashboard.searchBtn  = css:.search-button
dashboard.results    = css:.results-list
dashboard.url        = https://your-app.com/dashboard
```

**Format:** `pageName.elementName = locatorType:locatorValue`

**Locator type priority (most → least stable):**
```
id > name > css > xpath
```

**Playwright → @FindBy mapping:**

| Playwright Codegen | `locators.properties` type |
|---|---|
| `locator('#username')` | `id:username` |
| `locator('.btn')` | `css:.btn` |
| `locator('[name="email"]')` | `name:email` |
| `getByText('Login')` | `linktext:Login` |
| `locator('//div[@id="x"]')` | `xpath://div[@id="x"]` |

---

## Step 2: Add Test Data to `testdata.xlsx`

File: `src/test/resources/testdata/testdata.xlsx`

1. Open in Excel
2. Add a new sheet named after the feature (e.g. `Dashboard`)
3. First row = column headers — always include `TestCaseId` and `ExpectedResult`
4. Each subsequent row = one test case

**Example — Dashboard sheet:**

| TestCaseId | SearchTerm | ExpectedResult |
|---|---|---|
| ValidSearch | laptop | found |
| EmptySearch | | no-results |

> Sheet name must match `SHEET_NAME` constant in the Steps class exactly.

---

## Step 3: Create a Page Class

File: `src/test/java/com/automation/pages/DashboardPage.java`

**Rules:**
- Extend `BasePage` — inherits `type()`, `click()`, `getText()`, `open()`, `isDisplayed()`, `clear()`
- One method per user action (not per element)
- No Selenium API calls in page classes — use `BasePage` keywords only

```java
public class DashboardPage extends BasePage {

    public DashboardPage(WebDriver driver) { super(driver); }

    public void open()              { open("dashboard.url"); }
    public void search(String term) { type("dashboard.searchBox", term); click("dashboard.searchBtn"); }
    public String getResults()      { return getText("dashboard.results"); }
}
```

**Available BasePage keywords:**

| Keyword | Action |
|---|---|
| `open(urlKey)` | Navigate to URL from locators.properties |
| `type(key, value)` | Type text into a field |
| `click(key)` | Click an element |
| `getText(key)` | Get visible text of an element |
| `isDisplayed(key)` | Returns true if element is visible |
| `clear(key)` | Clear a text field |

---

## Step 4: Create a Steps Class

File: `src/test/java/com/automation/steps/DashboardSteps.java`

**Rules:**
- `DATA_FILE` always points to `testdata/testdata.xlsx`
- `SHEET_NAME` must match the Excel sheet name exactly
- Step text must be business-readable (no technical details)

```java
public class DashboardSteps {

    private static final String DATA_FILE  = "testdata/testdata.xlsx";
    private static final String SHEET_NAME = "Dashboard";  // must match Excel sheet name

    @Given("user is on dashboard page")
    public void openDashboard() {
        new DashboardPage(DriverManager.getDriver()).open();
    }

    @When("user searches with test case {string}")
    public void search(String testCaseId) {
        Map<String, String> data = ExcelUtils.getTestDataById(DATA_FILE, SHEET_NAME, testCaseId);
        new DashboardPage(DriverManager.getDriver()).search(data.get("SearchTerm"));
    }

    @Then("search result should be {string}")
    public void verifyResult(String expectedResult) {
        String actual = new DashboardPage(DriverManager.getDriver()).getResults();
        Assert.assertTrue(actual.contains(expectedResult),
                "Expected: " + expectedResult + " but got: " + actual);
    }
}
```

---

## Step 5: Create a Feature File

File: `src/test/resources/features/dashboard.feature`

**Rules:**
- Use `Scenario Outline` + `Examples` for data-driven scenarios
- `testCaseId` values in Examples must match `TestCaseId` in Excel exactly
- Step text must match step definitions in the Steps class exactly

```gherkin
Feature: Dashboard Search

  Scenario Outline: Search - <testCaseId>
    Given user is on dashboard page
    When user searches with test case "<testCaseId>"
    Then search result should be "<expectedResult>"

    Examples:
      | testCaseId  | expectedResult |
      | ValidSearch | found          |
      | EmptySearch | no-results     |
```

---

## Quick Reference

### Files changed per new feature

| File | Action |
|---|---|
| `locators.properties` | Add page locator section |
| `testdata.xlsx` | Add new sheet |
| `pages/XxxPage.java` | Create — extend BasePage |
| `steps/XxxSteps.java` | Create — set SHEET_NAME |
| `features/xxx.feature` | Create — testCaseId matches Excel |

**Files that NEVER change when adding features:**
`Hooks.java`, `DriverManager.java`, `ExcelUtils.java`, `BasePage.java`, `ObjectRepository.java`, `TestRunner.java`, `docker-compose.yml`, `selenium-tests.yml`

---

### Adding a test case to an existing feature (2 files only)

| File | Action |
|---|---|
| `testdata.xlsx` → feature sheet | Add one row |
| `xxx.feature` → Examples table | Add one line |

---

### Changing a locator (1 file only)

| File | Action |
|---|---|
| `locators.properties` | Update the locator value |
