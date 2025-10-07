package com.sf.xts.api.sdk.marketdata;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.List;



    public class BinaryReader implements Closeable {

        /**
         * List of integers used to create arrays of integers where the length of
         * the list is not known before reading starts.
         */
        public final List<Integer> list = new ArrayList<Integer>();
        private ByteBuffer byteBuffer;
        private FileChannel channel;

        /**
         * Creates a new BinaryReader object from byte array.
         *
         * @param data byte array to use as the source.
         */
        public BinaryReader(byte[] data) {
            byteBuffer = ByteBuffer.wrap(data);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        }

        /**
         * Creates a new BinaryReader object from file input stream.
         *
         * @param fileInputStream an open stream to the data file.
         * @throws IOException if there was a problem accessing data file.
         */
        public BinaryReader(FileInputStream fileInputStream) throws IOException {
            channel = fileInputStream.getChannel();
            byteBuffer = channel.map(
                    MapMode.READ_ONLY,
                    0,
                    channel.size());
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        }

        /**
         * Creates a new BinaryReader from a byte buffer.
         *
         * @param byteBuffer mapped to the data file.
         */
        public BinaryReader(ByteBuffer byteBuffer) {
            this.byteBuffer = byteBuffer;
            this.byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        }

        /**
         * Sets position.
         *
         * @param pos position to set.
         */
        public void setPos(int pos) {
            byteBuffer.position(pos);
        }

        /**
         * @return current position in the byte buffer.
         */
        public int getPos() {
            return byteBuffer.position();
        }

        public byte readByte() {
            return byteBuffer.get();
        }

        public short readInt16() {
            return byteBuffer.getShort();
        }

        public int readUInt16() {
            short s = byteBuffer.getShort();
            int intVal = s >= 0 ? s : 0x10000 + s;
            return intVal;
        }

        public int readInt32() {
            return byteBuffer.getInt();
        }

        public boolean readBoolean() {
            return byteBuffer.get() != 0;
        }

        public byte[] readBytes(final int length) {
            byte[] bytes = new byte[length];
            byteBuffer.get(bytes);
            return bytes;
        }

        /**
         * Set the byte buffer to null to prevent any further access to the under
         * lying data. This should be done before the channel is closed as the
         * byte buffer could be tied to the channel. Any subsequent access to the
         * methods will fail with a null object exception.
         */
        @Override
        public void close() {
            byteBuffer = null;
            if (channel != null) {
                try {
                    channel.close();
                }
                catch (Exception e)
                {
                    // Do nothing.
                    System.err.println(e.getMessage());
                }
            }
        }
    }

