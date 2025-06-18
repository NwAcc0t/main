
import uuid
import requests
from flask import Flask, request, jsonify

app = Flask(__name__)

# Адреси інших сервісів
LOGGING_SERVICE_URL = 'http://localhost:5001/messages'
MESSAGES_SERVICE_URL = 'http://localhost:5002/notify'

@app.route('/submit', methods=['POST'])
def submit_message():
    """
    Отримує JSON {"msg": "<текст>"},
    генерує UUID, надсилає {"uuid": <uuid>, "msg": "<текст>"}
    до logging-service та повертає статус із uuid.
    """
    data = request.get_json()
    if not data or 'msg' not in data:
        return jsonify({'error': 'Відсутнє поле msg'}), 400

    msg_text = data['msg']
    new_uuid = str(uuid.uuid4())
    payload = {'uuid': new_uuid, 'msg': msg_text}

    try:
        resp = requests.post(LOGGING_SERVICE_URL, json=payload)
        resp.raise_for_status()
    except requests.RequestException as e:
        return jsonify({'error': f'Не вдалося надіслати до logging-service: {e}'}), 500

    return jsonify({'status': 'ok', 'uuid': new_uuid}), 201

@app.route('/', methods=['GET'])
def aggregate_messages():
    """
    Виконує GET до logging-service і до messages-service,
    об’єднує рядки у форматі "<msgs> | <static_text>".
    """
    try:
        resp_log = requests.get(LOGGING_SERVICE_URL)
        resp_log.raise_for_status()
        msgs = resp_log.text  # рядок із повідомленнями
    except requests.RequestException as e:
        return jsonify({'error': f'Помилка запиту до logging-service: {e}'}), 500

    try:
        resp_msg = requests.get(MESSAGES_SERVICE_URL)
        resp_msg.raise_for_status()
        static = resp_msg.text  # "Not implemented yet"
    except requests.RequestException as e:
        return jsonify({'error': f'Помилка запиту до messages-service: {e}'}), 500

    combined = f"{msgs} | {static}"
    return combined, 200

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
