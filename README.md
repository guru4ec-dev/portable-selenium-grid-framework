# Selenium Grid Automation Framework
## Docker + Parallel + Multi-Browser + BDD + Data-Driven

---

## Overview

A portable, scalable Selenium automation framework with zero local installation required.
Tests run entirely in the cloud via **GitHub Actions** — just push code and download reports.

| Technology | Purpose |
|---|---|
| Java 17 + Selenium 4 | Browser automation |
| Cucumber 7 (BDD) | Behaviour-driven test scenarios |
| TestNG | Test execution and parallel control |
| Docker + Selenium Grid | Containerized cross-browser execution |
| GitHub Actions | CI/CD — runs without any local setup |
| Apache POI / CSV | Data-driven test data |
| Masterthought | Rich HTML test reports |

---

## Project Structure

```
src/
└── test/
    ├── java/com/automation/
    │   ├── core/
    │   │   └── DriverManager.java       ← ThreadLocal WebDriver + retry logic
    │   ├── hooks/
    │   │   └── Hooks.java               ← Before/After + configurable screenshot capture
    │   ├── pages/
    │   │   └── LoginPage.java           ← Page Object Model (PageFactory)
    │   ├── runners/
    │   │   └── TestRunner.java          ← Cucumber + TestNG entry point
    │   ├── steps/
    │   │   └── LoginSteps.java          ← BDD step definitions
    │   └── utils/
    │       └── ExcelUtils.java          ← CSV test data reader
    └── resources/
        ├── features/
        │   └── login.feature            ← Cucumber BDD scenarios
        └── testdata/
            └── login_data.csv           ← Data-driven test inputs
```

---

## Architecture

```
GitHub Actions (CI/CD)
        ↓
Docker Compose
        ↓
┌──────────────────────────────┐
│  chrome-tests container      │    ┌──────────────────────────────┐
│  mvn verify -Dbrowser=chrome │    │  firefox-tests container     │
│         ↓                    │    │  mvn verify -Dbrowser=firefox│
│  TestNG + Cucumber            │    │         ↓                    │
│  Hooks → DriverManager        │    │  TestNG + Cucumber           │
│  Page Objects + Steps         │    │  Hooks → DriverManager       │
│  ExcelUtils (CSV data)        │    │  Page Objects + Steps        │
└──────────────┬───────────────┘    └──────────────┬───────────────┘
               │                                    │
               └──────────────┬─────────────────────┘
                               ↓
                    Selenium Grid Hub (4444)
                    ├── Chrome Node (4.21.0)
                    └── Firefox Node (4.21.0)
```

---

## Key Features

### 1. Zero Local Installation
All tools (Java, Maven, Selenium Grid, Chrome, Firefox) run inside Docker.
Only **Git** is required on your local machine.

### 2. Page Object Model
All locators are in `LoginPage.java` using `PageFactory`.
Steps reference page objects — no raw locators in test code.

### 3. Hybrid BDD + Data-Driven Framework
Test scenarios are defined in Cucumber feature files (BDD).
Test data is read from `login_data.csv` (Data-Driven).

```
Feature file (BDD)         CSV file (Data-Driven)
──────────────────         ──────────────────────────────────────────
Scenario: ValidLogin    →  tomsmith / SuperSecretPassword! / success
Scenario: InvalidPass   →  tomsmith / wrongpassword         / failure
Scenario: InvalidUser   →  wronguser / SuperSecretPassword! / failure
```

To add new test cases — add a row to the CSV and a line in the `Examples` table.

### 4. Configurable Screenshot Capture
Screenshot behaviour is controlled by the `-Dscreenshot.mode` property.

| `screenshot.mode` | When screenshot is taken |
|---|---|
| `failure` (default) | End of scenario, only if it failed |
| `step-all` | After every step (passed + failed) |
| `step-failed` | After each individual step that fails |

```bash
# Default — screenshot only on scenario failure
mvn verify -Dbrowser=chrome

# Screenshot after every step
mvn verify -Dbrowser=chrome -Dscreenshot.mode=step-all

# Screenshot only after each failed step
mvn verify -Dbrowser=chrome -Dscreenshot.mode=step-failed
```

All screenshots are embedded directly in the HTML report alongside the relevant step.

### 5. Session Retry Logic
`DriverManager` retries session creation up to 5 times with 10s delay if the Grid node is not ready.

### 6. Docker Healthcheck Sequencing
Hub healthcheck ensures Chrome/Firefox nodes only start after hub is confirmed healthy.
Prevents `SessionNotCreatedException` caused by race conditions at startup.

### 7. Parallel Execution
Chrome and Firefox test containers run **simultaneously** in GitHub Actions (separate jobs).
Within each container, scenarios run in parallel via `@DataProvider(parallel = true)` in `TestRunner.java`.

**How parallelism works:**  
There is only one `@Test` method (`scenarios()`) in the suite, so TestNG-level `parallel="methods"` is intentionally **not used** — it would have no effect. All concurrency is handled exclusively by `data-provider-thread-count`, which caps how many scenarios the DataProvider runs simultaneously.

`data-provider-thread-count` is kept equal to `SE_NODE_MAX_SESSIONS` so threads never exceed Grid session capacity — extra scenarios queue and run as sessions free up.

To scale up, increase both values together:

| Setting | Location | Default |
|---|---|---|
| `data-provider-thread-count` | `testng.xml` | `3` |
| `SE_NODE_MAX_SESSIONS` | `docker-compose.yml` | `3` |

---

## Running Tests

### Via GitHub Actions (Recommended — no local setup needed)

**Manual trigger:**
1. Go to your GitHub repo → **Actions** tab
2. Select **Selenium Grid Tests**
3. Click **Run workflow**

**Download reports:**
1. Click the completed run
2. Scroll to **Artifacts**
3. Download `chrome-reports` or `firefox-reports`
4. Extract and open `overview-features.html`

### Via Docker locally (requires Docker Desktop)

```bat
docker-compose up --build
```

Reports saved to `reports/chrome/` and `reports/firefox/`.

---

## Test Data

Located at `src/test/resources/testdata/login_data.csv`:

```csv
TestCaseId,Username,Password,ExpectedResult
ValidLogin,tomsmith,SuperSecretPassword!,success
InvalidPassword,tomsmith,wrongpassword,failure
InvalidUsername,wronguser,SuperSecretPassword!,failure
```

To add new scenarios:
1. Add a row to `login_data.csv`
2. Add a matching line to the `Examples` table in `login.feature`

---

## Selenium Grid Setup

| Component | Image | Version |
|---|---|---|
| Hub | selenium/hub | 4.21.0 |
| Chrome Node | selenium/node-chrome | 4.21.0 |
| Firefox Node | selenium/node-firefox | 4.21.0 |

Grid UI (local): `http://localhost:4444`

---

## Common Issues & Fixes

| Issue | Cause | Fix |
|---|---|---|
| `SessionNotCreatedException` | Node not registered yet | Retry logic in DriverManager (5×10s) |
| `TimeoutException` on session | Grid slow to start | Docker healthcheck sequences startup |
| Report empty / not opening | Tests failed before verify phase | `testFailureIgnore=true` in pom.xml |
| Only 1 scenario passes | Parallel with 1 node | Sequential execution per container |

---

## CI/CD

GitHub Actions workflow (`.github/workflows/selenium-tests.yml`):
- Trigger: **manual only** (`workflow_dispatch`)
- Runs Chrome and Firefox jobs **in parallel**
- Uploads HTML reports as downloadable artifacts
- Reports include pass/fail stats, duration, and screenshots on failure

---

## Interview Highlights

- Built portable Selenium Grid using Docker — zero installation on any machine
- Implemented hybrid BDD + Data-Driven framework (Cucumber + CSV)  
- Designed Page Object Model with PageFactory for maintainable locators
- Achieved thread-safe parallel execution with ThreadLocal WebDriver
- Configurable screenshot capture: per-scenario failure, per-step (all), or per-step (failed only)
- Implemented session retry logic for flake-resistant Grid connections
- Set up GitHub Actions CI/CD with artifact-based HTML report delivery

---

## Author

Guruvaiya Muthukaruppan