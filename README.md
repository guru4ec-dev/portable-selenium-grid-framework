🚀 Selenium Grid Automation Framework (Docker + Parallel + Multi-Browser)
📌 Overview

This project is a scalable Selenium automation framework built with:

✅ Java + Selenium 4
✅ Cucumber (BDD)
✅ TestNG (Parallel execution)
✅ Docker-based Selenium Grid
✅ CI/CD ready (Jenkins compatible)

It supports:

🔁 Parallel execution
🌐 Cross-browser testing (Chrome + Firefox)
📦 Containerized Grid (Docker)
⚡ Dynamic browser allocation
🏗️ Architecture

TestNG + Cucumber
        ↓
Hooks (Before/After)
        ↓
DriverManager (ThreadLocal)
        ↓
RemoteWebDriver
        ↓
Selenium Grid (Docker Hub + Nodes)

⚙️ Key Features
✅ 1. Parallel Execution
Implemented using TestNG + DataProvider
@Override
@DataProvider(parallel = true)
public Object[][] scenarios() {
    return super.scenarios();
}

✅ 2. Thread-Safe Driver (ThreadLocal)
private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();


Ensures:

No session conflicts
Stable parallel execution

✅ 3. Dynamic Browser Selection
✔ Supports:
CLI override
Auto distribution across browsers
String browser = System.getProperty("browser");

if (browser == null) {
    int index = counter.getAndIncrement() % BROWSERS.length;
    browser = BROWSERS[index];
}

✅ 4. Multi-Browser Execution

Supports:

Chrome
Firefox

Runs both browsers in parallel automatically

🐳 Selenium Grid Setup (Docker)
🔹 Start Grid with Scaling
docker-compose up --scale chrome=3 --scale firefox=3 -d

🔹 Verify Containers
docker ps


Expected:

3 Chrome nodes
3 Firefox nodes
1 Hub
🔹 Access Grid UI
http://localhost:4444

▶️ Test Execution
🔹 Run (Auto Browser Distribution)
mvn clean test -Dexecution=remote

👉 Runs on:

Chrome + Firefox (parallel)
🔹 Run Only Chrome
mvn clean test -Dexecution=remote -Dbrowser=chrome

🔹 Run Only Firefox
mvn clean test -Dexecution=remote -Dbrowser=firefox

⚡ Parallel Configuration
testng.xml
<suite name="Suite" parallel="methods" thread-count="6">
    <test name="Test">
        <classes>
            <class name="com.automation.runners.TestRunner"/>
        </classes>
    </test>
</suite>


🧠 Smart Browser Distribution

Uses Atomic Counter:

private static AtomicInteger counter = new AtomicInteger(0);
private static final String[] BROWSERS = {"chrome", "firefox"};

Benefits:
Even load distribution
No dependency on thread ID
Predictable execution

❗ Common Issues & Fixes
🔴 Issue: Queue buildup in Grid

Cause:

Only one browser used

Fix:

Enable dynamic browser selection
🔴 Issue: Only 3 sessions running

Cause:

thread-count = 3

Fix:

thread-count="6"

🔴 Issue: SessionNotCreatedException

Cause:

Insufficient nodes

Fix:

docker-compose up --scale chrome=3 --scale firefox=3 -d

🔴 Issue: Invalid Hook Signature

Wrong:

@Before
public void setUp(ITestContext context)


Correct:

@Before
public void setUp(Scenario scenario)

🧪 Sample Execution Flow
Test starts
Hook initializes driver
Browser assigned dynamically
Test runs on Grid
Driver quits after scenario

📊 Expected Grid Behavior
Metric	Expected
Nodes	6
Sessions	6
Queue	0
Concurrency	100%

🚀 CI/CD Integration
Works with Jenkins pipelines
Supports Docker-based execution
Easy scaling in CI environments

💡 Future Enhancements

📊 Allure Reporting
📸 Screenshot on failure
🔁 Retry mechanism
🌍 Environment config (QA/UAT)
🔗 Jenkins parallel stages


🧠 Interview Highlights
You can explain:

Built Selenium Grid using Docker
Enabled parallel execution with TestNG
Implemented ThreadLocal WebDriver
Designed dynamic browser allocation
Achieved optimal Grid utilization
👨‍💻 Author

Guruvaiya Muthukaruppan