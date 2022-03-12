package kr.co.ggabi.addon.Service;

import kr.co.ggabi.addon.Domain.Addon;
import kr.co.ggabi.addon.Dto.AddonReqDto;
import kr.co.ggabi.addon.Dto.AddonResDto;
import kr.co.ggabi.addon.Dto.ResultDto;
import kr.co.ggabi.addon.Repository.AddonRepository;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AnalysisService {

    private static final int BUFFER_SIZE = 4096;
    private final AddonRepository addonRepository;

    public AnalysisService(AddonRepository addonRepository, Optional<Addon> addon) {
        this.addonRepository = addonRepository;
    }

    public void checkBeforeAnalysis(AddonReqDto addonReqDto, List<ResultDto> resultDto){

        for(String filename : addonReqDto.fileNames){
            ResultDto dto = new ResultDto();
            dto.fileName = filename;
            dto.malicious = 0.0;

            String[] fileExt = filename.split(".");

            /* 체크 1. 확장자가 없거나 2개 이상일 경우 악성 */
            //System.out.println(fileExt.length);
            if(fileExt.length == 0 || fileExt.length > 2){
                dto.malicious = -1;
            }

            /* 체크 2. 분석 가능한 확장자인지 검사 */
            else if(!(fileExt[fileExt.length-1]).equals("pdf")
                    || !(fileExt[fileExt.length-1]).equals("docx")
                    || !(fileExt[fileExt.length-1]).equals("xlsx")
                    || !(fileExt[fileExt.length-1]).equals("hwp"))
            {
                dto.malicious = -1;
            }

            /* 데이터 베이스에 존재하는 값인지 검사 */
            if(dto.malicious >= 0.0)
            {
                Optional<Addon> addon = addonRepository.findByUserNameAndMailIdAndFileName(addonReqDto.userName, addonReqDto.mailId, filename);
                if(!addon.isPresent()){
                    dto.malicious = addon.get().malicious;
                }
            }

            resultDto.add(dto);
        }
    }


    public AddonResDto analysis(AddonReqDto addonReqDto){

        AddonResDto addonResDto = new AddonResDto();
        List<ResultDto> resultDto = new ArrayList<>();
        addonResDto.userName = addonReqDto.userName;
        addonResDto.mailId = addonReqDto.mailId;


        // 0. 체크
        checkBeforeAnalysis(addonReqDto, resultDto);

        // 1. blob to file
        int listIdx = 0;
        for(String fileData : addonReqDto.files){
            String path = "./downloads"+ File.separator+addonResDto.userName+File.separator+addonResDto.mailId+File.separator;
            String fileName = addonReqDto.fileNames.get(listIdx);
            String fileNameExt = fileName.substring(fileName.lastIndexOf(".") + 1);
            StringBuilder save = new StringBuilder(path + fileName);

            File createFile = new File(path);
            if(!createFile.exists()){
                createFile.mkdirs();

                File saveFile = new File(save.toString());
                if(saveFile.isFile()){
                    try {
                        OutputStream outputStream = new FileOutputStream(save.toString());

                        outputStream.write(fileData.getBytes(StandardCharsets.UTF_8));

                        outputStream.close();
                        System.out.println("File saved");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            // 2. 첨부파일 분석
            if(resultDto.get(listIdx).malicious == 0.0) {
                double result = staticAnalysis(fileNameExt, path, fileName);
                resultDto.get(listIdx).malicious = result;
                addonResDto.analyzedResult = resultDto;


                // 3. 결과값 DB 저장
                addonRepository.save(Addon.builder()
                        .userName(addonResDto.userName)
                        .mailId(addonResDto.mailId)
                        .fileName(fileName)
                        .malicious(result)
                        .build());

                System.out.println(fileName);
                System.out.println(result);
            }

        }


        /*int listIdx = 0;
        for(MultipartFile file : addonReqDto.files){

            // 1. 첨부파일 다운로드
            String path = "./downloads"+File.separator+addonResDto.userName+File.separator+addonResDto.mailId+File.separator;
            StringBuilder save = new StringBuilder(path + file.getOriginalFilename());

            String fileNameExExt = file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf("."));
            String fileNameExt = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);

            StringBuilder saveNewFile = new StringBuilder();
            StringBuilder saveNewFileName = new StringBuilder();

            File createFile = new File(path);
            if(!createFile.exists()){
                createFile.mkdirs();

                File saveFile = new File(save.toString());
                if(saveFile.isFile()){
                    boolean fileExist = true;
                    int index = 0;

                    while(fileExist){
                        index++;
                        saveNewFileName = new StringBuilder(fileNameExExt + "(" + index + ")." + fileNameExt);
                        saveNewFile = new StringBuilder(path + saveNewFileName.toString());

                        fileExist = new File(saveNewFile.toString()).isFile();
                        if(!fileExist){
                            save = new StringBuilder(saveNewFile);
                        }
                    }
                }
            }


            try {
                System.out.println(save.toString());
                file.transferTo(new File(save.toString()));

                // 2. 첨부파일 분석
                if(file.getOriginalFilename().equals(resultDto.get(listIdx).fileName)){
                    if(resultDto.get(listIdx).malicious == 0) {
                        double result = staticAnalysis(fileNameExt, path, saveNewFileName.toString());
                        resultDto.get(listIdx).malicious = result;
                        addonResDto.analyzedResult = resultDto;

                        // 3. 결과값 DB 저장
                        addonRepository.save(Addon.builder()
                                .userName(addonResDto.userName)
                                .mailId(addonResDto.mailId)
                                .fileName(file.getOriginalFilename())
                                .malicious(result)
                                .build());

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            listIdx++;
        }*/

        return addonResDto;

    }


    public double staticAnalysis(String fileExtension, String path, String fileName){
        String command = "/usr/bin/python3.8";
        String hwpArg = "./model/file_predict.py";




        /*if ("hwp".equals(fileExtension))
            return hwp_model.hwp_model_predict(path, fileName)[0];
        else if ("pdf".equals(fileExtension))
            return pdf_model.pdf_model_predict(path, fileName)[0];
        else if ("docx".equals(fileExtension))
            return docx_model.docx_model_predict(path, fileName)[0];
        else if ("xlsx".equals(fileExtension))
            return xlsx_model.xlsx_model_predict(path, fileName)[0];
        else
            return -1;
        */
        return -1;
    }

}
