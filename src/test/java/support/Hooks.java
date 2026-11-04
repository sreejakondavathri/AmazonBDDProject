package support;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

public class Hooks {
    private static final Logger log = LogManager.getLogger(Hooks.class);

    @Before
    public void setUp(Scenario scenario) {
        log.info("Starting scenario: {}", scenario.getName());
        DriverFactory.createDriver();
    }

    @After
    public void tearDown(Scenario scenario) {
        WebDriver driver = null;
        try {
            driver = DriverFactory.getDriver();
            if (scenario.isFailed()) {
                byte[] screenshot = ScreenshotUtil.captureBytes(driver);
                scenario.attach(screenshot, "image/png", "Failure screenshot");
                log.error("Scenario failed: {}. Screenshot attached to report.", scenario.getName());
                log.error("Screenshot saved at: {}", ScreenshotUtil.save(driver, scenario.getName()));
            } else {
                log.info("Scenario passed: {}", scenario.getName());
            }
        } catch (Exception e) {
            log.warn("Could not capture screenshot/finish scenario cleanup: {}", e.getMessage());
        } finally {
            DriverFactory.quitDriver();
        }
    }
}
