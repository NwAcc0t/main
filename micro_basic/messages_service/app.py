from flask import Flask

app = Flask(__name__)

@app.route('/notify', methods=['GET'])
def notify():
    return 'Not implemented yet', 200

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5002, debug=True)
