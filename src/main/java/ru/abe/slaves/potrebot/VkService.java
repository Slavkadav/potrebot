package ru.abe.slaves.potrebot;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
public class VkService {
    public static final int GROUP_ID = 204764107;
    private static final String KEY = "e77dace000404e3754d4f0d84a651d12b52a37cb34e0ea48b7112b84d27f4190d97d15d75407ab31a2e51";
    private final VkApiClient vkApiClient;
    private final GroupActor groupActor;
    private final Random random = new Random();

    public VkService() {
        TransportClient transportClient = HttpTransportClient.getInstance();
        vkApiClient = new VkApiClient(transportClient);
        groupActor = new GroupActor(GROUP_ID, KEY);
    }

    public void sendMessage(int chatId, String message) {
        try {
            vkApiClient.messages().send(groupActor)
                    .chatId(chatId)
                    .randomId(random.nextInt())
                    .message(message).execute();
        } catch (ApiException | ClientException e) {
            log.error("Error occured", e);
        }
    }
}
