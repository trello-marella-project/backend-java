package com.marella;

import com.marella.models.*;
import com.marella.repositories.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.marella.models.ERole.*;

@SpringBootApplication
public class MarellaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarellaApplication.class, args);
    }

//    @Bean
//    CommandLineRunner commandLineRunner(RoleRepository roleRepository) {
//        return args -> {
//            Role role_admin = new Role(ROLE_ADMIN);
//            Role role_user = new Role(ROLE_USER);
//            Role role_moderator = new Role(ROLE_MODERATOR);
//            Set<Role> roles = Stream.of(role_admin, role_user, role_moderator).collect(Collectors.toSet());
//
//            roleRepository.saveAll(roles);

//            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
//            User semen = new User(
//                    "semen",
//                    bCryptPasswordEncoder.encode("semen"),
//                    "larkinsemen@gmail.com",
//                    AppUserRole.ROLE_USER
//            );
//
//            appUserRepository.save(semen);

//            debug(appUserRepository, spaceRepository);
//        };
//    }

//    private void debug(AppUserRepository appUserRepository, SpaceRepository spaceRepository) {
//        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
//        User user = new User(
//                "semen",
//                bCryptPasswordEncoder.encode("semen"),
//                "larkinsemen@gmail.com",
//                AppUserRole.ROLE_USER
//        );
//        Space space = new Space("first_space", true);
//        Tag tag = new Tag("treasury");
//        Permission permission = new Permission(user, space);
////            space.addTag(tag);
//        user.addSpace(space);
//        user.addPermission(permission);
//        appUserRepository.save(user);
//        space.addTag(tag);
//        appUserRepository.save(user);
////            При взятии данных из таблицы при работе с объектом все зависимости видны
////            При отключениии FetchType.EAGER на FetchType.LAZY работать перестанет
//        appUserRepository.findById(1L).ifPresent(
//                u -> {
//                    System.out.println(u.getPermissions());
//                }
//        );
//        spaceRepository.findById(1L).ifPresent(
//                u -> {
//                    System.out.println(u.getPermissions());
//                }
//        );
////            При работе с начальными объектами для user зависимости есть
////            Для space - нет, т.к. сохранение производилось только в объект user
//        System.out.println(user.getPermissions());
//        System.out.println(space.getPermissions());


//            userRepository.delete(user);
//            userRepository.save(user);

//            userRepository.deleteById(1L);
//            userRepository.save(new SiteUser(
//                    "user",
//                    "user",
//                    "user@gmail.com",
//                    true,
//                    false,
//                    "user.ru"
//            ));
//    }
}
