package cn.largequant.cloudcommon.entity.notice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageVo {

    private int id;
    private int fromUserId;
    private int toUserId;
    private String content;
    private Date createTime;
    private int hasRead;
    private String conversation_id;

}
