#!/bin/bash
KAFKA=kafka:9092

docker compose exec kafka kafka-topics --bootstrap-server $KAFKA --create --if-not-exists \
  --topic orders.created \
  --partitions 3 \
  --config retention.ms=604800000

docker compose exec kafka kafka-topics --bootstrap-server $KAFKA --create --if-not-exists \
  --topic inventory.reserved \
  --partitions 3 \
  --config retention.ms=604800000

docker compose exec kafka kafka-topics --bootstrap-server $KAFKA --create --if-not-exists \
  --topic inventory.insufficient-stock \
  --partitions 3 \
  --config retention.ms=604800000

docker compose exec kafka kafka-topics --bootstrap-server $KAFKA --create --if-not-exists \
  --topic inventory.released \
  --partitions 3 \
  --config retention.ms=604800000

docker compose exec kafka kafka-topics --bootstrap-server $KAFKA --create --if-not-exists \
  --topic payments.authorized \
  --partitions 3 \
  --config retention.ms=604800000

docker compose exec kafka kafka-topics --bootstrap-server $KAFKA --create --if-not-exists \
  --topic payments.denied \
  --partitions 3 \
  --config retention.ms=604800000

docker compose exec kafka kafka-topics --bootstrap-server $KAFKA --create --if-not-exists \
  --topic orders.confirmed \
  --partitions 3 \
  --config retention.ms=604800000

echo "Tópicos criados:"
docker compose exec kafka kafka-topics --bootstrap-server $KAFKA --list