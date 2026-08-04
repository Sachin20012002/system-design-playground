local key = KEYS[1]

local capacity = tonumber(ARGV[1])
local refillRate = tonumber(ARGV[2])
local nowMillis = tonumber(ARGV[3])
local ttlSeconds = tonumber(ARGV[4])

local values = redis.call(
    'HMGET',
    key,
    'tokens',
    'lastRefillTime'
)

local tokens = tonumber(values[1])
local lastRefillTime = tonumber(values[2])

if tokens == nil or lastRefillTime == nil then
    tokens = capacity
    lastRefillTime = nowMillis
end

local elapsedSeconds =
    (nowMillis - lastRefillTime) / 1000.0

local earnedTokens =
    elapsedSeconds * refillRate

tokens = math.min(
    capacity,
    tokens + earnedTokens
)

local allowed = 0

if tokens >= 1.0 then
    tokens = tokens - 1.0
    allowed = 1
end

redis.call(
    'HSET',
    key,
    'tokens',
    tokens,
    'lastRefillTime',
    nowMillis
)

redis.call('EXPIRE', key, ttlSeconds)

return allowed