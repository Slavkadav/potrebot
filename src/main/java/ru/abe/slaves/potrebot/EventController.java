package ru.abe.slaves.potrebot;

import com.fasterxml.jackson.databind.JsonNode;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@Slf4j
@RestController
@RequestMapping
public class EventController {
    public static final int CHAT_PEER_PREFIX = 2000000000;
    public static final String MESSAGE_TYPE = "message_new";
    public static final int GROUP_ID = 204764107;
    private static final String BLANK = "https://oauth.vk.com/blank.html";
    private static final String KEY = "e77dace000404e3754d4f0d84a651d12b52a37cb34e0ea48b7112b84d27f4190d97d15d75407ab31a2e51";
    private final Random random = new Random();

    @PostMapping
    public String handleEvent(@RequestBody JsonNode jsonNode) {
        log.info("received event {}", jsonNode);
        TransportClient transportClient = HttpTransportClient.getInstance();
        VkApiClient vk = new VkApiClient(transportClient);
        GroupActor groupActor = new GroupActor(GROUP_ID, KEY);
        if (MESSAGE_TYPE.equals(jsonNode.get("type").asText())) {
            int chatId = jsonNode.get("object")
                    .get("message")
                    .get("peer_id")
                    .intValue();
            chatId = chatId - CHAT_PEER_PREFIX;
            try {
                vk.messages().send(groupActor)
                        .chatId(chatId)
                        .randomId(random.nextInt())
                        .message("ДА РАБОТАЮ Я БЛЯТЬ!").execute();
            } catch (ApiException | ClientException e) {
                log.error("Error occured", e);
            }
        }
        return "ok";
    }

}
