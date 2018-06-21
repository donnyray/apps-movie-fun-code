package org.superbiz.moviefun;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class RabbitController {

    private RabbitTemplate rabbitTemplate;
    private String queue;

    public RabbitController(RabbitTemplate rabbitTemplate, @Value("${rabbitmq.queue}")String queue) {
        this.rabbitTemplate = rabbitTemplate;
        this.queue = queue;
    }

    @PostMapping("/rabbit")
    public Map<String, String> publishMessage() {
        rabbitTemplate.convertAndSend(queue, "test message");
        Map<String, String> response = new HashMap<>();
        response.put("response", "Message was published to the queue");
        return response;
    }
}
