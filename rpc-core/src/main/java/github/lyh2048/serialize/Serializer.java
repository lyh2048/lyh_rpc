package github.lyh2048.serialize;

import github.lyh2048.extension.SPI;

@SPI
public interface Serializer {
    /**
     * 序列化
     * @param obj 要序列化的对象
     * @return 字节数组
     */
    byte[] serialize(Object obj);

    /**
     * 反序列化
     * @param bytes 字节数组
     * @param clazz 目标类
     * @param <T> 类型
     * @return 反序列化的对象
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
