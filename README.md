# Portable Selenium Grid Framework

> **Enterprise-grade test automation — zero local setup, runs entirely in CI/CD.**
> Built by [Guruvaiya Muthukaruppan](https://linkedin.com/in/guruvaiya-m) — Automation Architect with 19+ years delivering QA frameworks for Fortune 500 firms.

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square)
![Selenium](https://img.shields.io/badge/Selenium-4-green?style=flat-square)
![Cucumber](https://img.shields.io/badge/Cucumber-BDD-brightgreen?style=flat-square)
![Docker](https://img.shields.io/badge/Docker-Grid-blue?style=flat-square)
![CI](https://img.shields.io/badge/CI-GitHub_Actions-black?style=flat-square)

---

## What this framework solves

Most Selenium Grid setups require local browser installation, manual Grid configuration, and environment-specific setup steps — creating flaky runs and slow onboarding.

This framework eliminates all of that:

- **Zero local installation** — only Git required; Docker handles everything else
- **Push-to-run CI/CD** — GitHub Actions triggers full cross-browser regression on every commit
- **Parallel Chrome + Firefox** — simultaneous execution cuts regression runtime significantly
- **Flake-resistant** — session retry logic (5 × 10s) and Docker healthcheck sequencing prevent race conditions
- **Business-readable tests** — Cucumber BDD feature files authored without writing code

---

## Tech stack

| Layer | Technology | Why |
|---|---|---|
| Browser automation | Java 17 + Selenium 4 | Industry standard, thread-safe WebDriver |
| BDD scenarios | Cucumber 7 | Business-readable; no-code test authoring for BAs |
| Execution control | TestNG | Parallel execution and suite management |
| Grid infrastructure | Docker + Selenium Grid 4 | Portable, containerised, zero-install |
| CI/CD pipeline | GitHub Actions | Runs on every push; artifacts auto-published |
| Test data | Apache POI (Excel) | Data-driven — one sheet per feature |
| Reporting | Masterthought | Rich HTML reports with screenshots |

---

## Architecture

```
GitHub Actions (CI/CD trigger on push)
        ↓
Docker Compose
        ↓
┌─────────────────────────┐    ┌─────────────────────────┐
│  chrome-tests container  │    │  firefox-tests container │
│  mvn verify -Dbrowser=  │    │  mvn verify -Dbrowser=  │
│  chrome                  │    │  firefox                 │
│  TestNG + Cucumber        │    │  TestNG + Cucumber       │
│  Hooks → DriverManager   │    │  Hooks → DriverManager   │
│  Page Objects + Steps    │    │  Page Objects + Steps    │
└──────────┬──────────────┘    └──────────┬───────────────┘
           └──────────────┬───────────────┘
                          ↓
               Selenium Grid Hub (:4444)
               ├── Chrome Node (4.21.0)
               └── Firefox Node (4.21.0)
```

**Layer responsibilities:**

| Layer | Responsibility |
|---|---|
| `feature` files | What to test — business language readable by BA/QA |
| `Steps` classes | How to test — reads Excel data, calls Page methods |
| `Page` classes | Page actions — open, login, getFlashMessage |
| `BasePage` | Reusable Selenium keywords — type, click, getText, isDisplayed |
| `locators.properties` | All element locators centralised in one place |
| `testdata.xlsx` | All test data — one sheet per feature |

---

## Project structure

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
    │       ├── ExcelUtils.java          ← Excel test data reader (Apache POI)
    │       └── ObjectRepository.java    ← Loads locators from locators.properties
    └── resources/
        ├── features/
        │   └── login.feature            ← Cucumber BDD scenarios
        ├── locators.properties          ← All element locators in one place
        └── testdata/
            └── testdata.xlsx            ← Test data (one sheet per feature)
```

---

## Key engineering decisions

**ThreadLocal WebDriver** — ensures thread safety during parallel execution; no shared state between test threads.

**Object Repository pattern** — all locators externalised to `locators.properties`; zero code changes needed when UI elements change.

**Configurable screenshot modes** — `failure` / `step-all` / `step-failed` via `-Dscreenshot.mode`; balances debug visibility vs report size.

**Docker healthcheck sequencing** — Grid Hub must be healthy before nodes register; nodes must be ready before tests execute. Prevents the most common source of CI flakiness.

**Session retry logic** — 5 retries × 10s intervals for Grid connections; handles transient startup delays without failing the suite.

---

## Running tests

| Guide | Contents |
|---|---|
| [Running Tests](docs/RUNNING-TESTS.md) | GitHub Actions, Docker local, screenshot modes, parallel scaling |
| [Developer Guide](docs/DEVELOPER-GUIDE.md) | Adding features — 5-step walkthrough, scaffold script, locators, test data |
| [Troubleshooting](docs/TROUBLESHOOTING.md) | Common errors, Grid setup, Docker logs |

---

## About the author

**Guruvaiya Muthukaruppan** — Automation Architect and Senior SDET with 19+ years building enterprise QA frameworks across Insurance, Banking, Retail, and Telecom.

Previously delivered automation at scale for Allianz, Home Depot (Fortune 500), Commonwealth Bank of Australia, and Vodafone. Co-developed the RAFT, TAM, and eRAFT frameworks adopted globally by JPMorgan Chase, Bank of America, and Humana at the TCS Centre of Excellence.

- 🔗 [LinkedIn](https://linkedin.com/in/guruvaiya-m)
- 💻 [GitHub](https://github.com/guru4ec-dev)
- 📧 guru4ec@gmail.com
