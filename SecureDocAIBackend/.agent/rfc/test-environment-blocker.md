# RFC: Test environment blocker — H2 adoption didn't resolve all failures

## Summary
H2 test configuration added to `src/test/resources/application.properties`. Tests still fail by timing out.

## Observed failures
The test command `mvn -B test` consistently times out after 10 seconds, even when running a single test. This prevents the capture of any meaningful stack traces or error messages.

## Likely causes
The timeout suggests a fundamental issue during the Spring application context startup. Possible causes include:
- **Infinite Loop or Deadlock:** A component in the application may be entering an infinite loop or a deadlock during initialization.
- **Resource Starvation:** The test environment may be starved for resources, causing the application to hang.
- **External Service Dependency:** The application may be trying to connect to an external service that is not available in the test environment, and the connection attempt is not timing out quickly enough.
- **Incorrect property keys for security/credentials:** The application may be expecting different property keys for the JWT secret, causing a hang during security initialization.

## Proposed conservative workarounds (pick one)
1. **Add Testcontainers Postgres:** Use Testcontainers to spin up a disposable PostgreSQL instance for tests. This would ensure the test environment is as close to production as possible. (Medium)
2. **Debug the Timeout:** Attach a debugger to the test process to identify the source of the hang. This would involve running the tests with debugging enabled and stepping through the code to find the point where it gets stuck. (Large)
3. **Mock External Integrations:** If the issue is an external service, mock the service so that the application context can load without it. (Small)

## Recommended next step
The most pressing issue is to identify the cause of the timeout. I recommend attaching a debugger to the test process to pinpoint the source of the hang. This will provide the necessary information to determine the best course of action.
