package com.SmartEmailReply.SmartEmailReply.Entity;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailRequest {

    private String emailContent;
    private String tone;
}
