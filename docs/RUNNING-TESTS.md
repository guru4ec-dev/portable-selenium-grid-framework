# Running Tests

---

## Option 1: GitHub Actions (Recommended — no local setup needed)

**Trigger a run manually:**
1. Go to your GitHub repo → **Actions** tab
2. Select **Selenium Grid Tests**
3. Click **Run workflow** → **Run workflow**

Chrome and Firefox run as **parallel jobs** — both complete at the same time.

**Download reports after the run:**
1. Click the completed workflow run
2. Scroll down to **Artifacts**
3. Download `chrome-reports` or `firefox-reports`
4. Extract the zip and open `overview-features.html`

No Java, Maven, or Docker required — everything runs in GitHub's Cloud.

---

## Option 2: Docker locally (requires Docker Desktop)

```bat
docker-compose up --build
```

This spins up:
- Selenium Hub on `http://localhost:4444`
- Chrome Node
- Firefox Node
- Maven container that runs the tests

Reports saved to `reports/chrome/` and `reports/firefox/`.

Grid UI (while running): `http://localhost:4444`

---

## Configurable Screenshot Modes

Screenshot behaviour is controlled by the `-Dscreenshot.mode` property.

| Mode | When screenshot is taken |
|---|---|
| `failure` (default) | End of scenario, only if it failed |
| `step-all` | After every step (passed and failed) |
| `step-failed` | After each individual step that fails |

All screenshots are embedded directly in the HTML report beside the relevant step.

**GitHub Actions** — change the mode in `.github/workflows/selenium-tests.yml`:
```yaml
run: mvn verify -Dbrowser=chrome -Dscreenshot.mode=failure
```

**Docker local** — pass as a Maven property in `docker-compose.yml`:
```yaml
command: mvn verify -Dbrowser=chrome -Dscreenshot.mode=step-all
```

---

## Parallel Execution

Chrome and Firefox run as **separate jobs** in GitHub Actions — completely independent and simultaneous.

Within each job, scenarios run in parallel via `@DataProvider(parallel = true)` in `TestRunner.java`.
The number of concurrent scenarios is controlled by `data-provider-thread-count` in `testng.xml`.

**Scaling up:**

| Setting | File | Default |
|---|---|---|
| `data-provider-thread-count` | `testng.xml` | `3` |
| `SE_NODE_MAX_SESSIONS` | `docker-compose.yml` | `3` |

Keep both values equal so threads never exceed the Grid's session capacity.

**Example — scale to 5 parallel scenarios:**
```xml
<!-- testng.xml -->
<suite name="Suite" parallel="none" data-provider-thread-count="5">
```
```yaml
# docker-compose.yml
environment:
  SE_NODE_MAX_SESSIONS: 5
```

---

## CI/CD — GitHub Actions Workflow

File: `.github/workflows/selenium-tests.yml`

| Setting | Value |
|---|---|
| Trigger | Manual (`workflow_dispatch`) |
| Jobs | `chrome-tests` and `firefox-tests` in parallel |
| Reports | Uploaded as downloadable artifacts |
| Test data | `testdata.xlsx` committed to the repo — no external dependency |

**To also trigger on every push**, uncomment the `push` section in the workflow file:
```yaml
on:
  workflow_dispatch:
  push:
    branches: [main]
```
