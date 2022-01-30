package kr.co.ggabi.addon.Conntroller;

import kr.co.ggabi.addon.Dto.AddonReqDto;
import kr.co.ggabi.addon.Dto.AddonResDto;
import kr.co.ggabi.addon.Service.AnalysisService;
import kr.co.ggabi.addon.Service.AnalyzedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AddonController {

    private final AnalysisService analysisService;
    private final AnalyzedService analyzedService;

    @PostMapping("/analyze")
    public AddonResDto analysis(@RequestBody AddonReqDto addonReqDto) throws Exception{
        System.out.println("5678");

        System.out.println(addonReqDto.fileNames);
        return analysisService.analysis(addonReqDto);
    }

    @PostMapping("/analyzed")
    public AddonResDto inquire(@RequestBody AddonReqDto request) throws Exception{
        return analyzedService.analyzed(request);
    }


}
