
#!/usr/bin/env bash
set -euo pipefail

# ==========================================
# MiniPedidos - Laboratorio Kafka
# Paso 23.3: Prueba de tres particiones
# ==========================================

TOPICO="pedidos-creados"
BROKER="localhost:9092"
CONTENEDOR="kafka"

echo "======================================"
echo " MiniPedidos - Particiones Kafka"
echo "======================================"

# 1. Verificar que Kafka esté ejecutándose
echo "Verificando contenedor Kafka..."

if [ "$(docker inspect -f '{{.State.Running}}' "$CONTENEDOR" 2>/dev/null || true)" != "true" ]; then
    echo "ERROR: Kafka está detenido."
    echo "Ejecuta: docker compose start kafka"
    exit 1
fi

# 2. Verificar que el tópico exista
echo "Verificando tópico: $TOPICO"

docker exec "$CONTENEDOR" \
    /opt/kafka/bin/kafka-topics.sh \
    --describe \
    --topic "$TOPICO" \
    --bootstrap-server "$BROKER"

# 3. Crear identificadores para esta ejecución
BASE_ID=$(date +%s)

echo ""
echo "Publicando seis pedidos de prueba..."

# 4. Enviar mensajes con claves de particionamiento
printf '%s\n' \
"cliente-A:{\"pedidoId\":${BASE_ID}1,\"cliente\":\"Cliente A\",\"producto\":\"Mouse\",\"cantidad\":1}" \
"cliente-B:{\"pedidoId\":${BASE_ID}2,\"cliente\":\"Cliente B\",\"producto\":\"Teclado\",\"cantidad\":2}" \
"cliente-C:{\"pedidoId\":${BASE_ID}3,\"cliente\":\"Cliente C\",\"producto\":\"Monitor\",\"cantidad\":1}" \
"cliente-D:{\"pedidoId\":${BASE_ID}4,\"cliente\":\"Cliente D\",\"producto\":\"Notebook\",\"cantidad\":1}" \
"cliente-E:{\"pedidoId\":${BASE_ID}5,\"cliente\":\"Cliente E\",\"producto\":\"Mouse\",\"cantidad\":3}" \
"cliente-F:{\"pedidoId\":${BASE_ID}6,\"cliente\":\"Cliente F\",\"producto\":\"Teclado\",\"cantidad\":2}" \
| docker exec -i "$CONTENEDOR" \
    /opt/kafka/bin/kafka-console-producer.sh \
    --bootstrap-server "$BROKER" \
    --topic "$TOPICO" \
    --property parse.key=true \
    --property key.separator=:

echo ""
echo "Publicación de seis pedidos finalizada."
echo "Tópico: $TOPICO"
echo "Claves utilizadas: cliente-A a cliente-F"
echo "======================================"
