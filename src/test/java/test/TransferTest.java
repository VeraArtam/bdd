package test;

import data.CardInfo;
import data.DataHelper;
import org.junit.jupiter.api.*;
import page.DashboardPage;
import page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransferTest {

    private DashboardPage dashboard;
    private CardInfo first;
    private CardInfo second;

    @BeforeEach
    void setUp() {
        open("http://localhost:9999");
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = new LoginPage().validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        dashboard = verificationPage.validVerify(verificationCode);

        first = DataHelper.getFirstCardInfo();
        second = DataHelper.getSecondCardInfo();
    }

    @Test
    @DisplayName("Успешный перевод с первой карты на вторую")
    void shouldTransferFromFirstToSecond() {
        int amount = 1000;

        int firstBefore = dashboard.getCardBalance(first.getId());
        int secondBefore = dashboard.getCardBalance(second.getId());

        dashboard.selectCardForTopUp(second.getId())
                .transfer(first.getNumber(), amount);

        int firstAfter = dashboard.getCardBalance(first.getId());
        int secondAfter = dashboard.getCardBalance(second.getId());

        assertEquals(firstBefore - amount, firstAfter);
        assertEquals(secondBefore + amount, secondAfter);
    }

    @Test
    @DisplayName("Успешный перевод со второй карты на первую")
    void shouldTransferFromSecondToFirst() {
        int amount = 500;

        int firstBefore = dashboard.getCardBalance(first.getId());
        int secondBefore = dashboard.getCardBalance(second.getId());

        dashboard.selectCardForTopUp(first.getId())
                .transfer(second.getNumber(), amount);

        int firstAfter = dashboard.getCardBalance(first.getId());
        int secondAfter = dashboard.getCardBalance(second.getId());

        assertEquals(firstBefore + amount, firstAfter);
        assertEquals(secondBefore - amount, secondAfter);
    }

    @Test
    @DisplayName("Перевод туда-обратно возвращает исходные балансы")
    void shouldRestoreBalancesAfterRoundTrip() {
        int firstBefore = dashboard.getCardBalance(first.getId());
        int secondBefore = dashboard.getCardBalance(second.getId());

        dashboard.selectCardForTopUp(second.getId())
                .transfer(first.getNumber(), 500);

        dashboard.selectCardForTopUp(first.getId())
                .transfer(second.getNumber(), 500);

        assertEquals(firstBefore, dashboard.getCardBalance(first.getId()));
        assertEquals(secondBefore, dashboard.getCardBalance(second.getId()));
    }

    @Test
    @DisplayName("Перевод на ту же карту не должен менять балансы обеих карт")
    void shouldNotTransferToSameCard() {
        int firstBefore = dashboard.getCardBalance(first.getId());
        int secondBefore = dashboard.getCardBalance(second.getId());

        dashboard.selectCardForTopUp(first.getId())
                .transfer(first.getNumber(), 100);

        assertEquals(firstBefore, dashboard.getCardBalance(first.getId()),
                "Баланс карты не должен меняться при переводе на саму себя");
        assertEquals(secondBefore, dashboard.getCardBalance(second.getId()),
                "Баланс второй карты не должен меняться при переводе на ту же карту");
    }

    @Test
    @DisplayName("Сумма балансов карт не меняется при переводе")
    void totalBalanceShouldStaySame() {
        int firstBefore = dashboard.getCardBalance(first.getId());
        int secondBefore = dashboard.getCardBalance(second.getId());

        dashboard.selectCardForTopUp(second.getId())
                .transfer(first.getNumber(), 300);

        int firstAfter = dashboard.getCardBalance(first.getId());
        int secondAfter = dashboard.getCardBalance(second.getId());

        assertEquals(firstBefore + secondBefore, firstAfter + secondAfter);
    }

    @Test
    @DisplayName("Перевод суммы больше баланса не должен менять балансы обеих карт")
    void shouldNotTransferMoreThanBalance() {
        int firstBefore = dashboard.getCardBalance(first.getId());
        int secondBefore = dashboard.getCardBalance(second.getId());
        int tooMuch = firstBefore + 1;

        dashboard.selectCardForTopUp(second.getId())
                .transferExpectingError(first.getNumber(), tooMuch);

        assertEquals(firstBefore, dashboard.getCardBalance(first.getId()),
                "Баланс карты-источника не должен меняться при отклонённом переводе");
        assertEquals(secondBefore, dashboard.getCardBalance(second.getId()),
                "Баланс карты-получателя не должен меняться при отклонённом переводе");
    }
}