package com.minipedidos.consumer;

import com.minipedidos.config.RabbitMQConfig;
import com.minipedidos.model.Pedido;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PedidoConsumer {

    @RabbitListener(
        queues = RabbitMQConfig.COLA_PEDIDOS,
        concurrency = "1"
    )
    public void recibirPedido(Pedido pedido) {

        System.out.println("==============================");
        System.out.println("Pedido recibido desde RabbitMQ");
        System.out.println("ID: " + pedido.getPedidoId());
        System.out.println("Cliente: " + pedido.getCliente());
        System.out.println("Producto: " + pedido.getProducto());
        System.out.println("Cantidad: " + pedido.getCantidad());
        System.out.println("Estado: " + pedido.getEstado());
        System.out.println("Pedido procesado correctamente");
        System.out.println("==============================");
    }
}
