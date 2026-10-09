import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
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
        fromCardField.shouldBe(visible).setValue(fromCardNumber);
        amountField.setValue(String.valueOf(amount));
        transferButton.click();
        return page(DashboardPage.class);
    }

    /**
     * Перевод с ожиданием ошибки (для негативных кейсов).
     */
    public TransferPage transferExpectingError(String fromCardNumber, int amount) {
        fromCardField.shouldBe(visible).setValue(fromCardNumber);
        amountField.setValue(String.valueOf(amount));
        transferButton.click();
        errorMessage.shouldBe(visible);
        return this;
    }
}