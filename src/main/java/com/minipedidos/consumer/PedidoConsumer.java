package com.minipedidos.consumer;

import com.minipedidos.config.RabbitMQConfig;
import com.minipedidos.model.Pedido;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PedidoConsumer {

    // Registra qué pedidos ya tuvieron un fallo temporal
    private final Set<Long> pedidosIntentados =
            ConcurrentHashMap.newKeySet();

    @RabbitListener(
        queues = RabbitMQConfig.COLA_PEDIDOS,
        concurrency = "2"
    )
    public void recibirPedido(Pedido pedido) {

        System.out.println("==============================");
        System.out.println(
            "Consumidor: " + Thread.currentThread().getName()
        );

        System.out.println("Pedido recibido desde RabbitMQ");
        System.out.println("ID: " + pedido.getPedidoId());
        System.out.println("Cliente: " + pedido.getCliente());
        System.out.println("Producto: " + pedido.getProducto());
        System.out.println("Cantidad: " + pedido.getCantidad());

        // CASO 1: Error permanente
        if ("ERROR".equalsIgnoreCase(pedido.getCliente())) {

            System.out.println(
                "ERROR PERMANENTE: pedido " +
                pedido.getPedidoId()
            );

            throw new AmqpRejectAndDontRequeueException(
                "Error permanente de procesamiento"
            );
        }

        // CASO 2: Error temporal
        if ("TEMPORAL".equalsIgnoreCase(pedido.getCliente())) {

            // Primera vez: provoca un fallo
            if (pedidosIntentados.add(pedido.getPedidoId())) {

                System.out.println(
                    "ERROR TEMPORAL: primer intento fallido"
                );

                throw new RuntimeException(
                    "Servicio temporalmente no disponible"
                );
            }

            // Segundo intento: procesamiento correcto
            System.out.println(
                "Servicio recuperado: pedido procesado"
            );

            pedidosIntentados.remove(pedido.getPedidoId());
        }

        // CASO 3: Pedido procesado correctamente
        System.out.println("Estado: " + pedido.getEstado());

        System.out.println("Pedido procesado correctamente");
        System.out.println("==============================");
    }
}
