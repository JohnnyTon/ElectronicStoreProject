# Electronic Store API Documentation

This document contains the full list of RESTful API endpoints provided by the Electronic Store system.

---

## Authentication

Currently, no authentication is implemented.

---

## Admin Endpoints

### Products

#### Create Product
- **POST** `/admin/products`
- **Body**: 
```json
{
  "name": "MacBook Pro",
  "description": "Apple M2 Pro laptop",
  "price": 1999.0,
  "category": "Laptop",
  "available": true,
  "stock": 10
}
```
- **Response**: `201 Created`  
Returns the created product with its ID and timestamps.

#### List All Products (with Pagination)
- **GET** `/admin/products?page=0&size=5`
- **Response**: `200 OK`  
Returns paginated products list.

---

### Discount Deals

#### Create Deal
- **POST** `/admin/deals`
- **Body**: 
```json
{
  "productIds": [1, 2],
  "type": "PERCENTAGE",
  "discountPercent": 10.0,
  "buyQuantity": 0,
  "getQuantity": 0,
  "expiresAt": "2025-12-31T23:59:59"
}
```
- **Response**: `201 Created`

#### Get Active Deals for Product
- **GET** `/admin/deals/product/1`
- **Response**: `200 OK`  
Returns active discount deals for product ID `1`.

---

## Customer Endpoints

### Basket

#### Add to Basket
- **POST** `/basket/items`
- **Body**: 
```json
{
  "customerId": "uuid-here",
  "productId": 1,
  "quantity": 2
}
```
- **Response**: `201 Created`

#### Remove from Basket
- **DELETE** `/basket/items`
- **Body**: 
```json
{
  "customerId": "uuid-here",
  "productId": 1,
  "quantity": 1
}
```
- **Response**: `200 OK`

#### View Basket
- **GET** `/basket/{customerId}`
- **Response**: `200 OK`  
Returns the current contents of the customer's basket.

#### Checkout
- **GET** `/basket/{customerId}/checkout`
- **Response**: `200 OK`  
Returns a receipt object with items, prices, deals applied, and total.

---

## Error Handling

All responses are wrapped in:
```json
{
  "status": "SUCCESS" | "FAILURE",
  "message": "Details",
  "data": {...}
}
```

---

## Notes

- Products can be filtered by category, price range, and availability.
- Product stock is updated when added or removed from the basket.
- If stock is insufficient, the response will include a graceful error.

---
