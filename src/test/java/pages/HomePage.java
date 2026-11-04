package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import support.ConfigReader;

import java.util.List;
import java.util.Locale;

public class HomePage extends BasePage {
    private final By dealsLink = By.xpath(
            "//a[contains(translate(normalize-space(.), \"ABCDEFGHIJKLMNOPQRSTUVWXYZ\", \"abcdefghijklmnopqrstuvwxyz\"), \"today's deals\") " +
            "or contains(translate(@aria-label, \"ABCDEFGHIJKLMNOPQRSTUVWXYZ\", \"abcdefghijklmnopqrstuvwxyz\"), \"today's deals\")]"
    );
    private final By searchBox = By.id("twotabsearchtextbox");
    private final By searchButton = By.id("nav-search-submit-button");
    private final By hamburger = By.id("nav-hamburger-menu");
    private final By amazonLogo = By.id("nav-logo");
    private final By menuLinks = By.cssSelector("#hmenu-content a");

    public void open() {
        driver.get(ConfigReader.get("base.url"));
        if (driver.getCurrentUrl().contains("/ap/signin")
                || !findAll(By.cssSelector("input#ap_email, input[name='email'], input[name='password']")).isEmpty()) {
            throw new IllegalStateException(
                    "Amazon redirected this browser session to sign-in. This public-only test does not enter personal information. "
                    + "Stop the scenario and retry later or use an approved public test environment."
            );
        }
        visible(searchBox);
    }

    public void openTodaysDeals() {
        List<WebElement> links = findAll(dealsLink);
        WebElement visibleDealsLink = links.stream()
                .filter(WebElement::isDisplayed)
                .filter(WebElement::isEnabled)
                .findFirst()
                .orElse(null);
        if (visibleDealsLink != null) {
            visibleDealsLink.click();
        } 
        else {
            // Fallback when Amazon changes the navigation link's text or markup.
            driver.get("https://www.amazon.in/gp/goldbox?ref_=nav_cs_gb");
        }
        wait.until(d -> d.getCurrentUrl().contains("goldbox")
                || !findAll(By.cssSelector("[data-testid*='deal'], .DealGridItem, #dealImage")).isEmpty());
    }

    public void search(String term) {
        type(searchBox, term);
        click(searchButton);
    }

    public void openLeftMenu() {
        click(hamburger);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("hmenu-content")));
    }

    public void navigateToMobilesAndPhones() {

        // First select Mobiles, Computers
        clickMenuItem(
            By.xpath("//div[@id='hmenu-content']//a[contains(normalize-space(.),'Mobiles, Computers')]")
        );

        // Then select All Mobile Phones
        clickMenuItem(
            By.xpath("//div[@id='hmenu-content']//a[contains(normalize-space(.),'All Mobile Phones')]")
        );
    }
    
    private void clickMenuItem(By locator) {

        List<WebElement> elements = findAll(locator);

        WebElement target = elements.stream()
                .filter(WebElement::isDisplayed)
                .filter(element -> {
                    try {
                        return element.isEnabled();
                    } catch (Exception e) {
                        return false;
                    }
                })
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Visible menu item not found: " + locator));

        scrollIntoView(target);

        try {
            wait.until(
                    org.openqa.selenium.support.ui.ExpectedConditions
                            .elementToBeClickable(target));

            target.click();

        } 
        catch (org.openqa.selenium.ElementClickInterceptedException e) {
            // Fall back to JavaScript when the normal click is intercepted.
            ((org.openqa.selenium.JavascriptExecutor) driver)
                    .executeScript("arguments[0].click();", target);
        }
    }

    public void returnHome() {
        click(amazonLogo);
        visible(searchBox);
    }

    public boolean isHomePage() {
        return driver.getCurrentUrl().startsWith(ConfigReader.get("base.url"))
                && !driver.getCurrentUrl().contains("/s?");
    }
}
