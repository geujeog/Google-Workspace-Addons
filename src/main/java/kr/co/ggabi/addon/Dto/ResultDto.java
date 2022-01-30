package kr.co.ggabi.addon.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResultDto {

    public String fileName;
    public double malicious; /* 0.0 < 악성 확률값 < 1.0 */
                             /* -1 값을 가질 경우 분석 불가 */
}
