package com.magicdogs.alkywall.servicies;

import com.magicdogs.alkywall.config.ModelMapperConfig;
import com.magicdogs.alkywall.dto.ThirdAccountDTO;
import com.magicdogs.alkywall.entities.ThirdAccount;
import com.magicdogs.alkywall.exceptions.ApiException;
import com.magicdogs.alkywall.repositories.AccountRepository;
import com.magicdogs.alkywall.repositories.ThirdAccountRepository;
import com.magicdogs.alkywall.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ThirdAccountService {
    private AccountRepository accountRepository;
    private ThirdAccountRepository thirdAccountRepository;
    private final ModelMapperConfig modelMapperConfig;
    private UserRepository userRepository;

    public String creatThirdAccount (ThirdAccountDTO thirdAccountDTO, String userEmail){
        var userOrigin = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        var account = accountRepository.findByCbu(thirdAccountDTO.getCbu())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "CBU no encontrado"));
        if(userOrigin == null && account == null) throw new ApiException(HttpStatus.NOT_FOUND, "CBU o usuario no encontrado");
        var oldThirdAccount = thirdAccountRepository.findByCBUAndUser(thirdAccountDTO.getCbu(), userOrigin);
        if(!oldThirdAccount.get().isEmpty()){ throw new ApiException(HttpStatus.NOT_FOUND, "El CBU ya fue guardado"); }
                //.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "El CBU ya fue guardado"));

        var newThirdAccount = modelMapperConfig.ThirdAccountDTOToEntitie(thirdAccountDTO);
        newThirdAccount.setUser(userOrigin);
        thirdAccountRepository.save(newThirdAccount);

        return "Se pudo agregar correctamente";
    }

    public String deleteThirdAccount (String cbu, String userEmail){

        var userOrigin = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        var thirdAccount = thirdAccountRepository.findByCBUAndUser(cbu, userOrigin).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "CBU no encontrado"));
        ThirdAccount thirdAccount2 = null;
        for(ThirdAccount t: thirdAccount){
            if(t.getUser().getIdUser().equals(userOrigin.getIdUser())){
                thirdAccount2 = t;
            }
        }
        /*Optional<ThirdAccount> thirdAccount2 = thirdAccount.stream()
                .filter(t -> t.getUser().getIdUser().equals(userOrigin.getIdUser()))
                .findFirst();*/

        if (thirdAccount2 == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "El CBU no lo tiene el usuario logeado");
        }

        thirdAccountRepository.delete(thirdAccount2);

        return "Se pudo eliminar correctamente";
    }
}
