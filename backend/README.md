
# Places API

API для управления туристическими и интересными местами с поддержкой поиска, фильтрации и аналитики.

---

## 🌎 Endpoints

### GET `/api/places`

Получить все места с поддержкой пагинации.

**Параметры:**

- `page` (по умолчанию: 0) — номер страницы
- `size` (по умолчанию: 20) — количество элементов на странице
- `sort` — сортировка (например: `name,asc`)

**Пример ответа:**

```json
{
  "content": [
    {
      "placeId": 1,
      "name": "Красная площадь",
      "description": "Главная площадь Москвы",
      "location": "Москва, Россия",
      "imageUrl": "https://example.com/image.jpg",
      "latitude": 55.7539,
      "longitude": 37.6208,
      "googleId": "ChIJybDUc_xKtUYRTM9XV8zWRD0",
      "rating": "4.5",
      "userTotal": "50000",
      "category": {
        "id": 1,
        "name": "Достопримечательности"
      }
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 100,
  "totalPages": 5
}
```

---

### GET `/api/places/{id}`

Получить место по ID.

---

### POST `/api/places`

Создать новое место.

**Пример запроса:**

```json
{
  "name": "Новое место",
  "description": "Описание места",
  "location": "Адрес места",
  "imageUrl": "https://example.com/image.jpg",
  "latitude": 55.7539,
  "longitude": 37.6208,
  "googleId": "unique_google_id",
  "rating": "4.5",
  "userTotal": "100",
  "category": { "id": 1 }
}
```

**Ответ:** `201 Created`

---

### PUT `/api/places/{id}`

Полностью обновить место.

---

### PATCH `/api/places/{id}`

Частично обновить место.

**Пример запроса:**

```json
{
  "name": "Новое название",
  "rating": "4.8"
}
```

---

### DELETE `/api/places/{id}`

Удалить место.

---


## 🔎 Поиск и фильтрация

- **Поиск по названию:** `/api/places/search?name=красная`
- **Поиск по категории:** `/api/places/category/{categoryId}`
- **Поиск по адресу:** `/api/places/location?location=Москва`
- **Поиск по рейтингу:** `/api/places/rating/{minRating}`
- **Ближайшие места:** `/api/places/nearby?latitude=55.7539&longitude=37.6208&radiusKm=5`
- **Поиск по Google ID:** `/api/places/google/{googleId}`
- **Топ-места:** `/api/places/top-rated?limit=10`
- **Статистика:** `/api/places/stats`

# Получить все предпочтения пользователя
GET /api/users/1/preferences

# Получить только понравившиеся места
GET /api/users/1/preferences/liked

# Добавить предпочтение
POST /api/users/1/preferences
{
"placeId": 5,
"isLiked": true
}

# Обновить предпочтение
PUT /api/users/1/preferences/5
{
"isLiked": false
}

# Удалить предпочтение
DELETE /api/users/1/preferences/5

# Проверить существование предпочтения
GET /api/users/1/preferences/5/exists
---

## ⚠️ Валидация

- Обязательные поля: `name`, `location`, `categoryId`
- Корректные координаты: `latitude` (−90 до 90), `longitude` (−180 до 180)
- Уникальность Google Place ID
- Наличие категории

---

## 💬 Коды ошибок

- `400 Bad Request` — Неверные параметры
- `404 Not Found` — Место не найдено
- `409 Conflict` — Дублирующий Google ID
- `500 Internal Server Error` — Внутренняя ошибка сервера

---

## 📄 Примеры использования (cURL)

### Создание места

```bash
curl -X POST http://localhost:8080/api/places \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Эрмитаж",
    "description": "Государственный музей",
    "location": "Санкт-Петербург, Дворцовая набережная, 34",
    "latitude": 59.9398,
    "longitude": 30.3146,
    "rating": "4.7",
    "userTotal": "25000",
    "category": {"id": 2}
  }'
```

### Поиск поблизости

```bash
curl "http://localhost:8080/api/places/nearby?latitude=55.7539&longitude=37.6208&radiusKm=2"
```

### Обновление рейтинга

```bash
curl -X PATCH http://localhost:8080/api/places/1 \
  -H "Content-Type: application/json" \
  -d '{"rating": "4.8", "userTotal": "51000"}'
```

---

## 🗺️ Pagination

- `page` — page number (starting from 0)
- `size` — items per page
- `sort` — field and direction (e.g., `name,asc`)

---

## 📊 Stats example

```json
{
  "totalPlaces": 1000,
  "totalCategories": 15,
  "averageRating": "4.2",
  "totalRatings": 50000
}
```

---


## ✉️ Contact

Feel free to create an issue or send a pull request if you'd like to contribute or suggest improvements.

---

🎉 Happy coding!
