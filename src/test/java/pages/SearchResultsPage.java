package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class SearchResultsPage extends BasePage {
    private final By results = By.cssSelector("div[data-component-type='s-search-result']");

    public void waitForResults() {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(results));
    }

    public String captureLastDisplayedProduct() {
        waitForResults();
        List<WebElement> cards = findAll(results).stream()
                .filter(WebElement::isDisplayed)
                .toList();

        if (cards.isEmpty()) {
            throw new IllegalStateException("No visible search result cards found.");
        }

        WebElement last = cards.get(cards.size() - 1);
        scrollIntoView(last);

        String title = last.findElements(By.cssSelector("h2 a span")).stream()
                .findFirst().map(WebElement::getText).orElse("Title not found");
        String price = last.findElements(By.cssSelector(".a-price .a-offscreen")).stream()
                .findFirst().map(WebElement::getText).orElse("Price not displayed");
        String link = last.findElements(By.cssSelector("h2 a")).stream()
                .findFirst().map(e -> e.getAttribute("href")).orElse("Product URL not found");

        return "Title: " + title + " | Price: " + price + " | URL: " + link;
    }
}
