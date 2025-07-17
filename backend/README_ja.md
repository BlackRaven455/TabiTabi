
# Places API

観光地やスポットを管理するためのAPIです。検索、フィルタリング、統計情報も提供します。

---

## 🌎 エンドポイント

### GET `/api/places`

ページング付きですべての場所を取得します。

**パラメータ:**

- `page`（デフォルト: 0）— ページ番号
- `size`（デフォルト: 20）— ページサイズ
- `sort` — ソート条件（例: `name,asc`）

**レスポンス例:**

```json
{
  "content": [
    {
      "placeId": 1,
      "name": "赤の広場",
      "description": "モスクワの中心的な広場",
      "location": "モスクワ、ロシア",
      "imageUrl": "https://example.com/image.jpg",
      "latitude": 55.7539,
      "longitude": 37.6208,
      "googleId": "ChIJybDUc_xKtUYRTM9XV8zWRD0",
      "rating": "4.5",
      "userTotal": "50000",
      "category": {
        "id": 1,
        "name": "観光名所"
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

IDで場所を取得します。

---

### POST `/api/places`

新しい場所を作成します。

**リクエスト例:**

```json
{
  "name": "新しい場所",
  "description": "場所の説明",
  "location": "住所",
  "imageUrl": "https://example.com/image.jpg",
  "latitude": 55.7539,
  "longitude": 37.6208,
  "googleId": "unique_google_id",
  "rating": "4.5",
  "userTotal": "100",
  "category": { "id": 1 }
}
```

**レスポンス:** `201 Created`

---

### PUT `/api/places/{id}`

場所を完全に更新します。

---

### PATCH `/api/places/{id}`

場所を部分的に更新します。

**リクエスト例:**

```json
{
  "name": "新しい名前",
  "rating": "4.8"
}
```

---

### DELETE `/api/places/{id}`

場所を削除します。

---

## 🔎 検索 & フィルター

- **名前検索:** `/api/places/search?name=赤`
- **カテゴリ検索:** `/api/places/category/{categoryId}`
- **住所検索:** `/api/places/location?location=東京`
- **評価検索:** `/api/places/rating/{minRating}`
- **近くの場所:** `/api/places/nearby?latitude=35.6895&longitude=139.6917&radiusKm=5`
- **Google ID検索:** `/api/places/google/{googleId}`
- **トップ評価の場所:** `/api/places/top-rated?limit=10`
- **統計情報:** `/api/places/stats`

---

## ⚠️ バリデーション

- 必須項目: `name`, `location`, `categoryId`
- 緯度（−90 から 90）、経度（−180 から 180）の範囲内
- Google Place ID の一意性
- カテゴリの存在確認

---

## 💬 エラーステータスコード

- `400 Bad Request` — 無効なリクエスト
- `404 Not Found` — 見つからない
- `409 Conflict` — Google ID 重複
- `500 Internal Server Error` — サーバー内部エラー

---

## 📄 使用例 (cURL)

### 新しい場所を作成

```bash
curl -X POST http://localhost:8080/api/places \
  -H "Content-Type: application/json" \
  -d '{
    "name": "エルミタージュ美術館",
    "description": "国立美術館",
    "location": "サンクトペテルブルク, ドヴォルツォヴァヤ河岸通り 34",
    "latitude": 59.9398,
    "longitude": 30.3146,
    "rating": "4.7",
    "userTotal": "25000",
    "category": {"id": 2}
  }'
```

### 近くの場所を検索

```bash
curl "http://localhost:8080/api/places/nearby?latitude=35.6895&longitude=139.6917&radiusKm=2"
```

### 評価を更新

```bash
curl -X PATCH http://localhost:8080/api/places/1 \
  -H "Content-Type: application/json" \
  -d '{"rating": "4.8", "userTotal": "51000"}'
```

---

## 🗺️ ページング

- `page` — ページ番号（0から開始）
- `size` — 1ページあたりの件数
- `sort` — フィールドと方向（例: `name,asc`）

---

## 📊 統計情報の例

```json
{
  "totalPlaces": 1000,
  "totalCategories": 15,
  "averageRating": "4.2",
  "totalRatings": 50000
}
```

---

## ✉️ コンタクト

改善提案やコントリビュートの際は、Issue または Pull Request を歓迎します！

---

🎉 楽しく開発しましょう！
