# Logging Service

## Опис
Цей сервіс зберігає у пам'яті всі пари {UUID → msg}, які надходять у POST-запитах. При GET-запиті повертає всі msg (без UUID) як один рядок.

## Запуск
1. Створити віртуальне оточення (необов'язково, але рекомендовано):
   ```bash
   python3 -m venv venv
   source venv/bin/activate  # Linux/macOS
   venv\Scripts\activate     # Windows
