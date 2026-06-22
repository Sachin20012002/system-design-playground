# 06 - Database Design

# Introduction

PostgreSQL is the primary data store for the URL Shortener application.

It stores the mapping between generated short URLs and their corresponding long URLs, providing durable and reliable persistence.

The current implementation uses Spring Data JPA with Hibernate for object-relational mapping.

---

# Why PostgreSQL?

PostgreSQL was selected because it provides:

* ACID transactions
* Strong consistency
* Reliable persistence
* Excellent indexing support
* Mature SQL ecosystem
* Seamless integration with Spring Data JPA

For the current workload, a relational database is sufficient and provides fast indexed lookups.

---

# Database Schema

The application currently contains a single table.

```text
url_mapping
```

---

# Table Structure

| Column     | Type          | Constraints                 |
| ---------- | ------------- | --------------------------- |
| id         | BIGINT        | Primary Key, Auto Generated |
| short_code | VARCHAR(255)  | NOT NULL, UNIQUE            |
| long_url   | VARCHAR(2048) | NOT NULL                    |

---

# Entity Mapping

The `UrlMapping` entity is mapped to the `url_mapping` table.

```text
UrlMapping
│
├── id
├── shortCode
└── longUrl
```

Each row represents a single URL mapping.

---

# Primary Key

```text
id
```

The `id` column is generated automatically by PostgreSQL and uniquely identifies each record.

Although the application uses Base62 short codes externally, the primary key remains a numeric identifier for efficient storage and indexing.

---

# Unique Constraint

The `short_code` column is marked as unique.

```java
@Column(unique = true)
private String shortCode;
```

Hibernate creates a unique constraint, which PostgreSQL implements using a unique B-Tree index.

This guarantees that every short code is unique.

---

# Database Indexes

The current table contains the following indexes.

## Primary Key Index

```text
url_mapping_pkey
```

Type:

```text
B-Tree
```

Column:

```text
id
```

---

## Unique Index

```text
ukvu6b9jvlpylq7xur9v1q19sb
```

Type:

```text
B-Tree
```

Column:

```text
short_code
```

This index is automatically created by PostgreSQL when the unique constraint is applied.

---

# Query Pattern

The application's primary database query is:

```sql
SELECT *
FROM url_mapping
WHERE short_code = ?;
```

This query retrieves the original URL for a given short code.

---

# Query Execution Plan

The execution plan was verified using:

```sql
EXPLAIN ANALYZE
SELECT *
FROM url_mapping
WHERE short_code = 'B';
```

Result:

```text
Index Scan using ukvu6b9jvlpylq7xur9v1q19sb
```

This confirms that PostgreSQL uses the unique B-Tree index instead of performing a sequential table scan.

---

# Why Indexing Matters

Without an index, PostgreSQL would examine each row until the requested short code is found.

```text
Row 1
Row 2
Row 3
...
Row N
```

This is known as a sequential scan.

With the unique B-Tree index, PostgreSQL can locate the required row efficiently through the index structure.

As the table grows, indexed lookups remain significantly faster than scanning every row.

---

# Database Access Layer

The application uses Spring Data JPA.

Repository:

```text
UrlMappingRepository
```

Primary operations:

* Save URL mapping
* Find URL mapping by short code

Custom finder method:

```java
findByShortCode(String shortCode){}
```

Spring Data JPA automatically generates the corresponding SQL query.

---

# Persistence Flow

```text
Create URL

Controller
      │
      ▼
Service
      │
      ▼
Repository
      │
      ▼
PostgreSQL
```

---

```text
Redirect

Controller
      │
      ▼
Service
      │
      ▼
Repository
      │
      ▼
PostgreSQL
```

The database is accessed only when the requested URL is not already available in Redis.

---

# Current Limitations

The current implementation does not include:

* Database migrations
* Read replicas
* Table partitioning
* Sharding
* Backup and recovery strategy

These are intentionally excluded to keep the project focused on the core URL shortening workflow.

---

# Summary

PostgreSQL serves as the durable data store for the URL Shortener application.

The database schema is intentionally simple, consisting of a single table with a unique indexed `short_code` column that supports efficient redirect lookups.

The use of Spring Data JPA, automatic entity mapping, and indexed queries provides a clean persistence layer while maintaining good performance for the current workload.
