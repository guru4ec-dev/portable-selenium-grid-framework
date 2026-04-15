🚀 Portable Selenium Grid Automation Framework

Implemented parallel execution using TestNG and achieved distributed execution by scaling Selenium Grid nodes using Docker. Each test runs on a separate containerized browser instance, ensuring true parallelism and scalability.

📌 Overview

This is a fully containerized Selenium automation framework designed for:

✅ Parallel execution
✅ Cross-browser testing (Chrome + Firefox)
✅ Selenium Grid (Docker)
✅ Cucumber + TestNG
✅ CI/CD integration (Jenkins + GitHub Actions)

👉 Built to run anywhere with zero manual setup

🧱 Tech Stack
Java 17
Selenium WebDriver
Cucumber (BDD)
TestNG
Docker & Docker Compose
Jenkins Pipeline
GitHub Actions
⚡ One-Click Execution
🧰 Prerequisite

Install Docker Desktop

▶️ Run the framework
git clone https://github.com/guru4ec-dev/portable-selenium-grid-framework.git
cd portable-selenium-grid-framework
docker-compose up --build

🌐 Selenium Grid UI

Open in browser:

http://localhost:4444

🧪 Test Execution

The framework automatically:

Starts Selenium Grid (Hub + Nodes)
Executes tests in:
Chrome ✅
Firefox ✅
Runs scenarios in parallel
Generates reports
📊 Reports

After execution, reports are available in:

/reports


Includes:

Cucumber HTML Report
Allure Results
🐳 Docker Architecture
Selenium Grid
 ├── Hub
 ├── Chrome Node
 ├── Firefox Node

Test Execution
 ├── Chrome Tests Container
 └── Firefox Tests Container

🔄 Cross-Browser Execution

Runs in parallel:

Browser	Execution
Chrome	✅
Firefox	✅
⚙️ Run Specific Browser
mvn clean test -Dbrowser=chrome
mvn clean test -Dbrowser=firefox

🧪 Run Headless Mode
mvn clean test -Dheadless=true

🏗️ Jenkins Integration

This framework supports Jenkins pipeline:

Parallel browser execution
Automated reporting
Email notifications
🤖 GitHub Actions (CI/CD)

Tests run automatically on:

Push to main branch
Pull requests

👉 Check Actions tab in GitHub

📁 Project Structure
.
├── docker-compose.yml
├── Dockerfile
├── Jenkinsfile
├── pom.xml
├── src/
│   └── test/
│       ├── java/
│       └── resources/
├── reports/

🛑 Stop Execution
docker-compose down

🚀 Scaling (Advanced)

Run multiple nodes:

docker-compose up --scale chrome=2 --scale firefox=2

💡 Key Features

✔ Fully portable (no setup required)
✔ Parallel + cross-browser execution
✔ Dockerized Selenium Grid
✔ CI/CD ready
✔ Thread-safe WebDriver (ThreadLocal)

🏆 Use Cases
Enterprise automation frameworks
CI/CD pipelines
Distributed test execution
Interview/demo projects
👨‍💻 Author

Guruvaiya Muthukaruppan

⭐ Support

If you like this project:

👉 Star the repo
👉 Share with your team

🎯 Final Note

This framework demonstrates a modern, scalable, and production-ready test automation architecture using Selenium Grid and Docker.