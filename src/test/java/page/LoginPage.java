package page;

import com.codeborne.selenide.SelenideElement;
import data.AuthInfo;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.page;

public class LoginPage {

    private SelenideElement loginField = $("[data-test-id='login'] input");
    private SelenideElement passwordField = $("[data-test-id='password'] input");
    private SelenideElement loginButton = $("[data-test-id='action-login']");

    public VerificationPage validLogin(AuthInfo authInfo) {
        loginField.shouldBe(visible).setValue(authInfo.getLogin());
        passwordField.setValue(authInfo.getPassword());
        loginButton.click();
        return page(VerificationPage.class);
    }
}