import csv
import time
import pika
import json
import sys
from datetime import datetime

# Preluarea argumentului pentru device_id
if len(sys.argv) < 2:
    print("Trebuie sa specificati ID-ul dispozitivului.")
    sys.exit(1)

device_id = sys.argv[1]  # ID-ul dispozitivului din argument

# Setarea conexiunii RabbitMQ
try:
    connection = pika.BlockingConnection(pika.ConnectionParameters('rabbitmq'))
    channel = connection.channel()
    print("[Producer] Conexiune la RabbitMQ reusita.")
except Exception as e:
    print(f"[Producer] Eroare la conectarea la RabbitMQ: {str(e)}")
    sys.exit(1)


# Crearea unei cozi numite 'energy_data_queue'
channel.queue_declare(queue='energy_data_queue',durable=True)

# Citirea fisierului sensor.csv
with open('sensor.csv', mode='r') as file:
    csv_reader = csv.reader(file)
    # Citirea tuturor valorilor intr-o lista
    values = [float(row[0]) for row in csv_reader if len(row) > 0]

    # Iterarea peste valorile consecutive pentru a calcula diferenta
    for i in range(1, len(values)):
        # Obtinem timestamp-ul in milisecunde
        timestamp = int(datetime.now().timestamp() * 1000)
        
        # Calculam diferenta dintre valorile consecutive
        measurement_value = values[i] - values[i - 1]

        # Structura mesajului JSON
        message = {
            "timestamp": timestamp,
            "device_id": device_id,
            "measurement_value": measurement_value
        }

        # Convertim mesajul in JSON
        message_json = json.dumps(message)

        # Trimitem mesajul la coada
        channel.basic_publish(exchange='',
         routing_key='energy_data_queue',
          body=message_json,
           properties=pika.BasicProperties(
            delivery_mode=2, # Mesaj persistent
            ) 
        )
        print(f"[Producer] Mesaj trimis: {message_json}")

        # Asteptam 3 secunde inainte de a trimite urmatorul mesaj
        time.sleep(3)

# Inchidem conexiunea
connection.close()
