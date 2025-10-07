package com.sf.xts.api.sdk.marketdata;
import java.io.ByteArrayOutputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class ZipInflate {

        public static byte[]  pakoInflateRaw(byte[] data) throws DataFormatException {
            // Create an inflater for raw DEFLATE data (no zlib header)
            Inflater inflater = new Inflater(true); // 'true' means raw DEFLATE stream
            inflater.setInput(data);

            // Output stream to hold the decompressed data
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];

            try {
                while (!inflater.finished()) {
                    int count = inflater.inflate(buffer);
                    outputStream.write(buffer, 0, count);
                }
            } catch (DataFormatException e) {
                DataFormatException dataFormatException = new DataFormatException("Error while decompressing: " + e.getMessage());
                dataFormatException.initCause(e);  // Set the original exception as the cause
                throw dataFormatException;
            } finally {
                inflater.end(); // Always close the inflater
            }

            return outputStream.toByteArray(); // Return decompressed data
        }

}
