
package com.minipedidos.consumer;

import com.minipedidos.config.RabbitMQConfig;
import com.minipedidos.model.Pedido;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PedidoConsumer {

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

        // CASO 1: ERROR PERMANENTE
        // Rechazar inmediatamente, sin reintentos.
        if ("ERROR".equalsIgnoreCase(pedido.getCliente())) {

            System.out.println(
                "ERROR PERMANENTE: pedido "
                + pedido.getPedidoId()
            );

            throw new AmqpRejectAndDontRequeueException(
                "Error permanente. Enviar mensaje a DLQ."
            );
        }

        // CASO 2: ERROR TEMPORAL
        // Simulacion didactica con dos intentos.
        if ("TEMPORAL".equalsIgnoreCase(pedido.getCliente())) {

            for (int intento = 1; intento <= 2; intento++) {

                System.out.println(
                    "Intento de procesamiento: " + intento
                );

                if (intento == 1) {

                    System.out.println(
                        "ERROR TEMPORAL: servicio no disponible"
                    );

                    System.out.println(
                        "Esperando 1 segundo para reintentar..."
                    );

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {

                        Thread.currentThread().interrupt();

                        throw new AmqpRejectAndDontRequeueException(
                            "Procesamiento interrumpido", e
                        );
                    }

                } else {

                    System.out.println(
                        "Servicio recuperado en el segundo intento"
                    );

                    System.out.println(
                        "Pedido procesado correctamente"
                    );
                }
            }

            System.out.println("==============================");
            return;
        }

        // CASO 3: PEDIDO NORMAL
        System.out.println("Estado: " + pedido.getEstado());
        System.out.println("Pedido procesado correctamente");
        System.out.println("==============================");
    }
}
