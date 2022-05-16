package ru.abe.slaves.potrebot

import com.vk.api.sdk.client.TransportClient
import com.vk.api.sdk.client.VkApiClient
import com.vk.api.sdk.client.actors.GroupActor
import com.vk.api.sdk.exceptions.ApiException
import com.vk.api.sdk.exceptions.ClientException
import com.vk.api.sdk.httpclient.HttpTransportClient
import com.vk.api.sdk.objects.callback.longpoll.responses.GetLongPollEventsResponse
import com.vk.api.sdk.objects.groups.responses.GetLongPollServerResponse
import com.vk.api.sdk.objects.users.Fields
import com.vk.api.sdk.objects.users.responses.GetResponse
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

private const val GROUP_ID = 204764107
private const val KEY = "e77dace000404e3754d4f0d84a651d12b52a37cb34e0ea48b7112b84d27f4190d97d15d75407ab31a2e51"

@Slf4j
@Service
class VkService {

    private val log = LoggerFactory.getLogger(VkService::class.java)

    private val vkApiClient: VkApiClient
    private val groupActor: GroupActor
    private val random = Random()

    init {
        val transportClient: TransportClient = HttpTransportClient.getInstance()
        vkApiClient = VkApiClient(transportClient)
        groupActor = GroupActor(GROUP_ID, KEY)
    }

    fun sendMessage(chatId: Int, message: String?) {
        try {
            vkApiClient.messages().send(groupActor)
                .chatId(chatId)
                .randomId(random.nextInt())
                .message(message).execute()
        } catch (e: ApiException) {
            log.error("Error occured", e)
        } catch (e: ClientException) {
            log.error("Error occured", e)
        }
    }

    fun loadUserInfo(userId: Int): GetResponse? {
        try {
            return vkApiClient.users()
                .get(groupActor)
                .userIds(userId.toString())
                .fields(Fields.SCREEN_NAME)
                .execute()[0]
        } catch (e: ApiException) {
            log.error("Error occured", e)
        } catch (e: ClientException) {
            log.error("Error occured", e)
        }
        return null
    }


    fun getLongPollServer(): GetLongPollServerResponse? {
        return vkApiClient.groups().getLongPollServer(groupActor, GROUP_ID).execute()
    }

    fun longPoll(server: String, key: String, ts: String): GetLongPollEventsResponse? {
        return vkApiClient.longPoll().getEvents(server, key, ts).waitTime(25).execute()
    }

}