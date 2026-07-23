# Low Level Design (V2)

# Data Structure

```java
ConcurrentHashMap<String, ClientRequestLog>
```

Key

```text
Client IP Address
```

Value

```text
ClientRequestLog
```

---

# ClientRequestLog

```java
public class ClientRequestLog {

    private final Deque<Long> requestTimestamps;

}
```

Each timestamp represents the time at which a request was accepted.

---

# Example

```text
{
    "192.168.1.10"
        →
        [
            1711111110000,
            1711111114000,
            1711111118000,
            1711111123000
        ]
}
```

---

# Sliding Window Algorithm

For every incoming request:

```text
Current Time
      ↓
Calculate Window Start Time
      ↓
Remove Expired Timestamps
      ↓
Remaining Requests >= Limit ?
      ↓
YES ─────────► Reject Request
      │
      NO
      │
      ▼
Add Current Timestamp
      ↓
Allow Request
```

---

# Thread Safety

The implementation uses:

- ConcurrentHashMap
- Per-client synchronization

to safely process concurrent requests.

Each client is synchronized independently, allowing multiple clients to be processed concurrently.

---

# Time Complexity

## Average

```text
O(1)
```

Each timestamp is:

- inserted once
- removed once

making the algorithm amortized O(1).

---

# Space Complexity

```text
O(N × R)
```

where:

- **N** = number of unique clients
- **R** = maximum requests allowed per client within the configured window

Unlike the Fixed Window algorithm, memory usage grows with the number of stored timestamps.

---

# Advantages

- Eliminates the Fixed Window boundary problem
- Provides accurate rate limiting
- Smooth request distribution
- Simple implementation
- Easy to understand and test

---

# Current Limitations

## Higher Memory Usage

Every accepted request stores a timestamp until it expires.

---

## No Cleanup

Inactive client entries remain in memory.

---

## Single Application Instance

Request history is stored locally and cannot be shared across multiple application instances.

This will be addressed in future versions using Redis.