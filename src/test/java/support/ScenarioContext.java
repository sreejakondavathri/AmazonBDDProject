package support;

public class ScenarioContext {
    private int expectedCartQuantity = 1;
    private String lastProductDetails;

    public int getExpectedCartQuantity() {
        return expectedCartQuantity;
    }

    public void setExpectedCartQuantity(int expectedCartQuantity) {
        this.expectedCartQuantity = expectedCartQuantity;
    }

    public String getLastProductDetails() {
        return lastProductDetails;
    }

    public void setLastProductDetails(String lastProductDetails) {
        this.lastProductDetails = lastProductDetails;
    }
}
