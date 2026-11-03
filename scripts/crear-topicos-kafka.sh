
#!/usr/bin/env bash

set -euo pipefail

# ==========================================
# MiniPedidos - Inicialización de Kafka
# Tópico: pedidos-creados
# Entorno: GitHub Codespaces + Docker
# ==========================================

TOPICO="pedidos-creados"
BROKER="localhost:9092"
KAFKA_CONTAINER="kafka"

KAFKA_TOPICS="/opt/kafka/bin/kafka-topics.sh"

echo "======================================"
echo " MiniPedidos - Configuración Kafka"
echo "======================================"

# 1. Verificar que el contenedor exista y esté activo
echo "Verificando contenedor Kafka..."

if [ "$(docker inspect -f '{{.State.Running}}' "$KAFKA_CONTAINER" 2>/dev/null || true)" != "true" ]; then
    echo "ERROR: Kafka no está iniciado."
    echo "Ejecuta: docker compose up -d kafka"
    exit 1
fi

# 2. Esperar hasta que Kafka responda
echo "Esperando disponibilidad del broker..."

DISPONIBLE=false

for intento in {1..15}; do
    if docker exec "$KAFKA_CONTAINER" "$KAFKA_TOPICS" \
        --bootstrap-server "$BROKER" \
        --list > /dev/null 2>&1; then
        DISPONIBLE=true
        break
    fi

    echo "Intento $intento de 15..."
    sleep 2
done

if [ "$DISPONIBLE" != "true" ]; then
    echo "ERROR: Kafka no responde."
    exit 1
fi

echo "Kafka está disponible."

# 3. Verificar si existe el tópico
echo "Comprobando tópico: $TOPICO"

if docker exec "$KAFKA_CONTAINER" "$KAFKA_TOPICS" \
    --bootstrap-server "$BROKER" \
    --list | grep -Fxq "$TOPICO"; then

    echo "El tópico ya existe: $TOPICO"

else
    # 4. Crear el tópico
    echo "Creando tópico: $TOPICO"

    docker exec "$KAFKA_CONTAINER" "$KAFKA_TOPICS" \
        --create \
        --if-not-exists \
        --topic "$TOPICO" \
        --bootstrap-server "$BROKER" \
        --partitions 1 \
        --replication-factor 1

    echo "Tópico creado correctamente."
fi

# 5. Mostrar la configuración del tópico
echo ""
echo "Información del tópico:"

docker exec "$KAFKA_CONTAINER" "$KAFKA_TOPICS" \
    --describe \
    --topic "$TOPICO" \
    --bootstrap-server "$BROKER"

echo ""
echo "Inicialización de Kafka finalizada."
