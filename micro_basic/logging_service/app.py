from flask import Flask, request, jsonify
from threading import Lock

app = Flask(__name__)

# У пам'яті: словник uuid → msg
messages = {}
lock = Lock()

@app.route('/messages', methods=['POST'])
def add_message():
    """
    Отримує JSON із полями "uuid" та "msg":
    {"uuid": "<some-uuid>", "msg": "<some-text>"}
    Зберігає у локальний словник і виводить у консоль.
    """
    data = request.get_json()
    if not data or 'uuid' not in data or 'msg' not in data:
        return jsonify({'error': 'Відсутні поля uuid або msg'}), 400

    uid = data['uuid']
    msg = data['msg']

    with lock:
        messages[uid] = msg

    print(f"[Logging-Service] Збережено: UUID={uid}, MSG='{msg}'")
    return jsonify({'status': 'ok'}), 201

@app.route('/messages', methods=['GET'])
def get_all_messages():
    """
    Повертає всі збережені msg як один рядок, розділені комою.
    Якщо повідомлень немає, повертає порожній рядок.
    """
    with lock:
        all_msgs = list(messages.values())

    # Конкатенуємо всі повідомлення через кому
    result = ', '.join(all_msgs)
    return result, 200

if __name__ == '__main__':
    # Запускаємо на порту 5001
    app.run(host='0.0.0.0', port=5001, debug=True)
