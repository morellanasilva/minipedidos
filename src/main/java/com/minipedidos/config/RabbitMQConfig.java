package com.minipedidos.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Configuracion principal de pedidos
    public static final String COLA_PEDIDOS = "pedidos.cola";
    public static final String EXCHANGE_PEDIDOS = "pedidos.exchange";
    public static final String ROUTING_KEY = "pedidos.nuevo";

    // Configuracion de mensajes fallidos
    public static final String DLX_PEDIDOS = "pedidos.dlx";
    public static final String DLQ_PEDIDOS = "pedidos.dlq";
    public static final String ROUTING_KEY_ERROR = "pedidos.error";

    // 1. Cola principal existente
    @Bean
    public Queue pedidosQueue() {
        return QueueBuilder.durable(COLA_PEDIDOS).build();
    }

    // 2. Exchange principal
    @Bean
    public DirectExchange pedidosExchange() {
        return new DirectExchange(EXCHANGE_PEDIDOS);
    }

    // 3. Vincular la cola principal al exchange
    @Bean
    public Binding pedidosBinding(
            Queue pedidosQueue,
            DirectExchange pedidosExchange) {

        return BindingBuilder.bind(pedidosQueue)
                .to(pedidosExchange)
                .with(ROUTING_KEY);
    }

    // 4. Crear el Dead Letter Exchange
    @Bean
    public DirectExchange pedidosDeadLetterExchange() {
        return new DirectExchange(DLX_PEDIDOS);
    }

    // 5. Crear la Dead Letter Queue
    @Bean
    public Queue pedidosDeadLetterQueue() {
        return QueueBuilder.durable(DLQ_PEDIDOS).build();
    }

    // 6. Vincular la DLQ al DLX
    @Bean
    public Binding pedidosDeadLetterBinding() {
        return BindingBuilder
                .bind(pedidosDeadLetterQueue())
                .to(pedidosDeadLetterExchange())
                .with(ROUTING_KEY_ERROR);
    }

    // 7. Convertir objetos Java a JSON y viceversa
    @Bean
    public JacksonJsonMessageConverter jacksonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
