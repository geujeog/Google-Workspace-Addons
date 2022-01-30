package kr.co.ggabi.addon.Service;

import kr.co.ggabi.addon.Domain.Addon;
import kr.co.ggabi.addon.Dto.AddonReqDto;
import kr.co.ggabi.addon.Dto.AddonResDto;
import kr.co.ggabi.addon.Dto.ResultDto;
import kr.co.ggabi.addon.Repository.AddonRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AnalyzedService {

    private final AddonRepository addonRepository;

    public AnalyzedService(AddonRepository addonRepository) {
        this.addonRepository = addonRepository;
    }

    public AddonResDto analyzed(AddonReqDto request){

        AddonResDto dto = new AddonResDto();
        List<ResultDto> rDto = new ArrayList<>();
        String userName = request.userName;
        String mailId = request.mailId;

        Optional<List<Addon>> optional = addonRepository.findByUserNameAndMailId(userName, mailId);
        if (optional.isPresent() && !optional.get().isEmpty()) {
            List<Addon> list = optional.get();

            dto.userName = userName;
            dto.mailId = mailId;


            for(Addon addon : list){
                ResultDto resultDto = new ResultDto();
                resultDto.fileName = addon.fileName;
                resultDto.malicious = addon.malicious;

                rDto.add(resultDto);
            }
            dto.analyzedResult = rDto;
        }

        return dto;

    }

}
