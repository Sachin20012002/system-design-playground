# Project Notes

These are implementation notes, design decisions, and clarifications gathered while building the project.

---

## V1 - Fixed Window

### Why ConcurrentHashMap?

Each client maintains an independent counter.

---

## Why synchronized?

ConcurrentHashMap protects the map.

It does not protect mutation of the value stored inside it.

Therefore synchronization is still required.

---

## V2 - Sliding Window

### Why Deque?

Old timestamps are removed from the head.

New timestamps are appended to the tail.

This gives O(1) amortized complexity.

---

## Why Sliding Log instead of Sliding Counter?

Sliding Log is easier to understand and more accurate.

Sliding Counter is more memory efficient but more complex.

---

## V3 - Token Bucket

### Why double instead of int?

Tokens can refill fractionally.

Example:

1 token/sec

250ms elapsed

0.25 tokens earned.

---

### Why lazy refill?

Avoid background scheduler.

Tokens are replenished only when requests arrive.

---

### Why initialize bucket as full?

New clients should not be rate limited immediately.

---

## V4 - Leaky Bucket

### Why does this implementation look similar to Token Bucket?

This project implements Leaky Bucket as a rate limiter.

The original Leaky Bucket algorithm models an actual queue.

Instead of storing queued requests, this implementation stores only queue occupancy (water level).

This keeps the implementation simple and suitable for HTTP APIs.

---

### Why not maintain a real queue?

A real queue would delay HTTP responses.

For synchronous REST APIs this causes:

- Long-held connections
- Increased thread usage
- Client timeouts

Returning HTTP 429 is generally preferred.

---

### When is a real Leaky Bucket useful?

- Network traffic shaping
- Message queues
- Background job processing
- Printer queues