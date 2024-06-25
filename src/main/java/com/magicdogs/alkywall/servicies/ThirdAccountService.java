package com.magicdogs.alkywall.servicies;

import com.magicdogs.alkywall.config.ModelMapperConfig;
import com.magicdogs.alkywall.dto.AccountDTO;
import com.magicdogs.alkywall.dto.ThirdAccountCreateDTO;
import com.magicdogs.alkywall.dto.ThirdAccountDTO;
import com.magicdogs.alkywall.entities.ThirdAccount;
import com.magicdogs.alkywall.exceptions.ApiException;
import com.magicdogs.alkywall.repositories.AccountRepository;
import com.magicdogs.alkywall.repositories.ThirdAccountRepository;
import com.magicdogs.alkywall.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ThirdAccountService {
    private AccountRepository accountRepository;
    private ThirdAccountRepository thirdAccountRepository;
    private final ModelMapperConfig modelMapperConfig;
    private UserRepository userRepository;

    public String createThirdAccount(ThirdAccountCreateDTO thirdAccountCreateDTO, String userEmail){
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        var destinationAccount = accountRepository.findById(thirdAccountCreateDTO.getIdDestinationAccount())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "CBU no encontrado"));

        var destinationUser = userRepository.findById(destinationAccount.getUser().getIdUser())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario destino no encontrado"));

        if(user == null && destinationAccount == null) throw new ApiException(HttpStatus.NOT_FOUND, "CBU o usuario no encontrado");

        var oldThirdAccount = thirdAccountRepository.findByDestinationAccountAndUser(destinationAccount, user);

        if(oldThirdAccount.isPresent()){ throw new ApiException(HttpStatus.NOT_FOUND, "El CBU ya se encuentra en su agenda"); }

        var newThirdAccount = modelMapperConfig.ThirdAccountDTOToEntity(thirdAccountCreateDTO);

        newThirdAccount.setNickname(thirdAccountCreateDTO.getNickname());
        newThirdAccount.setDestinationAccount(destinationAccount);
        newThirdAccount.setDestinationUser(destinationUser);
        newThirdAccount.setUser(user);

        thirdAccountRepository.save(newThirdAccount);

        return "Se agregó correctamente";
    }

    public String deleteThirdAccount (Long id, String userEmail){

        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        var DestinationAccount = accountRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "CBU no encontrado"));

        var thirdAccount = thirdAccountRepository.findByDestinationAccountAndUser(DestinationAccount, user)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "CBU no encontrado en la agenda"));

        thirdAccountRepository.delete(thirdAccount);

        return "Se eliminó correctamente";
    }

    public List<ThirdAccountDTO> getThirdAccount(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        List<ThirdAccount> thirdAccountList = thirdAccountRepository.findByUser(user);

        return thirdAccountList.stream()
                .map(modelMapperConfig::ThirdAccountToDTO)
                .collect(Collectors.toList());
    }
}
