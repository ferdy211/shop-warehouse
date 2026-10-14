# Shop Warehouse API

REST API for managing a shop's warehouse inventory: items, variants, pricing, stock, and selling with out-of-stock protection.

Built with Java 17, Spring Boot 3.5, Spring Data JPA, H2 (in-memory), Maven.

## Run

Requirements: JDK 17+.

```bash
./mvnw spring-boot:run
```

The API is available at `http://localhost:8080`. The database starts empty, so create items and variants first (see examples below). The H2 console is at `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:warehouse`, user `sa`, empty password).

Run tests:

```bash
./mvnw clean verify
```

## Endpoints

| Method | Path | Description |
|---|---|---|
| GET | `/api/items` | List items with their variants |
| GET | `/api/items/{id}` | Get an item |
| POST | `/api/items` | Create an item |
| PUT | `/api/items/{id}` | Update an item |
| DELETE | `/api/items/{id}` | Delete an item and its variants |
| GET | `/api/items/{itemId}/variants` | List variants of an item |
| POST | `/api/items/{itemId}/variants` | Create a variant |
| GET | `/api/variants/{id}` | Get a variant |
| PUT | `/api/variants/{id}` | Update a variant |
| DELETE | `/api/variants/{id}` | Delete a variant |
| POST | `/api/sales` | Sell an item or variant (decreases stock) |

### Examples

Create an item:

```bash
curl -X POST localhost:8080/api/items -H 'Content-Type: application/json' \
  -d '{"name":"Hoodie","description":"Warm hoodie","price":250000,"stock":0}'
```

Add a variant:

```bash
curl -X POST localhost:8080/api/items/1/variants -H 'Content-Type: application/json' \
  -d '{"name":"Grey - L","price":260000,"stock":12}'
```

Update stock or price of a variant:

```bash
curl -X PUT localhost:8080/api/variants/1 -H 'Content-Type: application/json' \
  -d '{"name":"Grey - L","price":265000,"stock":50}'
```

Sell an item without variants (create one with `stock: 40` first, here item 2):

```bash
curl -X POST localhost:8080/api/sales -H 'Content-Type: application/json' \
  -d '{"itemId":2,"quantity":5}'
```

```json
{"itemId":2,"variantId":null,"quantity":5,"unitPrice":75000.00,"totalPrice":375000.00,"remainingStock":35}
```

Sell a variant:

```bash
curl -X POST localhost:8080/api/sales -H 'Content-Type: application/json' \
  -d '{"itemId":1,"variantId":1,"quantity":2}'
```

Selling more than what is in stock returns `409 Conflict`:

```json
{"timestamp":"2026-10-14T13:03:45.622216Z","status":409,"message":"Insufficient stock for variant 1","errors":{}}
```

Error status codes: `400` validation / bad request, `404` not found, `409` insufficient stock.

## Design decisions

- **Layers**: controller -> service -> repository. Controllers only handle HTTP, services hold business rules and transactions.
- **DTOs (Java records)** for requests and responses, so entities are never exposed and Bean Validation rules live on the request.
- **Atomic stock decrease**: a sale runs `UPDATE ... SET stock = stock - :qty WHERE id = :id AND stock >= :qty`. If no row is updated the stock was not enough and the sale is rejected with 409. This prevents overselling and negative stock even with concurrent requests, without explicit locking.
- **Schema**: `item` (name, description, price, stock) and `variant` (item_id, name, price, stock). Price and stock live on both so an item can be sold either on its own or through its variants.
- **Prices** use `BigDecimal` (scale 2), never floating point.
- **Centralized error handling** with `@RestControllerAdvice` and a consistent JSON error body.
- **H2 in-memory** to keep the app runnable with zero setup.

## Assumptions

- An item either has variants or it does not. If an item has variants, it must be sold through a variant (`variantId` required) and the item's own stock is not used. Items without variants are sold directly.
- A variant has its own price, which may differ from the item's price.
- Stock is changed by creating or updating an item/variant (`PUT`) and by sales. There is no separate restock endpoint.
- Sales are not stored as history; `/api/sales` only validates and decreases stock and returns the result.
- No authentication, since it was out of scope.
- Data is lost on restart because the database is in-memory.
