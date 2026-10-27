package dev.kotlinds.libretrokmp

import com.sun.jna.Callback
import com.sun.jna.Pointer

/**
 * JVM-only extension implemented by [LibretroCore]: routing the console's local wireless radio
 * through the frontend instead of the core's own LAN transport.
 *
 * A DS core that emulates local multiplayer normally broadcasts its radio packets on the local
 * network (melonDS uses a NIFI UDP broadcast on port 7064). That is enough for two instances on one
 * LAN in real time, but it cannot be netplayed: the packets arrive whenever the network delivers
 * them, so the two emulated consoles see different radio timing. A patched core (see
 * libretro-rollback's `docs/patches`) exports
 *
 * ```c
 * void t6_link_set_callbacks(int (*send)(const u8* data, int len), int (*recv)(u8* data, int len));
 * ```
 *
 * and routes its radio through the frontend while the callbacks are installed. A stock core does not
 * export the symbol, [installLink] returns false, and the core keeps its own transport.
 *
 * Like [BufferedSerialization] and [CoreDirtyPages], this is a JVM addition for now.
 */
interface CoreLink {
    /**
     * Installs the callbacks. Returns false when the core has no link ABI. Install them before the
     * emulated console powers its radio on; the core skips opening its own socket once installed.
     */
    fun installLink(channel: LinkChannel): Boolean

    /** Removes the callbacks, restoring the core's own transport. */
    fun removeLink()

    /**
     * Diagnostic round trip through the core's radio path: sends a four-byte probe and reads it back
     * through the same `MP_SendPacket` / `MP_RecvPacket` routing the emulated radio uses.
     *
     * Returns bit 0 when the radio transmitted it, bit 1 when the radio received it back, and 0 when
     * the core has no link ABI. It lets an integration test prove the callbacks are wired without
     * needing a game that is inside a local wireless screen.
     */
    fun linkSelfTest(): Int
}

/**
 * The core's radio I/O. Both methods are called from inside `retro_run`, on the thread that owns the
 * emulator, and must never block: the emulated radio protocol retransmits, and waiting for the
 * network would stall the console.
 */
interface LinkChannel {
    /** The radio transmitted [length] bytes of [data]. Returns the bytes accepted (0 drops it). */
    fun onLinkSend(data: ByteArray, length: Int): Int

    /**
     * The radio polls for a packet. Writes the packet into [data] and returns its length, or 0 when
     * there is nothing to receive.
     */
    fun onLinkReceive(data: ByteArray): Int
}

/** `int (*)(const u8* data, int len)` */
fun interface LinkSendCallback : Callback {
    fun invoke(data: Pointer, length: Int): Int
}

/** `int (*)(u8* data, int len)` */
fun interface LinkRecvCallback : Callback {
    fun invoke(data: Pointer, capacity: Int): Int
}
