package github.lyh2048.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public enum RpcResponseCodeEnum {
    SUCCESS(0, "成功"),
    FAIL(-1, "失败");

    private final int code;
    private final String message;
}
