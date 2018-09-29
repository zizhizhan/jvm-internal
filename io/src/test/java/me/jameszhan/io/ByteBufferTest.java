package me.jameszhan.io;

import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 * Date: 2018/9/17
 * Time: 下午4:11
 */
public class ByteBufferTest {

    @Test
    public void testBasic() {
        ByteBuffer buf1 = ByteBuffer.allocate(12);
        Assert.assertEquals(0, buf1.position());
        Assert.assertEquals(12, buf1.limit());
        Assert.assertEquals(12, buf1.capacity());

        buf1.putInt(1);
        Assert.assertEquals(4, buf1.position());
        Assert.assertEquals(12, buf1.limit());
        Assert.assertEquals(12, buf1.capacity());

        buf1.putInt(2);
        Assert.assertEquals(8, buf1.position());
        Assert.assertEquals(12, buf1.limit());
        Assert.assertEquals(12, buf1.capacity());

        buf1.putInt(3);
        Assert.assertEquals(12, buf1.position());
        Assert.assertEquals(12, buf1.limit());
        Assert.assertEquals(12, buf1.capacity());

        buf1.flip();
        Assert.assertEquals(0, buf1.position());
        Assert.assertEquals(12, buf1.limit());
        Assert.assertEquals(12, buf1.capacity());
    }

    @Test
    public void testFlip() {
        ByteBuffer buf1 = ByteBuffer.allocate(12);

        buf1.putInt(1);
        Assert.assertEquals(4, buf1.position());
        Assert.assertEquals(12, buf1.limit());
        Assert.assertEquals(12, buf1.capacity());

        buf1.flip();
        Assert.assertEquals(0, buf1.position());
        Assert.assertEquals(4, buf1.limit());
        Assert.assertEquals(12, buf1.capacity());
    }


    @Test
    public void testSlice() {
        ByteBuffer buf1 = ByteBuffer.allocate(12);
        buf1.putInt(1);
        buf1.putInt(2);
        buf1.putInt(3);

        buf1.flip();

        ByteBuffer buf2 = buf1.slice();
        Assert.assertEquals(1, buf2.getInt());
        Assert.assertEquals(2, buf2.getInt());
        Assert.assertEquals(3, buf2.getInt());
        Assert.assertEquals(12, buf2.position());

        buf1.position(4);
        Assert.assertEquals(2, buf1.getInt());
        Assert.assertEquals(8, buf1.position());
    }
}
