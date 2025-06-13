package org.sikawofie.projecttracker.principal;

import lombok.RequiredArgsConstructor;
import org.sikawofie.projecttracker.entity.Role;
import org.sikawofie.projecttracker.entity.User;
import org.sikawofie.projecttracker.repository.RoleRepo;
import org.sikawofie.projecttracker.repository.UserRepo;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepo userRepository;
    private final RoleRepo roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        if (email == null) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        Optional<User> existingUserOpt = userRepository.findByEmail(email);
        if (existingUserOpt.isEmpty()) {
            User newUser = new User();
            newUser.setUsername(email);
            newUser.setEmail(email);
            newUser.setOauthUser(true);
            newUser.setPassword("oauth-user");
            newUser.setRoles(Set.of(getContractorRole()));
            userRepository.save(newUser);
        }

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_CONTRACTOR")),
                oAuth2User.getAttributes(),
                "email"
        );
    }

    private Role getContractorRole() {
        return roleRepository.findByName(Role.RoleName.ROLE_CONTRACTOR)
                .orElseThrow(() -> new RuntimeException("ROLE_CONTRACTOR not found"));
    }
}
