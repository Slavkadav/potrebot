package ru.abe.slaves.potrebot;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.abe.slaves.potrebot.domain.model.Consumer;
import ru.abe.slaves.potrebot.domain.repository.ConsumersRepository;
import ru.abe.slaves.potrebot.web.model.VkMessage;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class MessageProcessingService {
    private final Pattern spentPattern = Pattern.compile("\\[\\S+]\\W*(\\d+)");
    private final Pattern sumPattern = Pattern.compile("\\[\\S+]\\W*сколько?", Pattern.CASE_INSENSITIVE);
    private final Pattern sumMonthPattern = Pattern.compile("\\[\\S+]\\W*сколько за месяц?", Pattern.CASE_INSENSITIVE);
    private final Pattern sumYearPattern = Pattern.compile("\\[\\S+]\\W*сколько за год?", Pattern.CASE_INSENSITIVE);

    private final ConsumersRepository consumersRepository;
    private final VkService vkService;

    public void processMessage(VkMessage vkMessage) {
        String text = vkMessage.getText();
        var spentMatcher = spentPattern.matcher(text);
        var sumMatcher = sumPattern.matcher(text);
        var sumMonthMatcher = sumMonthPattern.matcher(text);
        var sumYearMatcher = sumYearPattern.matcher(text);

        if (spentMatcher.find()) {
            saveMoneySpent(vkMessage, Double.parseDouble(spentMatcher.group(1)));
        } else if (sumYearMatcher.find()) {
            countSpentForYear(vkMessage);
        } else if (sumMonthMatcher.find()) {
            countSpentForMoth(vkMessage);
        } else if (sumMatcher.find()) {
            countSpentForAllTime(vkMessage);
        } else {
            vkService.sendMessage(vkMessage.getChatId(), "Я вас таки не понял. Таки шо вы от меня хотите?");
        }
    }

    private void countSpentForAllTime(VkMessage message) {
        var consumers = consumersRepository.findAllByUserId(message.getFromId());
        var sum = consumers.stream().mapToDouble(Consumer::getMoneySpent).sum();
        vkService.sendMessage(message.getChatId(), String.format("За все время ты потребил на %s. Снимаю шляпу!", sum));
    }

    private void countSpentForMoth(VkMessage message) {
        var start = LocalDateTime.now().minus(1, ChronoUnit.MONTHS);
        var end = LocalDateTime.now();
        var consumers = consumersRepository.findAllByUserIdAndAddTimeBetween(message.getFromId(), start, end);
        var sum = consumers.stream().mapToDouble(Consumer::getMoneySpent).sum();
        vkService.sendMessage(message.getChatId(), String.format("За месяц ты потребил на %s. Достойно!", sum));

    }

    private void countSpentForYear(VkMessage message) {
        var start = LocalDateTime.now().minus(1, ChronoUnit.YEARS);
        var end = LocalDateTime.now();
        var consumers = consumersRepository.findAllByUserIdAndAddTimeBetween(message.getFromId(), start, end);
        var sum = consumers.stream().mapToDouble(Consumer::getMoneySpent).sum();
        vkService.sendMessage(message.getChatId(), String.format("За год ты потребил на %s. Вау!", sum));
    }


    private void saveMoneySpent(VkMessage message, double moneySpent) {
        int fromId = message.getFromId();
        Optional<Consumer> consumerOptional = consumersRepository.findFirstByUserId(fromId);
        if (consumerOptional.isPresent()) {
            var consumer = consumerOptional.get();
            consumer.setMoneySpent(consumer.getMoneySpent() + moneySpent);
            consumersRepository.save(consumer);
        } else {
            consumersRepository.save(new Consumer(fromId, moneySpent));
        }
        vkService.sendMessage(message.getChatId(), "Записал");
    }


}
