package github.lyh2048.compress;

import github.lyh2048.extension.SPI;

@SPI
public interface Compress {
    byte[] compress(byte[] bytes);
    byte[] decompress(byte[] bytes);
}
