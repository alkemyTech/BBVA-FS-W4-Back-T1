package com.magicdogs.alkywall.servicies;

import com.magicdogs.alkywall.config.ModelMapperConfig;
import com.magicdogs.alkywall.dto.AccountDTO;
import com.magicdogs.alkywall.dto.ThirdAccountCreateDTO;
import com.magicdogs.alkywall.dto.ThirdAccountDTO;
import com.magicdogs.alkywall.dto.ThirdAccountUpdateDTO;
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
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cuenta destino no encontrada"));

        var destinationUser = userRepository.findById(destinationAccount.getUser().getIdUser())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario destino no encontrado"));

        if (destinationAccount.getUser().equals(user)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "No puedes agregar una cuenta propia");
        }

        var oldThirdAccount = thirdAccountRepository.findByDestinationAccountAndUser(destinationAccount, user);

        if(oldThirdAccount.isPresent()){ throw new ApiException(HttpStatus.NOT_FOUND, "La cuenta ya se encuentra en su lista de contactos"); }

        var newThirdAccount = modelMapperConfig.ThirdAccountDTOToEntity(thirdAccountCreateDTO);

        newThirdAccount.setNickname(thirdAccountCreateDTO.getNickname());
        newThirdAccount.setDestinationAccount(destinationAccount);
        newThirdAccount.setDestinationUser(destinationUser);
        newThirdAccount.setUser(user);

        thirdAccountRepository.save(newThirdAccount);

        return "Contacto guardado correctamente";
    }

    public String deleteThirdAccount (Long idThirdAccount, String userEmail){
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        var thirdAccount = thirdAccountRepository.findByIdThirdAccountAndUser(idThirdAccount, user)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cuenta no encontrada en la lista de contactos"));

        thirdAccountRepository.delete(thirdAccount);

        return "Contacto eliminado correctamente";
    }

    public List<ThirdAccountDTO> getThirdAccount(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        List<ThirdAccount> thirdAccountList = thirdAccountRepository.findByUser(user);

        return thirdAccountList.stream()
                .map(modelMapperConfig::ThirdAccountToDTO)
                .collect(Collectors.toList());
    }

    public String updateThirdAccount(ThirdAccountUpdateDTO thirdAccountUpdateDTO, String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        var thirdAccount = thirdAccountRepository.findById(thirdAccountUpdateDTO.getIdThirdAccount())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cuenta no encontrada"));

        if (!thirdAccount.getUser().equals(user)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "No tienes permiso para actualizar esta cuenta");
        }

        thirdAccount.setNickname(thirdAccountUpdateDTO.getNickname());
        thirdAccountRepository.save(thirdAccount);

        return "Referencia del contacto actualizada correctamente";
    }
}
