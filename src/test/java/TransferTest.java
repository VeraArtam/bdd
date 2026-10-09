import org.junit.jupiter.api.*;
import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransferTest {

    private DashboardPage dashboard;

    @BeforeEach
    void setUp() {
        open("http://localhost:9999");
        dashboard = new LoginPage().validLogin("vasya", "qwerty123");
    }

    @Test
    @DisplayName("Успешный перевод с первой карты на вторую")
    void shouldTransferFromFirstToSecond() {
        int amount = 1000;

        int balanceFirstBefore = dashboard.getCardBalance("0001");
        int balanceSecondBefore = dashboard.getCardBalance("0002");

        dashboard.selectCardForTopUp("0002")
                .transfer("5559 0000 0000 0001", amount);   // полный номер            // с первой

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
    @DisplayName("Перевод суммы больше баланса")
    void shouldNotTransferMoreThanBalance() {
        int balanceFirst = dashboard.getCardBalance("0001");
        int tooMuch = balanceFirst + 1;

        dashboard.selectCardForTopUp("0002")
                .transferExpectingError("5559 0000 0000 0001", tooMuch);

        assertEquals(balanceFirst, dashboard.getCardBalance("0001"));
    }

    @Test
    @DisplayName("Перевод на ту же карту не должен менять баланс")
    void shouldNotTransferToSameCard() {
        int balanceBefore = dashboard.getCardBalance("0001");

        dashboard.selectCardForTopUp("0001")
                .transfer("5559 0000 0000 0001", 100);

        assertEquals(balanceBefore, dashboard.getCardBalance("0001"));
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