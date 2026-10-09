import com.codeborne.selenide.SelenideElement;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.page;

public class LoginPage {

    private SelenideElement loginField = $("[data-test-id='login'] input");
    private SelenideElement passwordField = $("[data-test-id='password'] input");
    private SelenideElement loginButton = $("[data-test-id='action-login']");
    private SelenideElement codeField = $("[data-test-id='code'] input");
    private SelenideElement verifyButton = $("[data-test-id='action-verify']");

    public DashboardPage validLogin(String login, String password) {
        loginField.shouldBe(visible).setValue(login);
        passwordField.setValue(password);
        loginButton.click();
        codeField.shouldBe(visible).setValue("12345");
        verifyButton.click();
        return page(DashboardPage.class);
    }
}
