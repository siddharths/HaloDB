package amannaly;

import java.nio.ByteBuffer;

/**
 * @author Arjun Mannaly
 */
public class IndexFileEntry {

    /**
     * Key size      - 2 bytes.
     * record size   - 4 bytes.
     * record offset - 8 bytes.
     * flags          - 1 byte.
     */
    public final static int INDEX_FILE_HEADER_SIZE = 15;

    public static final int KEY_SIZE_OFFSET = 0;
    public static final int RECORD_SIZE_OFFSET = 2;
    public static final int RECORD_OFFSET = 6;
    public static final int FLAG_OFFSET = 14;
    public static final int KEY_OFFSET = 15;

    private final byte[] key;
    private final int recordSize;
    private final long recordOffset;
    private final short keySize;
    private final byte flags;

    public IndexFileEntry(byte[] key, int recordSize, long recordOffset, byte flags) {
        this.key = key;
        this.recordSize = recordSize;
        this.recordOffset = recordOffset;
        this.flags = flags;

        this.keySize = (short)key.length;
    }

    public ByteBuffer[] serialize() {
        byte[] header = new byte[INDEX_FILE_HEADER_SIZE];
        ByteBuffer h = ByteBuffer.wrap(header);

        h.putShort(KEY_SIZE_OFFSET, keySize);
        h.putInt(RECORD_SIZE_OFFSET, recordSize);
        h.putLong(RECORD_OFFSET, recordOffset);
        h.put(FLAG_OFFSET, flags);

        return new ByteBuffer[] { h, ByteBuffer.wrap(key) };
    }

    public static IndexFileEntry deserialize(ByteBuffer buffer) {
        short keySize = buffer.getShort();
        int recordSize = buffer.getInt();
        long offset = buffer.getLong();
        byte flag = buffer.get();

        byte[] key = new byte[keySize];
        buffer.get(key);

        return new IndexFileEntry(key, recordSize, offset, flag);
    }

    public byte[] getKey() {
        return key;
    }

    public int getRecordSize() {
        return recordSize;
    }

    public long getRecordOffset() {
        return recordOffset;
    }

    public byte getFlags() {
        return flags;
    }

    /**
     * tombstones will have the lsb of flags set to 1
     */
    public boolean isTombStone() {
        return (flags & 1) == 1;
    }
}