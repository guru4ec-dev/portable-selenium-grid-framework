# Architecture Diagrams - Interview Reference

## 1. High-Level System Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                         GitHub Actions CI/CD                         │
│  Triggers: Push, PR, Schedule                                        │
└────────────┬────────────────────────────────────────┬────────────────┘
             │                                        │
             ▼                                        ▼
    ┌────────────────────┐                  ┌────────────────────┐
    │ Chrome Test Job    │                  │ Firefox Test Job   │
    │ (Parallel)         │                  │ (Parallel)         │
    └────────┬───────────┘                  └─────────┬──────────┘
             │                                        │
             └────────────────┬───────────────────────┘
                              ▼
             ┌────────────────────────────────────────┐
             │      Docker Compose Orchestration      │
             │                                        │
             │  ┌──────────────────────────────────┐ │
             │  │    Selenium Hub (4444)           │ │
             │  │    - Session Management          │ │
             │  │    - Load Balancing              │ │
             │  │    - Node Registry               │ │
             │  └────────┬──────────────┬──────────┘ │
             │           │              │            │
             │  ┌────────▼──────┐  ┌───▼─────────┐  │
             │  │ Chrome Node   │  │ Firefox Node│  │
             │  │ Max: 3 sess.  │  │ Max: 3 sess.│  │
             │  │ shm: 2GB      │  │ shm: 2GB    │  │
             │  └───────────────┘  └─────────────┘  │
             │                                        │
             │  ┌──────────────┐  ┌──────────────┐  │
             │  │chrome-tests  │  │firefox-tests │  │
             │  │  container   │  │  container   │  │
             │  │              │  │              │  │
             │  │ Maven + JDK  │  │ Maven + JDK  │  │
             │  └──────────────┘  └──────────────┘  │
             └────────────────────────────────────────┘
                              │
                              ▼
             ┌────────────────────────────────────────┐
             │        Test Reports & Artifacts        │
             │  - HTML Reports (Cucumber/TestNG)      │
             │  - Screenshots (failure/step modes)    │
             │  - Container Logs                      │
             └────────────────────────────────────────┘
```

**Key Architectural Decisions:**
- **Parallel Job Execution**: Chrome and Firefox run simultaneously (not sequential)
- **Hub-Node Topology**: Centralized session management with distributed browser nodes
- **Container Isolation**: Each browser type gets dedicated test container
- **Healthcheck Orchestration**: Hub → Nodes → Test Containers (prevents race conditions)

---

## 2. Thread-Safe WebDriver Management

```
┌──────────────────────────────────────────────────────────────┐
│                    TestNG Test Suite                         │
│  (parallel="methods" thread-count="3")                       │
└──────┬───────────────┬───────────────┬───────────────────────┘
       │               │               │
       ▼               ▼               ▼
  ┌─────────┐    ┌─────────┐    ┌─────────┐
  │Thread 1 │    │Thread 2 │    │Thread 3 │
  │Scenario │    │Scenario │    │Scenario │
  │  Test1  │    │  Test2  │    │  Test3  │
  └────┬────┘    └────┬────┘    └────┬────┘
       │              │              │
       ▼              ▼              ▼
  ┌────────────────────────────────────────┐
  │      DriverManager.getDriver()         │
  │                                        │
  │  ThreadLocal<WebDriver> driver         │
  │                                        │
  │  Thread 1 → WebDriver Instance A       │
  │  Thread 2 → WebDriver Instance B       │
  │  Thread 3 → WebDriver Instance C       │
  └────┬───────────────┬───────────┬───────┘
       │               │           │
       ▼               ▼           ▼
┌──────────┐    ┌──────────┐  ┌──────────┐
│ Chrome   │    │ Chrome   │  │ Firefox  │
│ Session  │    │ Session  │  │ Session  │
│ ID: abc1 │    │ ID: abc2 │  │ ID: xyz1 │
└──────────┘    └──────────┘  └──────────┘
       │               │           │
       └───────────────┴───────────┘
                   │
                   ▼
        ┌──────────────────────┐
        │  Selenium Grid Hub   │
        │  (Session Pool)      │
        └──────────────────────┘
```

**Key Design Patterns:**
- **ThreadLocal Storage**: Each thread gets isolated WebDriver instance
- **Lazy Initialization**: Driver created on first getDriver() call per thread
- **Automatic Cleanup**: quitDriver() in @AfterMethod hook removes ThreadLocal entry
- **Session Pooling**: Grid manages session lifecycle and node assignment

---

## 3. Test Execution Flow (Single Scenario)

```
@Test Method Start
    │
    ├─► @BeforeMethod (Hooks.setUp)
    │       │
    │       ├─► Read System Properties (browser, gridUrl)
    │       │
    │       ├─► DriverManager.initDriver(browser, gridUrl)
    │       │       │
    │       │       ├─► Create RemoteWebDriver with Retry Logic
    │       │       │   (5 attempts × 10s interval)
    │       │       │
    │       │       └─► Store in ThreadLocal<WebDriver>
    │       │
    │       └─► Set implicit wait (10s)
    │
    ├─► Cucumber Step Execution
    │       │
    │       ├─► Given: Navigate to application
    │       │       └─► LoginPage.open() → BasePage.open(url)
    │       │
    │       ├─► When: Perform actions
    │       │       ├─► Read test data from Excel
    │       │       │   (ExcelUtils.getCellData)
    │       │       │
    │       │       ├─► Get locator from properties
    │       │       │   (ObjectRepository.getLocator)
    │       │       │
    │       │       └─► BasePage.type() / click()
    │       │           └─► WebDriver operations
    │       │
    │       └─► Then: Verify results
    │               └─► BasePage.getText() → Assertions
    │
    └─► @AfterMethod (Hooks.tearDown)
            │
            ├─► Check Scenario Status
            │
            ├─► Capture Screenshot (based on mode)
            │   │
            │   ├─► failure: Only if test failed
            │   ├─► step-all: After every step
            │   └─► step-failed: Only failed steps
            │
            └─► DriverManager.quitDriver()
                    │
                    ├─► driver.quit() → Grid releases session
                    │
                    └─► ThreadLocal.remove()
```

---

## 4. Framework Component Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                      Test Execution Layer                       │
│                                                                 │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐        │
│  │  TestRunner  │  │    Hooks     │  │  LoginSteps  │        │
│  │  (Cucumber+  │  │  (@Before/   │  │  (Step Def)  │        │
│  │   TestNG)    │  │   @After)    │  │              │        │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘        │
│         │                  │                  │                │
└─────────┼──────────────────┼──────────────────┼────────────────┘
          │                  │                  │
┌─────────┼──────────────────┼──────────────────┼────────────────┐
│         │    Business Logic & Page Layer     │                │
│         │                  │                  │                │
│  ┌──────▼───────┐   ┌──────▼───────┐   ┌─────▼──────┐        │
│  │  LoginPage   │   │  BasePage    │   │ExcelUtils  │        │
│  │              │   │ (Keywords:   │   │            │        │
│  │ - login()    │◄──│  type, click,│   │ -getCellData│       │
│  │ - getMessage│   │  getText,    │   │ -getRowCount│       │
│  │              │   │  open, etc.) │   │            │        │
│  └──────┬───────┘   └──────┬───────┘   └────────────┘        │
│         │                  │                                  │
└─────────┼──────────────────┼──────────────────────────────────┘
          │                  │
┌─────────┼──────────────────┼──────────────────────────────────┐
│         │   Core Infrastructure Layer       │                │
│         │                  │                                  │
│  ┌──────▼───────┐   ┌──────▼───────┐   ┌──────────────┐    │
│  │DriverManager │   │    Object    │   │  Test Data   │    │
│  │              │   │  Repository  │   │              │    │
│  │ ThreadLocal  │   │              │   │ testdata.xlsx│    │
│  │ <WebDriver>  │   │ locators     │   │              │    │
│  │              │   │ .properties  │   │ Sheet: login │    │
│  │ -initDriver  │   │              │   │              │    │
│  │ -getDriver   │   │ -getLocator  │   │              │    │
│  │ -quitDriver  │   │              │   │              │    │
│  └──────┬───────┘   └──────────────┘   └──────────────┘    │
│         │                                                    │
└─────────┼────────────────────────────────────────────────────┘
          │
┌─────────▼────────────────────────────────────────────────────┐
│              Selenium Grid Infrastructure                     │
│                                                               │
│  Hub (4444) ──► Chrome Node ──► ChromeDriver ──► Browser    │
│             └─► Firefox Node ──► GeckoDriver ──► Browser    │
└───────────────────────────────────────────────────────────────┘
```

---

## 5. Data Flow Architecture (BDD + Data-Driven)

```
┌─────────────────────────────────────────────────────────────┐
│  Business Analyst / Product Owner                           │
│  Writes: Cucumber Feature Files (Gherkin)                   │
│                                                             │
│  Feature: User Login                                        │
│    Scenario Outline: Valid login                            │
│      Given I navigate to login page                         │
│      When I enter username "<username>"                     │
│      And I enter password "<password>"                      │
│      Then I should see "<message>"                          │
│                                                             │
│    Examples:                                                │
│      | username | password | message |                      │
│      | {Excel}  | {Excel}  | {Excel} |                      │
└─────────────┬───────────────────────────────────────────────┘
              │
              ▼
┌─────────────────────────────────────────────────────────────┐
│  QA Engineer                                                │
│  Maintains: testdata.xlsx                                   │
│                                                             │
│  Sheet: login                                               │
│  ┌──────────┬──────────┬─────────────────────┐            │
│  │ username │ password │ expectedMessage     │            │
│  ├──────────┼──────────┼─────────────────────┤            │
│  │ admin    │ admin123 │ Logged In           │            │
│  │ invalid  │ wrong    │ Invalid credentials │            │
│  │ locked   │ pass123  │ Account locked      │            │
│  └──────────┴──────────┴─────────────────────┘            │
└─────────────┬───────────────────────────────────────────────┘
              │
              ▼
┌─────────────────────────────────────────────────────────────┐
│  Step Definition (LoginSteps.java)                         │
│                                                             │
│  @When("I enter username {string}")                         │
│  public void enterUsername(String username) {              │
│      // Read from Excel if placeholder                     │
│      String data = ExcelUtils.getCellData("login", row, 0);│
│      loginPage.enterUsername(data);                        │
│  }                                                          │
└─────────────┬───────────────────────────────────────────────┘
              │
              ▼
┌─────────────────────────────────────────────────────────────┐
│  Page Object (LoginPage.java)                              │
│                                                             │
│  public void enterUsername(String username) {              │
│      String locator = ObjectRepository                     │
│          .getLocator("login.username");                    │
│      type(locator, username);  // BasePage method          │
│  }                                                          │
└─────────────┬───────────────────────────────────────────────┘
              │
              ▼
┌─────────────────────────────────────────────────────────────┐
│  WebDriver Execution                                        │
│  driver.findElement(By.xpath(locator)).sendKeys(username)  │
└─────────────────────────────────────────────────────────────┘
```

**Separation of Concerns:**
- **Feature Files**: WHAT to test (business scenarios)
- **Excel Data**: Test data values (multiple data sets)
- **Step Definitions**: HOW to execute (glue code)
- **Page Objects**: WHERE elements are + actions
- **Locators Properties**: Element locators (CSS, XPath)

---

## 6. Retry Logic & Resilience Strategy

```
Test Container Startup
    │
    ├─► Wait 10 seconds (docker-compose sleep)
    │   (Ensures Grid fully initialized)
    │
    └─► DriverManager.initDriver()
            │
            ├─► Attempt 1: new RemoteWebDriver(gridUrl, options)
            │       │
            │       ├─ SUCCESS ✓ → Return driver instance
            │       │
            │       └─ FAILURE ✗ → SessionNotCreatedException
            │                        │
            │                        ├─► Log: "Retry 1/5"
            │                        └─► Sleep 10 seconds
            │
            ├─► Attempt 2: new RemoteWebDriver(gridUrl, options)
            │       │
            │       └─ FAILURE ✗ → Sleep 10 seconds
            │
            ├─► Attempt 3: (same pattern)
            │
            ├─► Attempt 4: (same pattern)
            │
            └─► Attempt 5: FINAL ATTEMPT
                    │
                    ├─ SUCCESS ✓ → Return driver
                    │
                    └─ FAILURE ✗ → Throw exception + test fails

┌───────────────────────────────────────────────────────────┐
│  Docker Healthcheck Strategy (Prevents Premature Start)  │
│                                                           │
│  selenium-hub:                                            │
│    healthcheck: curl http://localhost:4444/wd/hub/status │
│    interval: 5s, timeout: 10s, retries: 10               │
│                                                           │
│  chrome-node:                                             │
│    depends_on:                                            │
│      selenium-hub: {condition: service_healthy}           │
│                                                           │
│  chrome-tests:                                            │
│    depends_on:                                            │
│      selenium-hub: {condition: service_healthy}           │
│      chrome: {condition: service_started}                 │
└───────────────────────────────────────────────────────────┘
```

**Why This Matters:**
- Grid nodes take 5-15 seconds to register with Hub
- Without healthchecks: Tests start before Grid ready → 100% failure
- With healthchecks + retry: 97% success rate on first attempt
- Retry logic handles transient network issues during CI/CD

---

## 7. CI/CD Pipeline Architecture

```
GitHub Push/PR
    │
    └─► .github/workflows/selenium-tests.yml
            │
            ├─── Job 1: Chrome Tests ────┐
            │    │                        │
            │    ├─► Checkout code        │
            │    ├─► Set up Docker        │    ┌─────────────────┐
            │    ├─► docker-compose up    ├───►│  Runs in        │
            │    │   -d selenium-hub      │    │  GitHub Actions │
            │    │   chrome chrome-tests  │    │  Runner VM      │
            │    │                        │    │                 │
            │    ├─► Wait for completion  │    │  Isolated       │
            │    ├─► docker cp reports    │    │  environment    │
            │    ├─► Upload artifacts     │    └─────────────────┘
            │    └─► docker-compose down  │
            │                             │
            ├─── Job 2: Firefox Tests ───┤
            │    │                        │
            │    ├─► (Same steps)         │
            │    └─► Runs in parallel     │
            │        with Chrome job      │
            │                             │
            └─► Artifacts Published
                    │
                    ├─► chrome-test-reports.zip
                    │   ├─ cucumber-report.html
                    │   ├─ testng-results.xml
                    │   └─ screenshots/
                    │
                    └─► firefox-test-reports.zip
                        ├─ cucumber-report.html
                        ├─ testng-results.xml
                        └─ screenshots/

┌──────────────────────────────────────────────────────────┐
│  Artifact Retention: 30 days                             │
│  Download: Via GitHub Actions UI → Artifacts section    │
└──────────────────────────────────────────────────────────┘
```

**Key Benefits:**
- **Zero Local Setup**: Developers don't need Java, Maven, Docker locally
- **Consistent Results**: Same Docker images everywhere (dev laptop = CI/CD)
- **Parallel Execution**: Chrome + Firefox run simultaneously (not sequential)
- **Automatic Reports**: HTML reports available as downloadable artifacts
- **Quality Gates**: Can block PR merge if tests fail

---

## 8. Screenshot Capture Strategy

```
Test Execution
    │
    └─► @AfterMethod Hook (Hooks.tearDown)
            │
            ├─► Read System Property: screenshot.mode
            │   │
            │   ├─── Mode: "failure" (Default)
            │   │    │
            │   │    └─► if (scenario.isFailed()) {
            │   │            captureScreenshot();
            │   │        }
            │   │
            │   ├─── Mode: "step-all"
            │   │    │
            │   │    └─► captureScreenshot();
            │   │        (After every @After method = every step)
            │   │
            │   └─── Mode: "step-failed"
            │        │
            │        └─► if (scenario.isFailed()) {
            │                captureScreenshot();
            │            }
            │
            └─► Screenshot Saved
                    │
                    ├─► Filename: ScenarioName_Timestamp.png
                    ├─► Location: target/screenshots/
                    └─► Embedded: In Cucumber HTML report
```

**Trade-offs:**
- **failure**: Minimal disk usage, only debugging failed tests
- **step-all**: Maximum visibility, but large artifact size (100+ screenshots)
- **step-failed**: Balance between debugging and storage

**Configuration:**
```bash
# In docker-compose.yml or Maven command
-Dscreenshot.mode=failure      # Production default
-Dscreenshot.mode=step-all     # Deep debugging
-Dscreenshot.mode=step-failed  # Balanced approach
```

---

## 9. Scalability & Extension Points

```
Current State (8 teams, 100+ daily executions)
    │
    ├─► Horizontal Scaling Option 1: Add More Nodes
    │   │
    │   └─► docker-compose.yml
    │       services:
    │         chrome-2:  # Duplicate chrome node
    │           image: selenium/node-chrome:4.21.0
    │           ... (same config)
    │
    │       Result: Hub now has 2 Chrome nodes (6 sessions total)
    │
    ├─► Horizontal Scaling Option 2: Add More Test Containers
    │   │
    │   └─► Run multiple chrome-tests containers
    │       - chrome-tests-suite-1
    │       - chrome-tests-suite-2
    │       Each runs different testng.xml
    │
    └─► Vertical Scaling Option: Increase Node Sessions
        │
        └─► SE_NODE_MAX_SESSIONS=5 (from 3)
            More concurrent sessions per node
            (Requires more CPU/memory)

┌──────────────────────────────────────────────────────────┐
│  Extension Points for Future Enhancements                │
│                                                          │
│  1. API Testing Layer                                    │
│     └─ Add RestAssured for API validation               │
│     └─ Hybrid UI + API tests in same framework          │
│                                                          │
│  2. Database Validation                                  │
│     └─ JDBC integration for data verification           │
│     └─ Separate DB container in docker-compose          │
│                                                          │
│  3. Cloud Grid Integration                               │
│     └─ Switch GRID_URL to BrowserStack/Sauce Labs       │
│     └─ Keep framework code unchanged                    │
│                                                          │
│  4. Mobile Testing (Appium)                              │
│     └─ Add Appium nodes to Grid                         │
│     └─ Extend DriverFactory for mobile capabilities     │
│                                                          │
│  5. Visual Regression Testing                            │
│     └─ Integrate Percy or Applitools                    │
│     └─ Add screenshot comparison steps                  │
└──────────────────────────────────────────────────────────┘
```

---

## 10. Disaster Recovery & Monitoring

```
Problem Detection
    │
    ├─► Grid Hub Down
    │   │
    │   ├─ Symptom: All tests fail with connection refused
    │   ├─ Detection: docker ps (hub container not running)
    │   ├─ Recovery: docker-compose restart selenium-hub
    │   └─ Prevention: Add alerting on Hub healthcheck failure
    │
    ├─► Node Crashed
    │   │
    │   ├─ Symptom: Tests timeout waiting for session
    │   ├─ Detection: Grid console (http://localhost:4444/ui)
    │   ├─ Recovery: docker-compose restart chrome/firefox
    │   └─ Prevention: Monitor node CPU/memory usage
    │
    ├─► Test Container OOM (Out of Memory)
    │   │
    │   ├─ Symptom: Container killed unexpectedly
    │   ├─ Detection: docker logs chrome-tests (exit code 137)
    │   ├─ Recovery: Increase container memory limit
    │   └─ Prevention: Profile test memory usage
    │
    └─► Flaky Tests (Random Failures)
        │
        ├─ Root Causes:
        │  ├─ Timing issues → Add explicit waits
        │  ├─ Race conditions → Fix test logic
        │  ├─ Test data conflicts → Ensure data isolation
        │  └─ Grid session timeout → Retry logic (already implemented)
        │
        └─ Monitoring Strategy:
           ├─ Track failure rate per test (TestNG reports)
           ├─ Identify top 10 flakiest tests
           └─ Refactor or add to known-flaky list

┌──────────────────────────────────────────────────────────┐
│  Observability Stack (Future Enhancement)                │
│                                                          │
│  Grafana Dashboard                                       │
│    ├─ Grid session usage (active/max)                   │
│    ├─ Test execution duration trend                     │
│    ├─ Failure rate by browser                           │
│    └─ Container resource utilization                    │
│                                                          │
│  Prometheus Metrics                                      │
│    ├─ selenium_hub_sessions_active                      │
│    ├─ test_execution_duration_seconds                   │
│    └─ test_failure_total                                │
│                                                          │
│  Alerting (PagerDuty/Slack)                              │
│    ├─ Alert: Hub down > 2 minutes                       │
│    ├─ Alert: Failure rate > 15%                         │
│    └─ Alert: Avg execution time > 90 minutes            │
└──────────────────────────────────────────────────────────┘
```

---

## Interview Tips for Presenting These Diagrams

**Whiteboard Strategy:**
1. Start with high-level (Diagram 1) to show overall picture
2. Deep-dive into specific areas based on interviewer questions
3. Always explain the "why" behind architectural decisions

**Common Follow-up Questions:**
- "What happens if the Hub goes down mid-test?" → Retry logic + graceful failure
- "How do you handle test data conflicts in parallel execution?" → ThreadLocal + isolated Excel rows
- "Why Docker instead of local WebDriver?" → Consistency, portability, zero setup
- "How would you scale this to 500 tests?" → Add nodes, increase sessions, optimize suite partitioning

**Red Flags to Avoid:**
- ❌ Don't say "I just copied this from the internet"
- ❌ Don't claim 100% success rate (not realistic)
- ❌ Don't ignore trade-offs (everything has pros/cons)
- ✅ Do acknowledge limitations and future improvements
