/*
 *
 *  *
 *  *  * Copyright (c) 2026
 *  *  *
 *  *  * Author: Athar Gul
 *  *  * GitHub: https://github.com/DevAtrii/Kmp-Starter-Template
 *  *  * YouTube: https://www.youtube.com/@devatrii/videos
 *  *  *
 *  *  * All rights reserved.
 *  *
 *  *
 *
 */

package com.kmpstarter.generator_data.impl.zip

internal object StoreZip {

    fun pack(files: Map<String, ByteArray>): ByteArray {
        val local = ArrayList<Byte>()
        val central = ArrayList<Byte>()
        var offset = 0
        files.entries.sortedBy { it.key }.forEach { entry ->
            val name = entry.key
            val data = entry.value
            val entryName = name.trimStart('/').replace('\\', '/')
            val nameBytes = entryName.encodeToByteArray()
            val crc = crc32(data)
            val localHeader = buildLocalHeader(nameBytes, data, crc)
            local.addAll(localHeader)
            data.forEach { local.add(it) }
            central.addAll(buildCentralHeader(nameBytes, data, crc, offset))
            offset += localHeader.size + data.size
        }
        val eocd = buildEocd(
            entryCount = files.size,
            centralSize = central.size,
            centralOffset = local.size,
        )
        return (local + central + eocd).toByteArray()
    }

    fun unpack(bytes: ByteArray): Map<String, ByteArray> {
        val eocd = findEocd(bytes)
        val entryCount = u16(bytes, eocd + 10)
        val centralOffset = u32(bytes, eocd + 16)
        val result = linkedMapOf<String, ByteArray>()
        var cursor = centralOffset
        repeat(entryCount) {
            require(u32(bytes, cursor) == 0x02014b50) { "Invalid ZIP central directory" }
            val method = u16(bytes, cursor + 10)
            val compressedSize = u32(bytes, cursor + 20)
            val nameLen = u16(bytes, cursor + 28)
            val extraLen = u16(bytes, cursor + 30)
            val commentLen = u16(bytes, cursor + 32)
            val localOffset = u32(bytes, cursor + 42)
            val name = bytes.decodeToString(cursor + 46, cursor + 46 + nameLen)
            require(".." !in name.split('/', '\\')) { "Invalid ZIP entry: $name" }
            val dataStart = localOffset + 30 + u16(bytes, localOffset + 26) + u16(bytes, localOffset + 28)
            val payload = bytes.copyOfRange(dataStart, dataStart + compressedSize)
            if (!name.endsWith("/")) {
                result[name] = when (method) {
                    0 -> payload
                    else -> error("ZIP compression method $method is not supported")
                }
            }
            cursor += 46 + nameLen + extraLen + commentLen
        }
        return result
    }

    private fun buildLocalHeader(name: ByteArray, data: ByteArray, crc: Int): List<Byte> {
        val header = ArrayList<Byte>(30 + name.size)
        put32(header, 0x04034b50)
        put16(header, 20)
        put16(header, 0)
        put16(header, 0)
        put16(header, 0)
        put16(header, 0)
        put32(header, crc)
        put32(header, data.size)
        put32(header, data.size)
        put16(header, name.size)
        put16(header, 0)
        header.addAll(name.toList())
        return header
    }

    private fun buildCentralHeader(
        name: ByteArray,
        data: ByteArray,
        crc: Int,
        localOffset: Int,
    ): List<Byte> {
        val header = ArrayList<Byte>(46 + name.size)
        put32(header, 0x02014b50)
        put16(header, 20)
        put16(header, 20)
        put16(header, 0)
        put16(header, 0)
        put16(header, 0)
        put16(header, 0)
        put32(header, crc)
        put32(header, data.size)
        put32(header, data.size)
        put16(header, name.size)
        put16(header, 0)
        put16(header, 0)
        put16(header, 0)
        put16(header, 0)
        put32(header, 0)
        put32(header, localOffset)
        header.addAll(name.toList())
        return header
    }

    private fun buildEocd(entryCount: Int, centralSize: Int, centralOffset: Int): List<Byte> {
        val header = ArrayList<Byte>(22)
        put32(header, 0x06054b50)
        put16(header, 0)
        put16(header, 0)
        put16(header, entryCount)
        put16(header, entryCount)
        put32(header, centralSize)
        put32(header, centralOffset)
        put16(header, 0)
        return header
    }

    private fun findEocd(bytes: ByteArray): Int {
        var i = bytes.size - 22
        while (i >= 0) {
            if (u32(bytes, i) == 0x06054b50) return i
            i--
        }
        error("Invalid ZIP: missing end of central directory")
    }

    private fun crc32(data: ByteArray): Int {
        var crc = -1
        for (b in data) {
            val index = (crc xor (b.toInt() and 0xFF)) and 0xFF
            crc = CRC_TABLE[index] xor (crc ushr 8)
        }
        return crc.inv()
    }

    private fun put16(out: ArrayList<Byte>, value: Int) {
        out += (value and 0xFF).toByte()
        out += ((value ushr 8) and 0xFF).toByte()
    }

    private fun put32(out: ArrayList<Byte>, value: Int) {
        put16(out, value)
        put16(out, value ushr 16)
    }

    private fun u16(bytes: ByteArray, offset: Int): Int =
        (bytes[offset].toInt() and 0xFF) or
            ((bytes[offset + 1].toInt() and 0xFF) shl 8)

    private fun u32(bytes: ByteArray, offset: Int): Int =
        u16(bytes, offset) or (u16(bytes, offset + 2) shl 16)

    private val CRC_TABLE = IntArray(256) { n ->
        var c = n
        repeat(8) {
            c = if (c and 1 != 0) (c ushr 1) xor -0x12477CE0 else c ushr 1
        }
        c
    }
}
