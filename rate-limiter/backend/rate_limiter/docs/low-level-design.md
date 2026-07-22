# Low Level Design (V1)

# Data Structure

```java
ConcurrentHashMap<String, RateLimitInfo>
```

Key:

```text
Client IP Address
```

Value:

```text
RateLimitInfo
```

---

# RateLimitInfo

```java
public class RateLimitInfo {

    private AtomicInteger requestCount;

    private long windowStartTime;

}
```

---

# Example

```text
{
    "192.168.1.10"
        →
        {
            requestCount = 3,
            windowStartTime = 1711111111111
        }
}
```

---

# Algorithm

```text
Request arrives
        ↓
Fetch client information
        ↓
Window expired ?
        ↓
YES → Reset counter
NO  → Continue
        ↓
Increment request counter
        ↓
Counter exceeds limit ?
        ↓
YES → Reject request
NO  → Allow request
```

---

# Thread Safety

The implementation uses:

- ConcurrentHashMap
- AtomicInteger
- Per-client synchronization

to ensure correctness under concurrent access.

---

# Complexity

## Time Complexity

```text
O(1)
```

Average case.

---

## Space Complexity

```text
O(N)
```

where:

```text
N = number of unique clients
```

---

# Known Limitations

## Boundary Problem

A client may send:

```text
100 requests at 10:00:59
100 requests at 10:01:00
```

Result:

```text
200 requests within a few seconds.
```

This limitation motivates the Sliding Window algorithm.

---

## Memory Growth

Inactive client entries are never removed.

Future versions may introduce cleanup mechanisms.

---

## Single Instance Limitation

Counters are stored in local memory and cannot be shared across multiple application instances.