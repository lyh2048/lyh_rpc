package github.lyh2048.remoting.transport.client;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ChannelProvider {
    private final Map<String, Channel> channelMap;

    public ChannelProvider() {
        channelMap = new ConcurrentHashMap<>();
    }

    public Channel get(InetSocketAddress address) {
        String key = address.toString();
        if (channelMap.containsKey(key)) {
            Channel ch = channelMap.get(key);
            if (ch != null && ch.isActive()) {
                return ch;
            } else {
                channelMap.remove(key);
            }
        }
        return null;
    }

    public void set(InetSocketAddress address, Channel channel) {
        String key = address.toString();
        channelMap.put(key, channel);
        log.info("Channel Map Size: {}", channelMap.size());
    }

    public void remove(InetSocketAddress address) {
        String key = address.toString();
        channelMap.remove(key);
        log.info("Channel Map Size: {}", channelMap.size());
    }
}
