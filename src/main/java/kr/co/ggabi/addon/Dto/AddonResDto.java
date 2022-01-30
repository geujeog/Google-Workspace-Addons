package kr.co.ggabi.addon.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.xml.transform.Result;
import java.util.List;

@Getter
@NoArgsConstructor
public class AddonResDto {

    public String userName;
    public String mailId;

    public List<ResultDto> analyzedResult;

}
