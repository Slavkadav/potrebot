package ru.abe.slaves.potrebot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.abe.slaves.potrebot.web.model.VkEvent;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class EventController {
    public static final String MESSAGE_TYPE = "message_new";

    private final MessageProcessingService messageProcessingService;

    @PostMapping
    public String handleEvent(@RequestBody VkEvent vkEvent) {
        log.info("received event {}", vkEvent);
        if (MESSAGE_TYPE.equals(vkEvent.getType())) {
            messageProcessingService.processMessage(vkEvent.getObject().getMessage());
        }
        return "ok";
    }


}
