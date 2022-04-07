package com.marella;

import com.marella.appuser.AppUser;
import com.marella.appuser.AppUserRepository;
import com.marella.appuser.AppUserRole;
import com.marella.javaObjectClasses.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.GregorianCalendar;

@SpringBootApplication
public class MarellaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarellaApplication.class, args);
    }



    @Bean
    CommandLineRunner commandLineRunner(AppUserRepository appUserRepository, SpaceRepository spaceRepository) {
        return args -> {
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            AppUser semen = new AppUser(
                    "semen",
                    bCryptPasswordEncoder.encode("semen"),
                    "larkinsemen@gmail.com",
                    AppUserRole.USER
            );

            appUserRepository.save(semen);

//            debug(appUserRepository, spaceRepository);
        };
    }

    private void debug(AppUserRepository appUserRepository, SpaceRepository spaceRepository) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        AppUser user = new AppUser(
                "semen",
                bCryptPasswordEncoder.encode("semen"),
                "larkinsemen@gmail.com",
                AppUserRole.USER
        );
        Space space = new Space("first_space", true, new GregorianCalendar());
        Tag tag = new Tag("treasury");
        Permission permission = new Permission(user, space);
//            space.addTag(tag);
        user.addSpace(space);
        user.addPermission(permission);
        appUserRepository.save(user);
        space.addTag(tag);
        appUserRepository.save(user);
//            При взятии данных из таблицы при работе с объектом все зависимости видны
//            При отключениии FetchType.EAGER на FetchType.LAZY работать перестанет
        appUserRepository.findById(1L).ifPresent(
                u -> {
                    System.out.println(u.getPermissions());
                }
        );
        spaceRepository.findById(1L).ifPresent(
                u -> {
                    System.out.println(u.getPermissions());
                }
        );
//            При работе с начальными объектами для user зависимости есть
//            Для space - нет, т.к. сохранение производилось только в объект user
        System.out.println(user.getPermissions());
        System.out.println(space.getPermissions());


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
    }
}
