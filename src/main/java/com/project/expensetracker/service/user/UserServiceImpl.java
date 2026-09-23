package com.project.expensetracker.service.user;

import com.project.expensetracker.dto.OnboardingRequestDto;
import com.project.expensetracker.dto.RegisterRequestDto;
import com.project.expensetracker.entity.Account;
import com.project.expensetracker.entity.Bank;
import com.project.expensetracker.entity.Card;
import com.project.expensetracker.entity.PaymentMode;
import com.project.expensetracker.entity.User;
import com.project.expensetracker.entity.UserConfig;
import com.project.expensetracker.exception.UserAlreadyExistsException;
import com.project.expensetracker.exception.UserNotFoundException;
import com.project.expensetracker.repo.AccountRepo;
import com.project.expensetracker.repo.BankRepo;
import com.project.expensetracker.repo.CardRepo;
import com.project.expensetracker.repo.PaymentModeRepo;
import com.project.expensetracker.repo.UserConfigRepo;
import com.project.expensetracker.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final BankRepo bankRepo;
    private final AccountRepo accountRepo;
    private final CardRepo cardRepo;
    private final PaymentModeRepo paymentModeRepo;
    private final UserConfigRepo userConfigRepo;

    @Override
    public void registerUser(RegisterRequestDto request) {
        final var exists = userRepo.existsByEmail(request.email());
        if(exists){
            throw new UserAlreadyExistsException(request.email());
        }
        User user = User.builder()
                .name(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();
        userRepo.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final var user = userRepo.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password(user.getPassword())
                .authorities("USER")
                .build();
    }
}
