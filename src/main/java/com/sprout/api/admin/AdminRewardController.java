package com.sprout.api.admin;

import com.sprout.api.common.client.dto.RegionInfoDto;
import com.sprout.api.common.client.dto.FileMetaData;
import com.sprout.api.common.utils.FileMetaDataExtractor;
import com.sprout.api.gis.domain.RegionRepository;
import com.sprout.api.gis.util.GisUtil;
import com.sprout.api.reward.application.RewardService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/rewards")
@RequiredArgsConstructor
public class AdminRewardController {

    private final RewardService rewardService;
    private final FileMetaDataExtractor fileMetaDataExtractor;
    private final RegionRepository regionRepository;

    @GetMapping
    public String getSpecialRegionRewardPage(Model model) {
        List<RegionInfoDto> specialRegions = regionRepository.findSpecialRegionNames();
        model.addAttribute("specialRegions", specialRegions);
        return "admin/reward-upload";
    }

    @PostMapping("/upload")
    public String uploadSpecialRegionReward(
        @RequestParam("file") MultipartFile file,
        @RequestParam("regionName") String regionName,
        @RequestParam("regionCode") String regionCode,
        RedirectAttributes redirectAttributes
    ) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "파일을 선택해주세요.");
            return "redirect:/admin/rewards";
        }

        FileMetaData fileMetaData = fileMetaDataExtractor.extractFrom(file);
        String imageUrl = rewardService.upload(fileMetaData, regionName, regionCode);

        redirectAttributes.addFlashAttribute("success",
            String.format("지역 '%s'의 리워드 이미지가 성공적으로 업로드되었습니다. (URL: %s)", regionName, imageUrl));

        return "redirect:/admin/rewards";
    }

    @GetMapping("/normal")
    public String getNormalRegionRewardPage(Model model) {
        List<RegionInfoDto> specialRegions = regionRepository.findNormalRegionNames();
        Map<String, List<RegionInfoDto>> maps = new HashMap<>();
        for (RegionInfoDto regionInfoDto : specialRegions) {
            String key = GisUtil.getRegionName(regionInfoDto.getRegionCode().substring(0, 2));
            List<RegionInfoDto> get = maps.getOrDefault(key, new ArrayList<>());
            get.add(regionInfoDto);
            maps.put(key, get);
        }

        model.addAttribute("normalRegionsMap", maps);
        return "admin/reward-upload-normal";
    }

    @PostMapping("/upload/normal")
    public String uploadNormalRegionReward(
        @RequestParam("file") MultipartFile file,
        @RequestParam("regionName") String regionName,
        @RequestParam("regionCode") String regionCode,
        RedirectAttributes redirectAttributes
    ) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "파일을 선택해주세요.");
            return "redirect:/admin/rewards/normal";
        }

        FileMetaData fileMetaData = fileMetaDataExtractor.extractFrom(file);
        String imageUrl = rewardService.upload(fileMetaData, regionName, regionCode);

        redirectAttributes.addFlashAttribute("success",
            String.format("지역 '%s'의 리워드 이미지가 성공적으로 업로드되었습니다. (URL: %s)", regionName, imageUrl));

        return "redirect:/admin/rewards/normal";
    }
}