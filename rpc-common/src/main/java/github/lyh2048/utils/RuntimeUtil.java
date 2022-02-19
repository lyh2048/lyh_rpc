package github.lyh2048.utils;

public class RuntimeUtil {
    /**
     * 获取CPU的核心数
     * @return cpu核心数
     */
    public static int cpus() {
        return Runtime.getRuntime().availableProcessors();
    }
}
