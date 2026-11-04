package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

public class DealsPage extends BasePage {

    private final By dealCards = By.cssSelector("div[data-testid*='product-card'], div.DealGridItem, [data-testid='deal-card']");

    private final By addToCart = By.id("add-to-cart-button");
    private final By quantity = By.id("quantity");

    // Amazon cart icon and cart badge
    private final By cartLink = By.cssSelector("a#nav-cart, a[href*='/cart']");

    private final By cartBadge = By.cssSelector("#nav-cart-count, .nav-cart-count");

    // Quantity controls on the cart page
    private final By cartQuantity = By.cssSelector(
            "select[name='quantity'], " +
            "input[name='quantityBox'], " +
            "input[name='quantity']");

    public void openThirdDeal() {
        List<WebElement> cards = findAll(dealCards).stream()
                .filter(WebElement::isDisplayed)
                .toList();

        if (cards.size() < 3) {
            throw new IllegalStateException("Fewer than 3 deal cards were found.");
        }

        WebElement third = cards.get(2);
        scrollIntoView(third);

        List<WebElement> links = third.findElements(
                By.cssSelector("a[href*='/dp/'], a[href*='/gp/']"));
        if (links.isEmpty()) {
            throw new IllegalStateException("Product link not found inside the third deal card.");
        }

        links.get(0).click();
        visible(addToCart);
    }

    public int addMinimumQuantity() {

        if (!findAll(quantity).isEmpty()) {
            WebElement quantityElement = visible(quantity);
            Select select = new Select(quantityElement);
            try {
                select.selectByValue("1");
            } 
            catch (org.openqa.selenium.NoSuchElementException e) {
                select.selectByIndex(0);
            }
        }
        click(addToCart);

        // Wait briefly for Amazon to update the cart badge.
        wait.until(driver -> {
            List<WebElement> badges = driver.findElements(cartBadge);
            if (badges.isEmpty()) {
                return false;
            }
            String value = badges.get(0).getText().trim();
            return !value.isEmpty() && parseQuantity(value) > 0;
        });

        return 1;
    }

    public int readCartQuantity() {

        // Open the cart.
        click(cartLink);

        // Wait until either the cart badge or cart quantity control appears.
        wait.until(driver -> {
            List<WebElement> badges = driver.findElements(cartBadge);

            boolean badgeHasQuantity = badges.stream()
                    .filter(WebElement::isDisplayed)
                    .anyMatch(element -> parseQuantity(element.getText()) > 0);

            boolean quantityControlExists = driver
                    .findElements(cartQuantity)
                    .stream()
                    .anyMatch(WebElement::isDisplayed);

            return badgeHasQuantity || quantityControlExists;
        });

        // First verify the cart badge.
        List<WebElement> badges = findAll(cartBadge).stream()
                .filter(WebElement::isDisplayed)
                .toList();

        if (!badges.isEmpty()) {
            int badgeQuantity = parseQuantity(
                    badges.get(0).getText());
            if (badgeQuantity > 0) {
                return badgeQuantity;
            }
        }

        // Fallback: read the actual cart quantity control.
        List<WebElement> quantities = findAll(cartQuantity).stream()
                .filter(WebElement::isDisplayed)
                .toList();

        if (quantities.isEmpty()) {
            throw new IllegalStateException(
                    "Neither cart badge nor cart quantity control was found. " +
                    "Check whether Amazon added the item successfully.");
        }

        WebElement itemQuantity = quantities.get(0);
        String value;

        if (itemQuantity.getTagName().equalsIgnoreCase("select")) {
            value = new Select(itemQuantity)
                    .getFirstSelectedOption()
                    .getText();
        } 
        else {
            value = itemQuantity.getAttribute("value");
            if (value == null || value.isBlank()) {
                value = itemQuantity.getText();
            }
        }
        return parseQuantity(value);
    }

    private int parseQuantity(String value) {
        if (value == null || value.isBlank()) {
            return 0;
        }
        String numericValue = value.replaceAll("[^0-9]", "");
        if (numericValue.isBlank()) {
            return 0;
        }
        return Integer.parseInt(numericValue);
    }
}
