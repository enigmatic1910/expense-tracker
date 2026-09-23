package com.project.expensetracker.service.userConfig;


import com.project.expensetracker.dto.UserConfigDto;
import com.project.expensetracker.entity.UserConfig;

public interface UserConfigService {
    UserConfig getByUserId(String id);

    UserConfigDto getConfig(String userId);

    UserConfigDto updateConfig(UserConfigDto userConfigDto, String userId);
}
