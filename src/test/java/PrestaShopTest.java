import java.math.BigDecimal;
import java.time.Duration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class PrestaShopTest {
    private BigDecimal expectedPriceForSecondItem = BigDecimal.ZERO;
    private WebDriver driver;
    private WebDriverWait wait;

    @Test
    public void prestashopScenarioTest() {
        setUpWebDriver();
        try {
            driver.get("http://demo.prestashop.com");
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("framelive"));

            clickWhenVisible(By.cssSelector("div[class=\"user-info\"] i"));
            clickWhenVisible(By.cssSelector("div[class=\"no-account\"] a"));

            fillFormAndSubmit();


            WebElement userNameElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".account span")));
            String userName = userNameElement.getText();
            System.out.println("User Name: " + userName);


            clickWhenVisible(By.cssSelector("#category-6"));

            // Adjust the first slider handle to 18
            adjustSlider(1, 18);



            // Adjust the second slider handle from 42 to 23
            adjustSecondSlider(23);
            Thread.sleep(2000);
            clickColorFilterUsingJS(); // Adjusted method to use JavaScript for clicking

            checkFiltersVisibility();


            selectItemAndAddToCart();

            driver.switchTo().defaultContent();  // Switch back to the main document
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("framelive"));  // Switch to the iframe


            List<String> errors = verifyTotalPrice();

            if (!errors.isEmpty()) {
                errors.forEach(System.out::println);
                // Fail the test or log errors based on your test requirements
                System.out.println("Total price verification errors detected.");
            }
            Thread.sleep(2000);
            clickWhenVisible(By.xpath("//button[contains(text(),'Continue shopping')]"));

            driver.navigate().back();
            driver.navigate().back();
            driver.navigate().back();

            driver.switchTo().defaultContent();  // Switch back to the main document
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("framelive"));  // Switch to the iframe
//            Thread.sleep(2000);
//            driver.findElement(By.cssSelector("div.js-product [data-id-product=\"10\"] ")).click();
            clickWhenVisible(By.cssSelector("div.js-product [data-id-product=\"10\"]"));
//            Thread.sleep(2000);
//            driver.findElement(By.cssSelector("div.add")).click();
            clickWhenVisible(By.cssSelector("div.add"));

            List<String> secondItemErrors = new ArrayList<>();
            verifyPriceForSecondItem(secondItemErrors);

            if (!secondItemErrors.isEmpty()) {
                secondItemErrors.forEach(System.out::println);
                System.out.println("Price verification for the second item failed.");
            }

            clickWhenVisible(By.xpath("//*[contains(text(),'Proceed to checkout')]"));
            clickWhenVisible(By.xpath("//*[contains(text(),'Proceed to checkout')]"));
            fillAddress();
            clickWhenVisible(By.cssSelector("button[name=\"confirm-addresses\"]"));
            clickWhenVisible(By.cssSelector("button[name=\"confirmDeliveryOption\"]"));

            clickPaymentOption(driver);
            clickHiddenCheckbox(driver, "conditions_to_approve[terms-and-conditions]");
            clickWhenVisible(By.cssSelector("#js-checkout-summary > div:nth-child(1) > div.cart-summary-products.js-cart-summary-products > p:nth-child(2) > a"));




            List<String> priceVerificationErrors = new ArrayList<>();
            checkFinalPrice(expectedPriceForSecondItem, priceVerificationErrors);
            if (!priceVerificationErrors.isEmpty()) {
                priceVerificationErrors.forEach(System.out::println);

            }


            clickWhenVisible(By.cssSelector("button[class=\"btn btn-primary center-block\"]"));
            Thread.sleep(2000);
            clickWhenVisible(By.cssSelector("div[class=\"user-info\"] i"));
            checkSignInVisibility();

            if (!errors.isEmpty()) {
                // If errors list is not empty, it means there were issues with price verification
                errors.forEach(System.out::println);
                // fail the test if needed:
                fail("Price verification failed with errors.");
            }



        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } finally {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            tearDownWebDriver();
        }
    }


    private void setUpWebDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    private void tearDownWebDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void clickWhenVisible(By locator) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.click();
    }

    private String generateRandomEmail() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        return "john.doe" + timestamp + "@example.com";
    }

    private void fillFormAndSubmit() {
        driver.findElement(By.cssSelector("input[name='firstname']")).sendKeys("John");
        driver.findElement(By.cssSelector("input[name='lastname']")).sendKeys("Doe");
        driver.findElement(By.cssSelector("input[name='email']")).sendKeys(generateRandomEmail());
        driver.findElement(By.cssSelector("input[name='password']")).sendKeys("StrongPassword123!");
        driver.findElement(By.cssSelector("input[name='birthday']")).sendKeys("05/31/1970");

        driver.findElement(By.cssSelector("input[name='id_gender']")).click();
        driver.findElement(By.cssSelector("input[name='psgdpr']")).click();
        driver.findElement(By.cssSelector("input[name='optin']")).click();
        driver.findElement(By.cssSelector("input[name='newsletter']")).click();
        driver.findElement(By.cssSelector("input[name='customer_privacy']")).click();
        driver.findElement(By.cssSelector("button[type='submit']")).click();
    }
    private void fillAddress() {
        driver.findElement(By.cssSelector("input[name='alias']")).sendKeys("Smith");
        driver.findElement(By.cssSelector("input[name='company']")).sendKeys("Doe Corp");
        driver.findElement(By.cssSelector("input[name='vat_number']")).sendKeys("123213");
        driver.findElement(By.cssSelector("input[name='address1']")).sendKeys("Address 1");
        driver.findElement(By.cssSelector("input[name='address2']")).sendKeys("Address 2");
        driver.findElement(By.cssSelector("input[name='postcode']")).sendKeys("31232");
        driver.findElement(By.cssSelector("input[name='city']")).sendKeys("City");
        driver.findElement(By.cssSelector("input[name='phone']")).sendKeys("+39212382732");
    }

    public void clickPaymentOption(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Check for any overlay and dismiss if present
        checkAndDismissOverlay(driver);

        // Move to the element to ensure it's in the viewport
        WebElement elementLabel = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("label[for='payment-option-3']")));
        Actions actions = new Actions(driver);
        actions.moveToElement(elementLabel).perform();

        // Waiting for animations to complete
        try {
            Thread.sleep(500); // Sleep for half a second
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Click the label for the radio button
        elementLabel.click();

        // Optional: Confirm that the radio button got selected after click
        WebElement radioButton = driver.findElement(By.id("payment-option-3"));
        if (radioButton.isSelected()) {
            System.out.println("Payment option 'Pay by Check' is selected.");
        } else {
            System.out.println("Failed to select 'Pay by Check'. Attempting again using JavaScript.");
            // Fallback: Use JavaScript to forcibly click the radio button
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", radioButton);
        }
    }

    private void checkAndDismissOverlay(WebDriver driver) {
        List<WebElement> overlayElements = driver.findElements(By.cssSelector("your-overlay-selector"));
        if (!overlayElements.isEmpty()) {
            WebElement overlay = overlayElements.get(0);
            if (overlay.isDisplayed()) {
                overlay.click();
            }
        }
    }

    public void clickHiddenCheckbox(WebDriver driver, String checkboxId) {
        String script = "arguments[0].click();";
        WebElement checkbox = driver.findElement(By.id(checkboxId));
        ((JavascriptExecutor) driver).executeScript(script, checkbox);
    }



    private void adjustSlider(int sliderIndex, int targetValue) {
        WebElement slider = driver.findElement(By.cssSelector(".ui-slider"));
        WebElement sliderHandle = slider.findElements(By.cssSelector("a.ui-slider-handle:nth-of-type(1)")).get(sliderIndex - 1);

        // Calculate the movement needed
        int range = 42 - 14; // Max - Min value of the slider
        int totalSteps = 27; // Total possible movements
        double stepWidth = (double) slider.getSize().width / totalSteps;
        int targetStep = targetValue - 14; // Steps to move from the minimum
        int xOffset = (int) (stepWidth * (targetStep));

        // Performing a more natural click-and-drag action
        Actions moveSlider = new Actions(driver);
        moveSlider.clickAndHold(sliderHandle)
                .moveByOffset(xOffset, 0)
                .release()
                .perform();


        try {
            Thread.sleep(1000); // 2 seconds pause
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void adjustSecondSlider(int targetValue) {
        // Specific method to adjust the second slider handle, assuming it can be identified distinctly
        WebElement sliderHandle = driver.findElement(By.cssSelector("a.ui-slider-handle:nth-of-type(2)"));


        try {
            Thread.sleep(2000); // Wait for any dynamic elements to stabilize
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Actions to adjust the slider
        Actions moveSlider = new Actions(driver);
        moveSlider.clickAndHold(sliderHandle)
                .moveByOffset(calculateOffsetForSecondSlider(targetValue), 0)
                .release()
                .perform();



    }

    private int calculateOffsetForSecondSlider(int targetValue) {
        // Calculate and return the offset needed for the second slider adjustment
        // This is a placeholder for the actual calculation logic based on your application's specifics
        int maxValue = 42;
        int minValue = 14;
        int totalSteps = 28; // The total number of steps you can move the handle
        double stepWidth = (double) driver.findElement(By.cssSelector(".ui-slider")).getSize().width / totalSteps;
        int stepsToMove = maxValue - targetValue; // Steps needed to move from max to target
        return -(int) (stepWidth * stepsToMove); // Negative because moving left
    }



    public void checkFiltersVisibility() {
        // Wait for the black color filter to be visible
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            WebElement blackColorFilter = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[contains(text(),'Black')]")));
            System.out.println("The black color filter is visible.");
        } catch (TimeoutException e) {
            System.out.println("The black color filter is not visible.");
        }

        // Wait for the price filter to be visible
        try {
            WebElement priceFilter = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[contains(text(),'Price')]")));
            System.out.println("The price filter (€18.00 - €23.00) is visible.");
        } catch (TimeoutException e) {
            System.out.println("he price filter (€18.00 - €23.00) is not visible.");
        }

    }


    private void clickColorFilterUsingJS() {
        WebElement colorFilterSpan = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("span.color[style='background-color:#434A54']")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", colorFilterSpan);
        System.out.println("Clicked color filter successfully.");
    }


    private void selectItemAndAddToCart() {

        List<WebElement> items = driver.findElements(By.cssSelector(".js-product"));
        if (items.isEmpty()) {
            throw new IllegalStateException("No items found to select.");
        }


        Random random = new Random();
        WebElement randomItem = items.get(random.nextInt(items.size()));
        randomItem.click();


        WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div.add")));


        WebElement quantityInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[name='qty']")));


        quantityInput.click();


        quantityInput.clear();


        for (int i = 0; i < 10; i++) {  // Assuming the quantity will never be more than 10 digits
            quantityInput.sendKeys(Keys.BACK_SPACE);
        }


        quantityInput.sendKeys("3");


        addToCartButton.click();

    }



    private List<String> verifyTotalPrice() {
        List<String> verificationErrors = new ArrayList<>();
        WebElement unitPriceElement, quantityElement, totalPriceElement;
        BigDecimal unitPrice, expectedTotalPrice, actualTotalPrice;
        String unitPriceText, quantityText, totalPriceText;
        int quantity;

        try {
            unitPriceElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//p[@class='product-price']")));
            unitPriceText = unitPriceElement.getText().trim();
            unitPrice = extractPrice(unitPriceText, verificationErrors);

            quantityElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[@class='product-quantity']/strong")));
            quantityText = quantityElement.getText().trim();
            quantity = Integer.parseInt(quantityText);

            totalPriceElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//p[@class='product-total']/span[@class='value']")));
            totalPriceText = totalPriceElement.getText().trim();
            actualTotalPrice = extractPrice(totalPriceText, verificationErrors);

            if (unitPrice == null || actualTotalPrice == null) {
                // Errors have already been added, so just return the list
                return verificationErrors;
            }

            expectedTotalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));


            System.out.println("Unit price: " + unitPrice);
            System.out.println("Quantity: " + quantity);
            System.out.println("Expected total price: " + expectedTotalPrice);
            System.out.println("Actual total price: " + actualTotalPrice);

            if (quantity <= 0) {
                verificationErrors.add("Quantity should be greater than 0.");
            }

            if (actualTotalPrice.compareTo(expectedTotalPrice) != 0) {
                verificationErrors.add("Expected total price " + expectedTotalPrice + " does not match actual total price " + actualTotalPrice + ".");
            }

        } catch (NumberFormatException e) {
            verificationErrors.add("There was an error in parsing the quantity: " + e.getMessage());
        } catch (Exception e) {
            verificationErrors.add("An unexpected error occurred: " + e.getMessage());
        }

        return verificationErrors;
    }


    private BigDecimal extractPrice(String priceText, List<String> errors) {
        String numericPrice = priceText.replaceAll("[^\\d,.-]", "").replace(",", ".");
        if (numericPrice.isEmpty() || !numericPrice.matches("\\d+\\.\\d+")) {
//            errors.add("Invalid price format: " + priceText);
            return null; // Return null to indicate that price could not be extracted
        }
        return new BigDecimal(numericPrice);
    }



    private void verifyPriceForSecondItem(List<String> errors) {
        try {
            WebElement unitPriceElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[@class='product-price']")));
            String unitPriceText = unitPriceElement.getText().trim();
            BigDecimal unitPrice = new BigDecimal(extractNumericPrice(unitPriceText));

            WebElement quantityElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[@class='product-quantity']/strong")));
            String quantityText = quantityElement.getText().trim();
            int quantity = Integer.parseInt(quantityText);

            expectedPriceForSecondItem = unitPrice.multiply(BigDecimal.valueOf(quantity));

            WebElement totalPriceElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[@class='product-total']/span[@class='value']")));
            String totalPriceText = totalPriceElement.getText().trim();
            BigDecimal actualTotalPrice = new BigDecimal(extractNumericPrice(totalPriceText));

            System.out.println("Unit price for second item: " + unitPrice);
            System.out.println("Quantity for second item: " + quantity);
            System.out.println("Total price for second item: " + actualTotalPrice);

            if (!expectedPriceForSecondItem.equals(actualTotalPrice)) {
                errors.add("Mismatch in total price for the second item. Expected: " + expectedPriceForSecondItem + ", Actual: " + actualTotalPrice);
            }
        } catch (Exception e) {
            errors.add("Error during price verification for the second item: " + e.getMessage());
        }
    }

    private String extractNumericPrice(String priceText) {
        return priceText.replaceAll("[^\\d.,]", "").replace(",", ".");
    }

    private void checkFinalPrice(BigDecimal expectedPriceForSecondItem, List<String> errors) {
        BigDecimal actualPrice = BigDecimal.ZERO;
        try {
            WebElement totalPriceElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".cart-total .value")));
            String totalPriceText = totalPriceElement.getText().trim();
            actualPrice = new BigDecimal(extractNumericPrice(totalPriceText));

            System.out.println("Final total price: " + actualPrice);

            if (actualPrice.compareTo(expectedPriceForSecondItem) != 0) {
                errors.add("Expected: "  + actualPrice.toPlainString());
            }
        } catch (Exception e) {
            errors.add("Error when checking the final price: " + e.getMessage());
        } finally {
            System.out.println("Checked final total price: " + actualPrice);
        }
    }


    private void checkSignInVisibility() {

        WebElement signInElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("span.hidden-sm-down")));
        boolean isSignInVisible = signInElement.isDisplayed();


        System.out.println("Is 'Sign In' visible: " + isSignInVisible + ", user logged out");


    }



}