# Todo

- Fix datasource (1 central) ✅
- Change healthcheck to also query DB ✅
- Better migrations ✅
- Fix routing ✅
- Fix queries (CRUD User + Todos) ✅
- JWT (with RSA) ✅
- Auth middleware ✅
- Koin ✅
- CORS ✅
- Secret management (dotenv, .properties, .yaml) (dotenvx) ✅
- Request validation (Konform) ✅
- Code cleanup (app entry ✅, middlewares ✅)
- Exception handling & reporting (40x vs 50x) ✅
- Setup in command + Result monad ✅
- Fix Todo entity CRUD ✅
- Unit tests ✅
- Log SQL in dev and test ✅
- Integration tests (test containers with reuse) ✅
- Test commands with integration tests ✅

- JSON serialization (Date etc.) ✅
- Java dates (store and send in UTC) ✅

- Good logging (incl. exceptions)
- Different loggers for local, test, production ✅

- Lint & editorconfig ✅
- Gradle cleanup (version lock etc.)

- Check build
- Docker build (multi-stage) and run
- Build script (lint, format, unit tests, int. tests, build jar, docker build & run etc.)

## Problems

- The built jar cannot read the pem files from classpath
