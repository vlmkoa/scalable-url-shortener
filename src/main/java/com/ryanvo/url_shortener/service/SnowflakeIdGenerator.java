package com.ryanvo.url_shortener.service;

import java.time.Instant;

public class SnowflakeIdGenerator {
    private static final int NODE_ID_BITS = 10;
    private static final int SEQUENCE_BITS = 12;

    private static final long MAX_NODE_ID = (1L << NODE_ID_BITS) - 1;
    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;

    private static final long NODE_ID_SHIFT = SEQUENCE_BITS;
    private static final long EPOCH_SHIFT = SEQUENCE_BITS + NODE_ID_BITS;

    private static final long CUSTOM_EPOCH = 1704067200000L;

    private final long nodeId;
    private long lastTimestamp = -1L;
    private long sequence = 0L;

    public SnowflakeIdGenerator(long nodeId) {
        if (nodeId < 0 || nodeId > MAX_NODE_ID) {
            throw new IllegalArgumentException(String.format("NodeId must be between 0 and %d", MAX_NODE_ID));
        }
        this.nodeId = nodeId;
    }

    public synchronized long nextId() {
        long currentTimestamp = timestamp();

        if (currentTimestamp < lastTimestamp)
        {
            long offset = lastTimestamp - currentTimestamp;

            // wait it out if small offset
            if (offset <= 5) {
                try
                {
                    wait(offset + 1); // Pause the thread
                    currentTimestamp = timestamp();

                    // Re-check just to be safe
                    if (currentTimestamp < lastTimestamp)
                    {
                        throw new IllegalStateException("Clock is still broken.");
                    }
                }
                catch (InterruptedException e)
                {
                    throw new RuntimeException(e);
                }
            }
            else
            {
                // if the jump is big, crash
                throw new IllegalStateException("Clock moved backwards by " + offset + "ms");
            }
        }

        if (currentTimestamp == lastTimestamp)
        {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0)
            {
                currentTimestamp = waitNextMillis(currentTimestamp);
            }
        }
        else
        {
            sequence = 0L;
        }

        lastTimestamp = currentTimestamp;

        return ((currentTimestamp - CUSTOM_EPOCH) << EPOCH_SHIFT)
                | (nodeId << NODE_ID_SHIFT)
                | sequence;
    }

    private long waitNextMillis(long currentTimestamp) {
        while (currentTimestamp <= lastTimestamp) {
            currentTimestamp = timestamp();
        }
        return currentTimestamp;
    }

    private long timestamp() {
        return Instant.now().toEpochMilli();
    }
}
