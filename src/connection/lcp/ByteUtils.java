package connection.lcp;

import java.nio.ByteBuffer;

public class ByteUtils {
    private static ByteBuffer longBuffer = ByteBuffer.allocate(Long.BYTES);
    private static ByteBuffer shortBuffer = ByteBuffer.allocate(Short.BYTES);
    
    public static byte[] longToBytes(long x) {
        longBuffer.putLong(0, x);
        return longBuffer.array();
    }

    public static long bytesToLong(byte[] bytes) {
        longBuffer.put(bytes, 0, bytes.length);
        longBuffer.flip();//need flip 
        return longBuffer.getLong();
    }
    
    public static byte[] shortToBytes(short x) {
    	shortBuffer.putShort(x);
    	return shortBuffer.array();
    }
    
    public static short bytesToShort(byte[] bytes) {
    	shortBuffer.put(bytes, 0, bytes.length);
    	shortBuffer.flip();
    	return shortBuffer.getShort();
    }
}