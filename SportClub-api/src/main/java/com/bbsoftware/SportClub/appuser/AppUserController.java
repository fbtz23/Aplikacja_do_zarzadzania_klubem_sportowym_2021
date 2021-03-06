package com.bbsoftware.SportClub.appuser;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.bbsoftware.SportClub.exceptions.AppUserNotFoundException;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import lombok.AllArgsConstructor;
import org.springframework.web.server.ResponseStatusException;

@RestController
@AllArgsConstructor
public class AppUserController {

    private final AppUserRepository appUserRepository;
    private final AppUserModelAssembler appUserModelAssembler;
    private final AppUserService appUserService;

    @GetMapping("/appUsers")
    public CollectionModel<EntityModel<AppUser>> all() {

        List<EntityModel<AppUser>> appUsers = appUserRepository.findAll().stream() //
                .map(appUserModelAssembler::toModel) //
                .collect(Collectors.toList());

        return CollectionModel.of(appUsers, //
                linkTo(methodOn(AppUserController.class).all()).withSelfRel());
    }

    @GetMapping("/appUsers/{id}")
    public EntityModel<AppUser> one(@PathVariable Long id) {

        AppUser appUser = appUserRepository.findById(id) //
                .orElseThrow(() -> new AppUserNotFoundException(id));

        return appUserModelAssembler.toModel(appUser);
    }

    @GetMapping("/appUsers/players")
    public CollectionModel<EntityModel<AppUser>> players() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof AppUser) {

            Long id = ((AppUser) principal).getId();

            AppUser appUser = appUserRepository.findById(id) //
                    .orElseThrow(() -> new AppUserNotFoundException(id));

            List<EntityModel<AppUser>> players = appUser.getPlayers().stream()
                    .map(appUserModelAssembler::toModel)
                    .collect(Collectors.toList());

            return CollectionModel.of(players, //
                    linkTo(methodOn(AppUserController.class).all()).withSelfRel());
        }else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }


    }

    @PatchMapping("/appUsers/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Map<Object, Object> updates) {

        AppUser appUser = appUserRepository.findById(id) //
                .orElseThrow(() -> new AppUserNotFoundException(id));

        updates.forEach((k, v) -> {
            // use reflection to get field k on manager and set it to value v
            if (k == "coach") {
                appUserService.setCoachId(Long.parseLong((String) v), id);
                return;
            }
            Field field = ReflectionUtils.findField(AppUser.class, (String) k);
            field.setAccessible(true);
            if (field.getType().isEnum()) {
                ReflectionUtils.setField(field, appUser, Enum.valueOf((Class<Enum>) field.getType(), (String) v));
            } else {
                ReflectionUtils.setField(field, appUser, v);
            }
        });

        AppUser updatedAppUser = appUserRepository.save(appUser);
        return ResponseEntity //
                .created(linkTo(methodOn(AppUserController.class).one(updatedAppUser.getId())).toUri()) //
                .body(appUserModelAssembler.toModel(updatedAppUser));
    }

}
