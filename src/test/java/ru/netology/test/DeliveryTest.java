package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static ru.netology.data.DataGenerator.*;
import static ru.netology.data.DataGenerator.Registration.*;

class DeliveryTest {

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    public void setUp() {
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Happy Path")
    public void happyPath() {
        var validUser = generateUser();
        var meetingDate = generateDate(3);
        $("[data-test-id='city'] input").setValue(validUser.getCity());
        $("[data-test-id='date'] input").doubleClick().sendKeys(meetingDate);
        $("[data-test-id='name'] input").setValue(validUser.getName());
        $("[data-test-id='phone'] input").setValue(validUser.getPhone());
        $("[data-test-id='agreement']").click();
        $(withText("Запланировать")).click();
        $("[data-test-id='success-notification'] [class='notification__content']")
                .should(appear).shouldHave(exactText("Встреча успешно запланирована на " + meetingDate));
    }

    @Test
    @DisplayName("Should change meeting date")
    public void changingDeliveryDate() {
        var validUser = generateUser();
        var firstMeetingDate = generateDate(4);
        var secondMeetingDate = generateDate(5);
        $("[data-test-id='city'] input").setValue(validUser.getCity());
        $("[data-test-id='date'] input").doubleClick().sendKeys(firstMeetingDate);
        $("[data-test-id='name'] input").setValue(validUser.getName());
        $("[data-test-id='phone'] input").setValue(validUser.getPhone());
        $("[data-test-id='agreement']").click();
        $(withText("Запланировать")).click();
        $("[data-test-id='success-notification'] [class='notification__content']")
                .should(appear).shouldHave(exactText("Встреча успешно запланирована на " + firstMeetingDate));
        $("[data-test-id='date'] input").doubleClick().sendKeys(secondMeetingDate);
        $(withText("Запланировать")).click();
        $("[data-test-id='replan-notification']").should(appear);
        $("[data-test-id='replan-notification'] .notification__content")
                .shouldHave(text("У вас уже запланирована встреча на другую дату. Перепланировать?"));
        $(withText("Перепланировать")).click();
        $("[data-test-id='success-notification'] [class='notification__content']")
                .should(appear).shouldHave(exactText("Встреча успешно запланирована на " + secondMeetingDate));
    }
}