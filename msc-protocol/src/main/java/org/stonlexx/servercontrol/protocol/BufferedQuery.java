package org.stonlexx.servercontrol.protocol;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.stonlexx.servercontrol.protocol.exception.OverflowPacketException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.ToIntFunction;

@UtilityClass
public class BufferedQuery {

    public void writeString(String s, ByteBuf buf) {
        if (s.length() > Short.MAX_VALUE) {
            throw new OverflowPacketException(String.format(
                    "Cannot send string longer than Short.MAX_VALUE (got %s characters)",
                    s.length()));
        }

        byte[] b = s.getBytes(Charsets.UTF_8);
        writeVarInt(b.length, buf);
        buf.writeBytes(b);
    }

    public <T> void writeArray(@NonNull T[] array,
                               @NonNull ByteBuf buf,
                               @NonNull BiConsumer<T, ByteBuf> dataWriter) {
        writeVarInt(array.length, buf);
        for (T value : array) {
            dataWriter.accept(value, buf);
        }
    }

    public void writeBoolean(boolean b, ByteBuf buf) {
        writeVarInt(b ? 1 : 0, buf);
    }

    public boolean readBoolean(ByteBuf buf) {
        return readVarInt(buf) == 1;
    }

    public long readVarLong(ByteBuf buf) {
        int numRead = 0;
        long result = 0;
        byte read;
        do {
            read = buf.readByte();
            int value = (read & 0b01111111);
            result |= ((long) value << (7 * numRead));

            numRead++;
            if (numRead > 10) {
                throw new RuntimeException("VarLong is too big");
            }
        } while ((read & 0b10000000) != 0);

        return result;
    }

    public UUID readUUID(ByteBuf input) {
        return new UUID(input.readLong(), input.readLong());
    }

    public void writeVarLong(long value, ByteBuf buf) {
        do {
            byte temp = (byte) (value & 0b01111111);
            // Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
            value >>>= 7;
            if (value != 0) {
                temp |= 0b10000000;
            }
            buf.writeByte(temp);
        } while (value != 0);
    }

    public <T> T[] readArray(@NonNull ByteBuf buf,
                             @NonNull Function<Integer, T[]> arrayCreator,
                             @NonNull Function<ByteBuf, T> dataReader) {

        int length = readVarInt(buf);
        T[] array = arrayCreator.apply(length);

        for (int i = 0; i < length; i++) {
            array[i] = dataReader.apply(buf);
        }

        return array;
    }

    public String readString(ByteBuf buf, ToIntFunction<ByteBuf> sizeSupplier) {
        int len = sizeSupplier.applyAsInt(buf);

        if (len > Short.MAX_VALUE) {
            throw new OverflowPacketException(String.format(
                    "Cannot receive string longer than Short.MAX_VALUE (got %s characters)", len));
        }

        byte[] b = new byte[len];

        buf.readBytes(b);
        return new String(b, Charsets.UTF_8);
    }

    public String readString(ByteBuf buf) {
        return readString(buf, BufferedQuery::readVarInt);
    }

    public void writeArray(byte[] b, ByteBuf buf) {
        if (b.length > Short.MAX_VALUE) {
            throw new OverflowPacketException(String.format(
                    "Cannot send byte array longer than Short.MAX_VALUE (got %s bytes)", b.length));
        }

        writeVarInt(b.length, buf);

        buf.writeBytes(b);
    }

    public byte[] toArray(ByteBuf buf) {
        byte[] ret = new byte[buf.readableBytes()];

        buf.readBytes(ret);
        return ret;
    }

    public byte[] readArray(ByteBuf buf) {
        return readArray(buf, buf.readableBytes());
    }

    public byte[] readArray(ByteBuf buf, int limit) {
        int len = readVarInt(buf);

        if (len > limit) {
            throw new OverflowPacketException(
                    String.format("Cannot receive byte array longer than %s (got %s bytes)", limit, len));
        }
        byte[] ret = new byte[len];

        buf.readBytes(ret);
        return ret;
    }

    public void writeStringArray(List<String> s, ByteBuf buf) {
        writeVarInt(s.size(), buf);
        for (String str : s) {
            writeString(str, buf);
        }
    }

    public List<String> readStringArray(ByteBuf buf) {
        int len = readVarInt(buf);
        List<String> ret = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            ret.add(readString(buf));
        }
        return ret;
    }

    public int readVarInt(ByteBuf input) {
        return readVarInt(input, 5);
    }

    public int readVarInt(ByteBuf input, int maxBytes) {
        int out = 0;
        int bytes = 0;
        byte in;

        do {
            in = input.readByte();

            out |= (in & 0x7F) << (bytes++ * 7);

            if (bytes > maxBytes) {
                throw new RuntimeException("VarInt too big");
            }

        } while ((in & 0x80) == 0x80);

        return out;
    }

    public void writeVarInt(int value, ByteBuf output) {
        int part;

        do {
            part = value & 0x7F;

            value >>>= 7;
            if (value != 0) {
                part |= 0x80;
            }

            output.writeByte(part);

        } while (value != 0);
    }

    public void writeEnum(Enum<?> en, ByteBuf buf) {
        writeVarInt(en.ordinal(), buf);
    }

    public <T> T readEnum(Class<T> clazz, ByteBuf buf) {
        return clazz.getEnumConstants()[readVarInt(buf)];
    }

    public int readVarShort(ByteBuf buf) {
        int low = buf.readUnsignedShort();
        int high = 0;
        if ((low & 0x8000) != 0) {
            low = low & 0x7FFF;
            high = buf.readUnsignedByte();
        }
        return ((high & 0xFF) << 15) | low;
    }

    public void writeVarShort(ByteBuf buf, int toWrite) {
        int low = toWrite & 0x7FFF;
        int high = (toWrite & 0x7F8000) >> 15;
        if (high != 0) {
            low = low | 0x8000;
        }
        buf.writeShort(low);
        if (high != 0) {
            buf.writeByte(high);
        }
    }

    public void writeUUID(UUID value, ByteBuf output) {
        output.writeLong(value.getMostSignificantBits());
        output.writeLong(value.getLeastSignificantBits());
    }

}
