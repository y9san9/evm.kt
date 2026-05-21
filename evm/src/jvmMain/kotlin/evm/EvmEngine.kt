package evm

import java.util.concurrent.atomic.AtomicLong

internal actual class EvmRequestId actual constructor() {
    private val atomic = AtomicLong()
    actual fun next(): Long = atomic.getAndIncrement()
}
