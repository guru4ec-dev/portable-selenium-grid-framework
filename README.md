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
| Apache POI | Data-driven test data (Excel) |
| Masterthought | Rich HTML test reports |

---

## Guides

| Guide | Contents |
|---|---|
| [Running Tests](docs/RUNNING-TESTS.md) | GitHub Actions, Docker local, screenshot modes, parallel scaling, CI/CD |
| [Developer Guide](docs/DEVELOPER-GUIDE.md) | Adding features — 5-step walkthrough, scaffold script, locators, test data |
| [Troubleshooting](docs/TROUBLESHOOTING.md) | Common errors, Grid setup, Docker logs |

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
    │       ├── ExcelUtils.java          ← Excel test data reader (Apache POI)
    │       └── ObjectRepository.java   ← Loads locators from locators.properties
    └── resources/
        ├── features/
        │   └── login.feature            ← Cucumber BDD scenarios
        ├── locators.properties          ← All element locators in one place
        └── testdata/
            └── testdata.xlsx            ← Test data (one sheet per feature)
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
│  ExcelUtils (xlsx data)       │    │  Page Objects + Steps        │
└──────────────┬───────────────┘    └──────────────┬───────────────┘
               │                                    │
               └──────────────┬─────────────────────┘
                               ↓
                    Selenium Grid Hub (4444)
                    ├── Chrome Node (4.21.0)
                    └── Firefox Node (4.21.0)
```

**Layer responsibilities:**

```
feature file          → What to test (business language — readable by BA/QA)
Steps class           → How to test (reads CSV, calls Page methods)
Page class            → Page actions (open, login, getFlashMessage)
BasePage              → Reusable Selenium keywords (type, click, getText...)
locators.properties   → All element locators in one place
testdata.xlsx         → All test data, one sheet per feature
```

---

## Key Features

- **Zero local installation** — only Git needed; everything else runs in Docker
- **Hybrid BDD + Data-Driven** — Cucumber feature files with Excel test data (one sheet per feature)
- **Page Object Model** — `BasePage` keyword-driven actions (`type`, `click`, `getText`, `open`, `isDisplayed`)
- **Object Repository** — all locators centralised in `locators.properties`
- **Configurable screenshots** — `failure` / `step-all` / `step-failed` via `-Dscreenshot.mode`
- **Session retry logic** — 5 retries × 10s for flake-resistant Grid connections
- **Docker healthcheck sequencing** — prevents race conditions at Grid startup
- **Parallel execution** — Chrome + Firefox simultaneously; scenarios parallel within each job

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