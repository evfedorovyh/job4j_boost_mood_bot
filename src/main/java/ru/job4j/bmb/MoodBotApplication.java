package ru.job4j.bmb;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.job4j.bmb.model.Award;
import ru.job4j.bmb.model.Mood;
import ru.job4j.bmb.model.MoodContent;
import ru.job4j.bmb.repository.AwardRepository;
import ru.job4j.bmb.repository.MoodContentRepository;
import ru.job4j.bmb.repository.MoodRepository;
import ru.job4j.bmb.telegram.*;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@EnableAspectJAutoProxy
@EnableScheduling
@SpringBootApplication
public class MoodBotApplication {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("dd-MM-yyyy HH:mm")
            .withZone(ZoneId.systemDefault());

    public static void main(String[] args) {
        SpringApplication.run(MoodBotApplication.class, args);
    }

    @Bean
    @Conditional(OnRealCondition.class)
    public CommandLineRunner commandLineRunnerReal(ApplicationContext ctx) {
        return args -> {
            var bot = ctx.getBean(LongPollingBot.class);
            var botsApi = new TelegramBotsApi(DefaultBotSession.class);
            try {
                botsApi.registerBot(bot);
                System.out.println("Бот успешно зарегистрирован");
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        };
    }

    @Bean
    @Conditional(OnFakeCondition.class)
    public CommandLineRunner commandLineRunnerFake(ApplicationContext ctx) {
        return args -> {
            ctx.getBean(LongPollingBot.class);
            System.out.println("FakeBot ready for using");
        };
    }

    @Bean
    public CommandLineRunner loadDatabase(MoodRepository moodRepository,
                                           MoodContentRepository moodContentRepository,
                                           AwardRepository awardRepository) {
        return args -> {
            var moodIs = moodRepository.findAll();
            if (!moodIs.isEmpty()) {
                return;
            }
            var data = new ArrayList<MoodContent>();
            data.add(new MoodContent(
                    new Mood("Счастливейший на свете \uD83C\uDF1F", true),
                    "Невероятно! Вы сияете от счастья, продолжайте радоваться жизни."));
                    data.add(new MoodContent(
                    new Mood("Успокоение и гармония \uD83E\uDDD8\u200D\u2642\uFE0F", true),
                            "Потрясающе! Вы в состоянии внутреннего мира и гармонии."));
                    data.add(new MoodContent(
                    new Mood("В состоянии комфорта \u263A\uFE0F", true),
                            "Отлично! Вы чувствуете себя уютно и спокойно."));
                    data.add(new MoodContent(
                    new Mood("Вдохновенное настроение \ud83d\udca1", true),
                    "Потрясающе! Вы полны идей и энергии для их реализации."));
                    data.add(new MoodContent(
                    new Mood("Легкое волнение \ud83c\udf88", true),
                            "Замечательно! Немного волнения добавляет жизни краски."));
                    data.add(new MoodContent(
                    new Mood("Сосредоточенное настроение \ud83c\udfaf", false),
                            "Хорошо! Ваш фокус на высоте, используйте это время эффективно."));
                    data.add(new MoodContent(
                    new Mood("Тревожное настроение \ud83d\ude1f", false),
                            "Не волнуйтесь, всё пройдет. Попробуйте расслабиться и найти источник вашего беспокойства."));
                    data.add(new MoodContent(
                    new Mood("Разочарованное настроение \ud83d\ude1e", false),
                            "Бывает. Не позволяйте разочарованию сбить вас с толку, всё наладится."));
                    data.add(new MoodContent(
                    new Mood("Усталое настроение \ud83d\ude34", false),
                            "Похоже, вам нужен отдых. Позаботьтесь о себе и отдохните."));
                    data.add(new MoodContent(
                    new Mood("Раздраженное настроение \ud83d\ude20", false),
                            "Попробуйте успокоиться и найти причину раздражения, чтобы исправить ситуацию."));
            moodRepository.saveAll(data.stream().map(MoodContent::getMood).toList());
            moodContentRepository.saveAll(data);
            var awardIs = awardRepository.findAll();
            if (!awardIs.isEmpty()) {
                return;
            }
            var awards = new ArrayList<Award>();
            awards.add(new Award("Смайлик дня", "За 1 день хорошего настроения."
                    + "Награда: Веселый смайлик или стикер, отправленный пользователю в качестве поощрения.", 1));
            awards.add(new Award("Настроение недели", "За 7 последовательных дней хорошего или отличного настроения."
                    + "Награда: Специальный значок или иконка, отображаемая в профиле пользователя в течение недели.", 7));
            awards.add(new Award("Бонусные очки", "За каждые 3 дня хорошего настроения. "
                    + "Награда: Очки, которые можно обменять на виртуальные предметы или функции внутри приложения.", 3));
            awards.add(new Award("Персонализированные рекомендации", "После 5 дней хорошего настроения. "
                    + "Награда: Подборка контента или активности на основе интересов пользователя.", 5));
            awards.add(new Award("Достижение 'Солнечный луч'", "За 10 дней непрерывного хорошего настроения. "
                    + "Награда: Разблокировка новой темы оформления или фона в приложении.", 10));
            awards.add(new Award("Виртуальный подарок", "После 15 дней хорошего настроения."
                    + "Награда: Возможность отправить или получить виртуальный подарок внутри приложения.", 15));
            awards.add(new Award("Титул 'Лучезарный'", "За 20 дней хорошего или отличного настроения."
                    + "Награда: Специальный титул, отображаемый рядом с именем пользователя.", 20));
            awards.add(new Award("Доступ к премиум-функциям", "После 30 дней хорошего настроения."
                    + "Награда: Временный доступ к премиум-функциям или эксклюзивному контенту.", 30));
            awards.add(new Award("Участие в розыгрыше призов", "За каждую неделю хорошего настроения."
                    + "Награда: Шанс выиграть призы в ежемесячных розыгрышах.", 7));
            awards.add(new Award("Эксклюзивный контент", "После 25 дней хорошего настроения."
                    + "Награда: Доступ к эксклюзивным статьям, видео или мероприятиям.", 25));
            awards.add(new Award("Награда 'Настроение месяца'", "За поддержание хорошего или отличного настроения в течение целого месяца."
                    + "Награда: Специальный значок, признание в сообществе или дополнительные привилегии.", 31));
            awards.add(new Award("Физический подарок", "После 60 дней хорошего настроения."
                    + "Награда: Возможность получить небольшой физический подарок, например, открытку или фирменный сувенир.", 60));
            awards.add(new Award("Коучинговая сессия", "После 45 дней хорошего настроения."
                    + "Награда: Бесплатная сессия с коучем или консультантом для дальнейшего улучшения благополучия.", 45));
            awards.add(new Award("Разблокировка мини-игр", "После 14 дней хорошего настроения."
                    + "Награда: Доступ к развлекательным мини-играм внутри приложения.", 14));
            awards.add(new Award("Персональное поздравление", "За значимые достижения (например, 50 дней хорошего настроения)."
                    + "Награда: Персонализированное сообщение от команды приложения или вдохновляющая цитата.", 50));
            awardRepository.saveAll(awards);
        };
    }
}
