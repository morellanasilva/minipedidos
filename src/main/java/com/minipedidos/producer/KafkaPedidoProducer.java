
package com.minipedidos.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaPedidoProducer {

    private static final String TOPICO = "pedidos-creados";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaPedidoProducer(
            KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publicarPedidoCreado(String mensajeJson) {

        kafkaTemplate.send(TOPICO, mensajeJson)
                .whenComplete((resultado, error) -> {

                    if (error != null) {
                        System.err.println(
                            "Error al publicar evento Kafka: "
                            + error.getMessage()
                        );
                    } else {
                        System.out.println(
                            "Evento publicado en Kafka: "
                            + mensajeJson
                        );

                        System.out.println(
                            "Tópico: "
                            + resultado.getRecordMetadata().topic()
                        );

                        System.out.println(
                            "Partición: "
                            + resultado.getRecordMetadata().partition()
                        );
                    }
                });
    }
}
