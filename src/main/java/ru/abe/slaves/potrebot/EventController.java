package ru.abe.slaves.potrebot;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.abe.slaves.potrebot.domain.model.Consumer;
import ru.abe.slaves.potrebot.domain.repository.ConsumersRepository;
import ru.abe.slaves.potrebot.web.model.VkEvent;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class EventController {
    public static final String MESSAGE_TYPE = "message_new";
    public static final int GROUP_ID = 204764107;

    private static final String BLANK = "https://oauth.vk.com/blank.html";
    private static final String KEY = "e77dace000404e3754d4f0d84a651d12b52a37cb34e0ea48b7112b84d27f4190d97d15d75407ab31a2e51";

    private final Random random = new Random();
    private final ConsumersRepository consumersRepository;
    private final Pattern spentPattern = Pattern.compile("\\[\\S+]\\W(\\d+)");
    private final Pattern sumPattern = Pattern.compile("\\[\\S+]\\W*сколько?", Pattern.CASE_INSENSITIVE);

    @PostMapping
    public String handleEvent(@RequestBody VkEvent vkEvent) {
        log.info("received event {}", vkEvent);
        TransportClient transportClient = HttpTransportClient.getInstance();
        VkApiClient vk = new VkApiClient(transportClient);
        GroupActor groupActor = new GroupActor(GROUP_ID, KEY);
        if (MESSAGE_TYPE.equals(vkEvent.getType())) {
            int fromId = vkEvent.getObject().getMessage().getFromId();
            Matcher spentMatcher = spentPattern.matcher(vkEvent.getObject().getMessage().getText());
            if (spentMatcher.find()) {
                Optional<Consumer> consumerOptional = consumersRepository.findFirstByUserId(fromId);
                var sum = Double.parseDouble(spentMatcher.group(1));
                if (consumerOptional.isPresent()) {
                    var consumer = consumerOptional.get();
                    consumer.setMoneySpent(consumer.getMoneySpent() + sum);
                    consumersRepository.save(consumer);
                } else {
                    consumersRepository.save(new Consumer(fromId, sum));
                }
                sendMessage(vkEvent, vk, groupActor, "Записал");
            } else {
                Matcher sumMatcher = sumPattern.matcher(vkEvent.getObject().getMessage().getText());
                if (sumMatcher.find()) {
                    List<Consumer> consumers = consumersRepository.findAllByUserId(fromId);
                    double sum = consumers.stream().mapToDouble(Consumer::getMoneySpent).sum();
                    sendMessage(vkEvent, vk, groupActor, String.format("Ты потратил уже %s. Так держать!", sum));
                }
            }
        }
        return "ok";
    }

    private void sendMessage(VkEvent vkEvent, VkApiClient vk, GroupActor groupActor, String message) {
        try {
            vk.messages().send(groupActor)
                    .chatId(vkEvent.getObject().getMessage().getChatId())
                    .randomId(random.nextInt())
                    .message(message).execute();
        } catch (ApiException | ClientException e) {
            log.error("Error occured", e);
        }
    }

}
