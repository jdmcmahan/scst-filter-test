package com.test;

import com.test.model.Foo;
import com.test.model.FooEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.UUID;
import java.util.function.Function;

@Configuration
public class FooListener {

    @Bean
    public IntegrationFlow fooFlow() {
        return IntegrationFlows.from(FooMessageFunction.class, gateway -> gateway.beanName("foo"))
                .filter(Message.class, message -> {
                    Object value = message.getHeaders().get("eventType");
                    if (value == null) {
                        return false;
                    } else {
                        value = value instanceof byte[] ? new String((byte[]) value) : value.toString();
                        return value.equals("foo");
                    }
                })
                .transform(Foo.class, this::handleFoo)
                .get();
    }

    protected Message<FooEvent> handleFoo(Foo foo) {
        System.out.println("handle foo");

        FooEvent event = FooEvent.builder()
                .id("foo-event-" + UUID.randomUUID().toString())
                .fooId(foo.getId())
                .build();

        return MessageBuilder.withPayload(event)
                .setHeader("eventType", "fooEvent")
                .build();
    }

    interface FooMessageFunction extends Function<Message<?>, Message<FooEvent>> {}
}
