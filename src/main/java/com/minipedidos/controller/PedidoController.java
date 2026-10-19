package com.minipedidos.controller;

import com.minipedidos.model.Pedido;
import com.minipedidos.producer.PedidoProducer;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoProducer pedidoProducer;

    private final AtomicLong secuencia = new AtomicLong(1000);

    public PedidoController(PedidoProducer pedidoProducer) {
        this.pedidoProducer = pedidoProducer;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearPedido(
            @Valid @RequestBody Pedido pedido) {

        pedido.setPedidoId(secuencia.incrementAndGet());
        pedido.setEstado("PENDIENTE");

        pedidoProducer.enviarPedido(pedido);

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", "Pedido enviado a procesamiento");
        respuesta.put("pedidoId", pedido.getPedidoId());
        respuesta.put("estado", pedido.getEstado());

        return ResponseEntity.accepted().body(respuesta);
    }
}
