package test;

import data.DataHelper;
import org.junit.jupiter.api.*;
import page.DashboardPage;
import page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransferTest {

    private DashboardPage dashboard;

    @BeforeEach
    void setUp() {
        open("http://localhost:9999");
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = new LoginPage().validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        dashboard = verificationPage.validVerify(verificationCode);
    }

    @Test
    @DisplayName("Успешный перевод с первой карты на вторую")
    void shouldTransferFromFirstToSecond() {
        int amount = 1000;

        int balanceFirstBefore = dashboard.getCardBalance("0001");
        int balanceSecondBefore = dashboard.getCardBalance("0002");

        dashboard.selectCardForTopUp("0002")
                .transfer("5559 0000 0000 0001", amount);
        int balanceFirstAfter = dashboard.getCardBalance("0001");
        int balanceSecondAfter = dashboard.getCardBalance("0002");

        assertEquals(balanceFirstBefore - amount, balanceFirstAfter);
        assertEquals(balanceSecondBefore + amount, balanceSecondAfter);
    }

    @Test
    @DisplayName("Успешный перевод со второй карты на первую")
    void shouldTransferFromSecondToFirst() {
        int amount = 500;

        int balanceFirstBefore = dashboard.getCardBalance("0001");
        int balanceSecondBefore = dashboard.getCardBalance("0002");

        dashboard.selectCardForTopUp("0001")
                .transfer("5559 0000 0000 0002", amount);

        int balanceFirstAfter = dashboard.getCardBalance("0001");
        int balanceSecondAfter = dashboard.getCardBalance("0002");

        assertEquals(balanceFirstBefore + amount, balanceFirstAfter);
        assertEquals(balanceSecondBefore - amount, balanceSecondAfter);
    }

    @Test
    @DisplayName("Перевод суммы больше баланса не должен менять балансы обеих карт")
    void shouldNotTransferMoreThanBalance() {
        int balanceFirstBefore = dashboard.getCardBalance("0001");
        int balanceSecondBefore = dashboard.getCardBalance("0002");
        int tooMuch = balanceFirstBefore + 1;

        dashboard.selectCardForTopUp("0002")
                .transferExpectingError("5559 0000 0000 0001", tooMuch);

        int balanceFirstAfter = dashboard.getCardBalance("0001");
        int balanceSecondAfter = dashboard.getCardBalance("0002");

        assertEquals(balanceFirstBefore, balanceFirstAfter,
                "Баланс карты-источника не должен меняться при отклонённом переводе");
        assertEquals(balanceSecondBefore, balanceSecondAfter,
                "Баланс карты-получателя не должен меняться при отклонённом переводе");
    }

    @Test
    @DisplayName("Перевод на ту же карту не должен менять балансы обеих карт")
    void shouldNotTransferToSameCard() {
        int balanceFirstBefore = dashboard.getCardBalance("0001");
        int balanceSecondBefore = dashboard.getCardBalance("0002");

        dashboard.selectCardForTopUp("0001")
                .transfer("5559 0000 0000 0001", 100);

        int balanceFirstAfter = dashboard.getCardBalance("0001");
        int balanceSecondAfter = dashboard.getCardBalance("0002");

        assertEquals(balanceFirstBefore, balanceFirstAfter,
                "Баланс карты не должен меняться при переводе на саму себя");
        assertEquals(balanceSecondBefore, balanceSecondAfter,
                "Баланс второй карты не должен меняться при переводе на ту же карту");
    }

    @Test
    @DisplayName("Перевод туда-обратно возвращает исходные балансы")
    void shouldRestoreBalancesAfterRoundTrip() {
        int firstBefore = dashboard.getCardBalance("0001");
        int secondBefore = dashboard.getCardBalance("0002");

        dashboard.selectCardForTopUp("0002")
                .transfer("5559 0000 0000 0001", 500);

        dashboard.selectCardForTopUp("0001")
                .transfer("5559 0000 0000 0002", 500);

        assertEquals(firstBefore, dashboard.getCardBalance("0001"));
        assertEquals(secondBefore, dashboard.getCardBalance("0002"));
    }

    @Test
    @DisplayName("Сумма балансов карт не меняется при переводе")
    void totalBalanceShouldStaySame() {
        int firstBefore = dashboard.getCardBalance("0001");
        int secondBefore = dashboard.getCardBalance("0002");

        dashboard.selectCardForTopUp("0002")
                .transfer("5559 0000 0000 0001", 300);

        int firstAfter = dashboard.getCardBalance("0001");
        int secondAfter = dashboard.getCardBalance("0002");

        assertEquals(firstBefore + secondBefore, firstAfter + secondAfter);
    }
}