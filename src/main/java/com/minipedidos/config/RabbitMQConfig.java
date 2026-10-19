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

    public static final String COLA_PEDIDOS = "pedidos.cola";
    public static final String EXCHANGE_PEDIDOS = "pedidos.exchange";
    public static final String ROUTING_KEY = "pedidos.nuevo";

    // 1. Crear una cola durable
    @Bean
    public Queue pedidosQueue() {
        return QueueBuilder.durable(COLA_PEDIDOS).build();
    }

    // 2. Crear un exchange de tipo Direct
    @Bean
    public DirectExchange pedidosExchange() {
        return new DirectExchange(EXCHANGE_PEDIDOS);
    }

    // 3. Asociar la cola al exchange
    @Bean
    public Binding pedidosBinding(
            Queue pedidosQueue,
            DirectExchange pedidosExchange) {

        return BindingBuilder.bind(pedidosQueue)
                .to(pedidosExchange)
                .with(ROUTING_KEY);
    }

    // 4. Convertir objetos Java a JSON y viceversa
    @Bean
    public JacksonJsonMessageConverter jacksonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
