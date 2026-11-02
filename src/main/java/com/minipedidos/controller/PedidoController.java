
package com.minipedidos.controller;

import com.minipedidos.model.Pedido;
import com.minipedidos.producer.PedidoProducer;
import com.minipedidos.producer.KafkaPedidoProducer;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tools.jackson.databind.json.JsonMapper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    // Productor de RabbitMQ
    private final PedidoProducer pedidoProducer;

    // Productor de Apache Kafka
    private final KafkaPedidoProducer kafkaPedidoProducer;

    // Conversor de objetos Java a JSON
    private final JsonMapper jsonMapper;

    // Identificador automático de pedidos
    private final AtomicLong secuencia =
            new AtomicLong(1000);

    // Constructor
    public PedidoController(
            PedidoProducer pedidoProducer,
            KafkaPedidoProducer kafkaPedidoProducer,
            JsonMapper jsonMapper) {

        this.pedidoProducer = pedidoProducer;
        this.kafkaPedidoProducer = kafkaPedidoProducer;
        this.jsonMapper = jsonMapper;
    }

    // Endpoint POST /api/pedidos
    @PostMapping
    public ResponseEntity<Map<String, Object>> crearPedido(
            @Valid @RequestBody Pedido pedido) {

        // 1. Asignar identificador
        pedido.setPedidoId(
                secuencia.incrementAndGet()
        );

        // 2. Establecer estado inicial
        pedido.setEstado("PENDIENTE");

        // 3. Convertir el pedido a JSON
        String mensajeJson =
                jsonMapper.writeValueAsString(pedido);

        // 4. Enviar pedido a RabbitMQ
        pedidoProducer.enviarPedido(pedido);

        System.out.println(
                "RabbitMQ - Envío solicitado para pedido: "
                + pedido.getPedidoId()
        );

        // 5. Publicar evento en Kafka
        kafkaPedidoProducer.publicarPedidoCreado(
                mensajeJson
        );

        System.out.println(
                "Kafka - Publicación solicitada para pedido: "
                + pedido.getPedidoId()
        );

        // 6. Crear respuesta HTTP
        Map<String, Object> respuesta =
                new LinkedHashMap<>();

        respuesta.put(
                "mensaje",
                "Pedido enviado a procesamiento"
        );

        respuesta.put(
                "pedidoId",
                pedido.getPedidoId()
        );

        respuesta.put(
                "estado",
                pedido.getEstado()
        );

        // 7. Respuesta HTTP 202
        return ResponseEntity.accepted()
                .body(respuesta);
    }
}
