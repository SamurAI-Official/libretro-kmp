package dev.kotlinds.libretrokmp

/**
 * JVM-only extension implemented by [LibretroCore]: serialization into a caller-owned buffer.
 *
 * The standard [LibretroCore.saveState] / [LibretroCore.loadState] API allocates a `ByteArray` per
 * call, which is fine for menu save states but too expensive for rollback netcode (one save per
 * frame). These methods keep the allocation out of the frame loop. They are a JVM addition for now;
 * the common API is unchanged.
 */
interface BufferedSerialization {
    /** Size in bytes required by [saveStateInto], or 0 when the core has no save states. */
    fun serializeSize(): Long

    /**
     * Serializes the current state into [buffer] (which must be at least [serializeSize] bytes) and
     * returns the number of bytes written, or -1 on failure.
     */
    fun saveStateInto(buffer: ByteArray): Int

    /** Restores [size] bytes from the beginning of [buffer]. */
    fun loadState(buffer: ByteArray, size: Int): Boolean
}
