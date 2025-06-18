# Facade Service

## Опис
Facade-service приймає POST- та GET-запити від клієнта, взаємодіє з logging-service і messages-service та повертає результат клієнту.

### Роутинг
- **POST /submit**  
  Очікує JSON `{"msg": "<текст>"}`, генерує UUID, надсилає `{"uuid": <uuid>, "msg": "<текст>"}` на logging-service, повертає `{"status": "ok", "uuid": <uuid>}`.

- **GET /**  
  Зчитує всі повідомлення з logging-service (як рядок `msg1, msg2, …`), зчитує статичний текст з messages-service (`Not implemented yet`), об’єднує їх у форматі `"msg1, msg2, … | Not implemented yet"` та повертає як простий текст.

## Запуск
1. Створити (за потреби) віртуальне оточення та активувати його.
2. Встановити залежності:
   ```bash
   pip install -r requirements.txt
