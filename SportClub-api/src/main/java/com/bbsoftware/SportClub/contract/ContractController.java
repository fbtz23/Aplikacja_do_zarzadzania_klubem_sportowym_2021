package com.bbsoftware.SportClub.contract;

import com.bbsoftware.SportClub.announcement.*;
import com.bbsoftware.SportClub.appuser.AppUser;
import com.bbsoftware.SportClub.appuser.AppUserRepository;
import com.bbsoftware.SportClub.exceptions.AnnouncementNotFoundException;
import com.bbsoftware.SportClub.exceptions.AppUserNotFoundException;

import com.bbsoftware.SportClub.exceptions.ContractNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@AllArgsConstructor
public class ContractController {
    private final ContractRepository contractRepository;
    private final ContractModelAssembler assembler;
    private final AppUserRepository appUserRepository;

    @GetMapping("/contract/{id}")
    public EntityModel<Contract> one(@PathVariable Long id) {
        Contract contract = contractRepository.findById(id).orElseThrow(() -> new ContractNotFoundException(id));
        return assembler.toModel(contract);
    }

    @GetMapping("/mycontract")
    public EntityModel<Contract> getMyContract() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof AppUser) {
                Long id = ((AppUser) principal).getId();
                Contract contract = contractRepository.findByUser_id(id)
                        .orElseThrow(() -> new ContractNotFoundException(id));
                return assembler.toModel(contract);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Contract not found");
        }
    }

    @GetMapping("/contract/user/{id}")
    public ResponseEntity<EntityModel<Contract>> getContract(@PathVariable Long id) {
        AppUser appUser = appUserRepository.findById(id).orElseThrow(() -> new AppUserNotFoundException(id));
        Optional<Contract> contract = contractRepository.findByUser_id(appUser.getId());
        if (!contract.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Contract contract = contractRepository.findByUser_id(appUser.getId()).orElse
        return new ResponseEntity<>(assembler.toModel(contract.get()), HttpStatus.OK);

    }

    @PostMapping("/contract")
    ResponseEntity<EntityModel<Contract>> newContract(@RequestBody ContractRequest contractRequest) {

        Contract contract = new Contract();
        contract.setMoney(contractRequest.getMoney());
        contract.setStart_date(contractRequest.getStart_date());
        contract.setEnd_date(contractRequest.getEnd_date());
        AppUser appUser = appUserRepository.findById(contractRequest.getUser_id())
                .orElseThrow(() -> new AppUserNotFoundException(contractRequest.getUser_id()));
        contract.setUser(appUser);
        contractRepository.save(contract);
        return ResponseEntity //
                .created(linkTo(methodOn(ContractController.class).one(contract.getId())).toUri()) //
                .body(assembler.toModel(contract));
    }

    @PutMapping("/contract/{id}")
    ResponseEntity<?> updateContract(@RequestBody Contract newContract, @PathVariable Long id) {

        Contract updatedEmployee = contractRepository.findById(id) //
                .map(contract -> {
                    contract.setMoney(newContract.getMoney());
                    contract.setEnd_date(newContract.getEnd_date());
                    contract.setStart_date(newContract.getStart_date());
                    return contractRepository.save(contract);
                }) //
                .orElseGet(() -> {
                    newContract.setId(id);
                    return contractRepository.save(newContract);
                });

        EntityModel<Contract> entityModel = assembler.toModel(updatedEmployee);
        return ResponseEntity //
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                .body(entityModel);
    }

    @PatchMapping("/contract/user/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ContractUpdateRequest contractRequest) {

        AppUser appUser = appUserRepository.findById(id).orElseThrow(() -> new AppUserNotFoundException(id));
        Contract contract = appUser.getContract();

        if (contract == null)
            contract = new Contract();

        contract.setMoney(contractRequest.getMoney());
        contract.setStart_date(contractRequest.getStart_date());
        contract.setEnd_date(contractRequest.getEnd_date());
        contract.setUser(appUser);

        contractRepository.save(contract);
        return ResponseEntity //
                .created(linkTo(methodOn(ContractController.class).one(contract.getId())).toUri()) //
                .body(assembler.toModel(contract));
    }
}
// ADZKS-122