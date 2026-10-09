import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.page;

public class DashboardPage {

    private ElementsCollection cards = $$(".list__item");
    private SelenideElement heading = $("[data-test-id='dashboard']");

    private final String balanceStart = "баланс: ";
    private final String balanceFinish = " р.";

    public DashboardPage() {
        heading.shouldBe(visible);
    }

    /**
     * Возвращает баланс карты по последним 4 цифрам номера.
     * Пример текста карты: "5559 0000 0000 0001 10 000 ₽"
     */
    public int getCardBalance(String lastFourDigits) {
        SelenideElement card = cards.findBy(
                com.codeborne.selenide.Condition.text(lastFourDigits)
        );
        String text = card.getText();
        return extractBalance(text);
    }

    /**
     * Кликает "Пополнить" на карте, которую хотим пополнить.
     * Возвращает страницу перевода.
     */
    public TransferPage selectCardForTopUp(String lastFourDigits) {
        SelenideElement card = cards.findBy(
                com.codeborne.selenide.Condition.text(lastFourDigits)
        );
        card.find("[data-test-id='action-deposit']").click();
        return page(TransferPage.class);
    }

    private int extractBalance(String text) {
        int start = text.indexOf(balanceStart);
        int finish = text.indexOf(balanceFinish);
        if (start == -1 || finish == -1) {
            throw new IllegalStateException(
                    "Не нашли баланс в тексте карты: [" + text + "]"
            );
        }
        String value = text.substring(start + balanceStart.length(), finish);
        // на случай пробелов внутри числа: "10 000" -> "10000"
        value = value.replaceAll("\\s+", "");
        return Integer.parseInt(value);
    }

    private SelenideElement findCard(String lastFourDigits) {
        return cards.findBy(text(lastFourDigits));
    }
}