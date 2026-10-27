package page;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.page;

public class TransferPage {

    private SelenideElement amountField = $("[data-test-id='amount'] input");
    private SelenideElement fromCardField = $("[data-test-id='from'] input");
    private SelenideElement transferButton = $("[data-test-id='action-transfer']");
    private SelenideElement errorMessage = $("[data-test-id='error-notification']");

    public TransferPage() {
        amountField.shouldBe(visible);
    }

    public DashboardPage transfer(String fromCardNumber, int amount) {
        fillAndSubmit(fromCardNumber, amount);
        return page(DashboardPage.class);
    }

    public TransferPage transferExpectingError(String fromCardNumber, int amount) {
        fillAndSubmit(fromCardNumber, amount);
        errorMessage.shouldBe(visible);
        return this;
    }

    private void fillAndSubmit(String fromCardNumber, int amount) {
        fromCardField.shouldBe(visible).press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE).setValue(fromCardNumber);
        amountField.press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE).setValue(String.valueOf(amount));
        transferButton.click();
    }
}