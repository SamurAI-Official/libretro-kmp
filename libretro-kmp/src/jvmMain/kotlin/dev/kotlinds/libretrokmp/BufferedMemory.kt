package dev.kotlinds.libretrokmp

/**
 * JVM-only extension implemented by [LibretroCore]: memory reads into a caller-owned buffer.
 *
 * [LibretroCore.readMemory] allocates a new `ByteArray` per call, which is fine for occasional
 * reads but too expensive for a per-frame checksum (4 MiB per frame on a DS). [readMemoryInto]
 * writes into a reusable buffer instead. Like [BufferedSerialization], this is a JVM addition for
 * now; the common API is unchanged.
 */
interface BufferedMemory {
    /**
     * Copies up to `into.size` bytes of [type] into [into] and returns the number of bytes written,
     * or 0 when the core does not expose that region.
     */
    fun readMemoryInto(type: MemoryType, into: ByteArray): Int
}
