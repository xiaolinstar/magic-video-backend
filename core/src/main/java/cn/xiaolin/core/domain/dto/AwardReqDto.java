package cn.xiaolin.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 奖项信息请求Dto
 * @create 2023/7/30
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AwardReqDto {
    private Long id;
    private String name;
    private String icon;
    private Integer year;
    private String country;
    private Long bestDirectorId;
    private Long bestDirectorNominationId;
    private Long bestMaleActorId;
    private Long bestFemaleActorId;
    private Long bestMaleActorNominationId;
    private Long bestFemaleActorNominationId;
    private Long bestScripterId;
    private Long bestScripterNominationId;
    private Long bestNewActorId;
}
