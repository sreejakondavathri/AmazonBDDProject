package steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.Assertions;

import pages.DealsPage;
import pages.HomePage;
import pages.SearchResultsPage;

import support.ConfigReader;
import support.DriverFactory;
import support.ScenarioContext;

public class AmazonSteps {

    private static final Logger log = LogManager.getLogger(AmazonSteps.class);

    private HomePage home;
    private DealsPage deals;
    private SearchResultsPage searchResults;

    private final ScenarioContext context = new ScenarioContext();

    private void initializePages() {

        home = new HomePage();
        deals = new DealsPage();
        searchResults = new SearchResultsPage();
    }

    @Given("I open the Amazon home page")
    public void openHomePage() {

        initializePages();

        home.open();

        Assertions.assertTrue(
                DriverFactory.getDriver()
                        .getCurrentUrl()
                        .startsWith(ConfigReader.get("base.url")),
                "Amazon home page did not open."
        );

        log.info("Opened Amazon home page");
    }

    @When("I open Today's Deals")
    public void openDeals() {

        home.openTodaysDeals();

        log.info("Opened Today's Deals");
    }

    @When("I open the third displayed deal")
    public void openThirdDeal() {

        deals.openThirdDeal();

        log.info("Opened the third visible deal card");
    }

    @When("I add the minimum available quantity of the item to the cart")
    public void addMinimumQuantity() {

        int quantity = deals.addMinimumQuantity();

        context.setExpectedCartQuantity(quantity);

        log.info("Added minimum quantity: {}", quantity);
    }

    @Then("the cart should contain the expected quantity")
    public void verifyCartQuantity() {

        int expected = context.getExpectedCartQuantity();

        int actual = deals.readCartQuantity();

        Assertions.assertEquals(
                expected,
                actual,
                "Expected cart quantity: " + expected +
                ", but actual cart quantity: " + actual
        );

        log.info(
                "Cart quantity verification successful. Expected: {}, Actual: {}",
                expected,
                actual
        );
    }

    @When("I search for {string}")
    public void searchFor(String term) {

        home.search(term);

        searchResults.waitForResults();

        log.info("Searched Amazon for: {}", term);
    }

    @When("I scroll to the last displayed search result")
    public void scrollToLastResult() {

        String details = searchResults.captureLastDisplayedProduct();

        context.setLastProductDetails(details);

        log.info("Last visible product details captured");
    }

    @Then("the last displayed product details should be captured")
    public void verifyLastProductCaptured() {

        Assertions.assertNotNull(
                context.getLastProductDetails(),
                "No product details were captured."
        );

        Assertions.assertFalse(
                context.getLastProductDetails().isBlank(),
                "Captured product details were blank."
        );

        log.info("Verified last displayed product details");
    }

    @When("I open the left navigation menu")
    public void openLeftMenu() {

        home.openLeftMenu();

        log.info("Opened left navigation menu");
    }

    @When("I navigate to Mobiles and Mobile Phones")
    public void navigateMobiles() {

        home.navigateToMobilesAndPhones();

        log.info("Selected Mobiles > Mobile Phones");
    }

    @Then("I should be able to return to the Amazon home page")
    public void returnHome() {

        home.returnHome();

        Assertions.assertTrue(
                home.isHomePage(),
                "Did not return to Amazon home page."
        );

        log.info("Returned to Amazon home page");
    }
}
