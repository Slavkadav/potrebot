import com.google.gson.JsonObject;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.callback.longpoll.responses.GetLongPollEventsResponse;
import com.vk.api.sdk.objects.groups.responses.GetLongPollServerResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

@Slf4j
public class Main {

    public static final int GROUP_ID = 204764107;
    private static final String BLANK = "https://oauth.vk.com/blank.html";
    private static final String KEY = "e77dace000404e3754d4f0d84a651d12b52a37cb34e0ea48b7112b84d27f4190d97d15d75407ab31a2e51";
    public static final int CHAT_PEER_PREFIX = 2000000000;
    public static final String MESSAGE_TYPE = "message_new";

    public static void main(String[] args) throws ClientException, ApiException {
        TransportClient transportClient = HttpTransportClient.getInstance();
        VkApiClient vk = new VkApiClient(transportClient);
        GroupActor groupActor = new GroupActor(GROUP_ID, KEY);
        GetLongPollServerResponse response = vk.groupsLongPoll().getLongPollServer(groupActor, GROUP_ID).execute();
        Random random = new Random();
        Integer ts = Integer.valueOf(response.getTs());
        while (true) {
            try {
                GetLongPollEventsResponse eventsResponse;

                eventsResponse = vk.longPoll()
                        .getEvents(response.getServer(), response.getKey(), ts)
                        .execute();
                ts = eventsResponse.getTs();

                if (!eventsResponse.getUpdates().isEmpty()) {
                    for (JsonObject update : eventsResponse.getUpdates()) {
                        if (MESSAGE_TYPE.equals(update.get("type").getAsString())) {
                            int chatId = update.getAsJsonObject("object")
                                    .getAsJsonObject("message")
                                    .get("peer_id")
                                    .getAsInt();
                            chatId = chatId - CHAT_PEER_PREFIX;
                            vk.messages().send(groupActor)
                                    .chatId(chatId)
                                    .randomId(random.nextInt())
                                    .message("ДА РАБОТАЮ Я БЛЯТЬ!").execute();
                            System.out.println(update);
                        }
                    }
                }
            } catch (ApiException | ClientException e) {
                log.info("Exception caught ", e);
            }
        }
    }
}
