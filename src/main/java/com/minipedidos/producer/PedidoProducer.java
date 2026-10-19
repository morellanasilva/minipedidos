package com.minipedidos.producer;

import com.minipedidos.config.RabbitMQConfig;
import com.minipedidos.model.Pedido;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class PedidoProducer {

    private final RabbitTemplate rabbitTemplate;

    public PedidoProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarPedido(Pedido pedido) {

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_PEDIDOS,
                RabbitMQConfig.ROUTING_KEY,
                pedido
        );

        System.out.println(
                "Pedido enviado a RabbitMQ: " + pedido.getPedidoId()
        );
    }
}
