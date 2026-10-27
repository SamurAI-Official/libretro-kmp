package dev.kotlinds.libretrokmp

/**
 * JVM-only extension implemented by [LibretroCore]: the core's dirty-page bitmap for its serialized
 * state, when the core exports one.
 *
 * A patched core (see libretro-rollback's `docs/patches`) exports
 * `size_t t6_rollback_dirty_pages(uint8_t* out, size_t pages)`: it writes one byte per 4 KiB page of
 * the buffer `retro_serialize` fills, non-zero when that page changed since the previous save, and
 * returns the number of pages. Rollback's delta snapshots use it to skip their per-frame page scan.
 *
 * Like [BufferedSerialization] and [BufferedMemory], this is a JVM addition for now; the common API
 * is unchanged, and a core without the symbol returns 0.
 */
interface CoreDirtyPages {
    /**
     * Fills [out] with the dirty-page bitmap and returns the number of pages written, or 0 when the
     * core has no dirty-page support (or [out] is too small).
     */
    fun dirtyPages(out: ByteArray): Int
}
