package github.lyh2048;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Hello implements Serializable {
    private static final long serialVersionUID = 5858153165033638075L;
    private String message;
    private String description;
}
