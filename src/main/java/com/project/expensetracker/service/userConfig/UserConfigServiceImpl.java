package com.project.expensetracker.service.userConfig;

import com.project.expensetracker.dto.UserConfigDto;
import com.project.expensetracker.entity.PaymentMode;
import com.project.expensetracker.entity.UserConfig;
import com.project.expensetracker.enums.LanguagePreference;
import com.project.expensetracker.exception.UserConfigNotFoundException;
import com.project.expensetracker.repo.UserConfigRepo;
import com.project.expensetracker.repo.AccountRepo;
import com.project.expensetracker.exception.AccountNotOwnedByUserException;
import com.project.expensetracker.service.paymentMode.PaymentModeService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserConfigServiceImpl implements UserConfigService {

    private final UserConfigRepo userConfigRepo;
    private final PaymentModeService paymentModeService;
    private final AccountRepo accountRepo;


    @Override
    public UserConfig getByUserId(String userId) {
        return userConfigRepo.findByEmail(userId)
                .orElseThrow(() -> new UserConfigNotFoundException(userId));
    }

    @Override
    public UserConfigDto getConfig(String userId) {
        UserConfig userConfig = getByUserId(userId);

        String language = mapLanguageToString(userConfig);
        Long defaultPaymentModeId = userConfig.getPaymentMode() != null ? userConfig.getPaymentMode().getId() : null;
        Long defaultAccountId = userConfig.getDefaultAccount() != null ? userConfig.getDefaultAccount().getId() : null;
        return new UserConfigDto(language, defaultPaymentModeId, defaultAccountId);
    }

    private static String mapLanguageToString(UserConfig userConfig) {
        return userConfig.getLanguagePreference() != null ? userConfig.getLanguagePreference().toString() : "English";
    }

    @Override
    public UserConfigDto updateConfig(UserConfigDto userConfigDto, String userId) {
        UserConfig config = getByUserId(userId);

        if(config.getLanguagePreference() != null){
            config.setLanguagePreference(LanguagePreference.valueOf(userConfigDto.language().toUpperCase()));
        }

        if(config.getPaymentMode() != null){
            PaymentMode paymentMode = paymentModeService.get(userConfigDto.paymentModeId());
            config.setPaymentMode(paymentMode);
        }

        if (userConfigDto.defaultAccountId() != null) {
            var account = accountRepo.findByIdAndUserId(userConfigDto.defaultAccountId(), userId);
            if (account == null || !account.isActive()) {
                throw new AccountNotOwnedByUserException(
                        java.util.List.of(userConfigDto.defaultAccountId()), userId);
            }
            config.setDefaultAccount(account);
        }

        UserConfig updatedConfig = userConfigRepo.save(config);

        String language = mapLanguageToString(updatedConfig);
        Long defaultPaymentModeId = updatedConfig.getPaymentMode() != null ? updatedConfig.getPaymentMode().getId() : null;
        Long defaultAccountId = updatedConfig.getDefaultAccount() != null ? updatedConfig.getDefaultAccount().getId() : null;
        return new UserConfigDto(language, defaultPaymentModeId, defaultAccountId);
    }


}


