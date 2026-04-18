# Troubleshooting

---

## Common Issues & Fixes

| Issue | Cause | Fix |
|---|---|---|
| `SessionNotCreatedException` | Grid node not registered yet when test starts | Retry logic in `DriverManager` handles this automatically (5 retries × 10s) |
| `TimeoutException` when getting session | Hub started before nodes were ready | Docker healthcheck on `selenium-hub` sequences startup — ensure `docker-compose up --build` is used |
| Report is empty or not opening | Tests failed before the `verify` phase completed | `testFailureIgnore=true` is set in `pom.xml` — check `target/surefire-reports` for raw output |
| Locator not found (`NoSuchElementException`) | Wrong key name in `locators.properties` | Check the key spelling in `locators.properties` matches exactly what is used in the Page class |
| `NullPointerException` in Steps class | `DriverManager.getDriver()` returned null | Confirm `@Before` in `Hooks.java` ran successfully — look for Grid connection errors above |
| Excel sheet not found | `SHEET_NAME` in Steps class doesn't match the Excel sheet tab name | Sheet names are case-sensitive — `"Login"` ≠ `"login"` |
| Test data row not found | `TestCaseId` in feature file doesn't match any row in Excel | Check the Examples table value matches the `TestCaseId` column value exactly |
| Scaffold script not running | PowerShell execution policy blocked | Run once: `Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser` |
| `PageObjectsNotInitialisedException` | PageFactory not initialised before use | Ensure `PageFactory.initElements(driver, this)` is called in the page class constructor |

---

## Selenium Grid Setup

| Component | Docker Image | Version |
|---|---|---|
| Hub | `selenium/hub` | 4.21.0 |
| Chrome Node | `selenium/node-chrome` | 4.21.0 |
| Firefox Node | `selenium/node-firefox` | 4.21.0 |

All component versions are pinned to the same version in `docker-compose.yml` — do not mix versions.

**Grid UI (local run only):** `http://localhost:4444`  
Use this to confirm nodes have registered and sessions are active.

**Grid startup order:**
1. `selenium-hub` starts and passes healthcheck
2. `chrome` and `firefox` nodes start and register with hub
3. Maven test container starts and connects to hub

This sequencing is enforced by `depends_on` + `condition: service_healthy` in `docker-compose.yml`.

---

## Ports

| Service | Port | Purpose |
|---|---|---|
| Selenium Hub UI | `4444` | Grid UI + WebDriver endpoint |
| Chrome node | (internal) | Connects to hub via Docker network |
| Firefox node | (internal) | Connects to hub via Docker network |

---

## Checking Logs

**Hub logs:**
```bat
docker logs selenium-hub
```

**Node logs:**
```bat
docker logs chrome-node
docker logs firefox-node
```

**Maven test container:**
```bat
docker logs maven-tests
```

Look for `Registered a new session` in the hub log to confirm a test has connected to the Grid.
