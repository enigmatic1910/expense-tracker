package com.project.expensetracker.service.userConfig;

import com.project.expensetracker.entity.UserConfig;
import com.project.expensetracker.exception.UserConfigNotFoundException;
import com.project.expensetracker.repo.UserConfigRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserConfigServiceImpl implements UserConfigService {

    private final UserConfigRepo userConfigRepo;


    @Override
    public UserConfig getByUserId(String userId) {
        return userConfigRepo.findByUserId(userId)
                .orElseThrow(() -> new UserConfigNotFoundException(userId));
    }
}
