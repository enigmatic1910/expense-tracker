package com.project.expensetracker.service.userConfig;


import com.project.expensetracker.entity.UserConfig;

public interface UserConfigService {
    UserConfig getByUserId(String id);
}
