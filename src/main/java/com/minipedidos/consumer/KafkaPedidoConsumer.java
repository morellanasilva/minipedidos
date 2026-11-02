
package com.minipedidos.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaPedidoConsumer {

    @KafkaListener(
        topics = "pedidos-creados",
        groupId = "minipedidos-group"
    )
    public void consumirPedidoCreado(String mensajeJson) {

        System.out.println(
            "======================================"
        );

        System.out.println(
            "KAFKA - Evento de pedido recibido"
        );

        System.out.println(
            "Mensaje: " + mensajeJson
        );

        System.out.println(
            "======================================"
        );
    }
}
