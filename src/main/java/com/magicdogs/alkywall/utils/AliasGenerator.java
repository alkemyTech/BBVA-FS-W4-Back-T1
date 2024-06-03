package com.magicdogs.alkywall.utils;

import com.magicdogs.alkywall.repositories.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
@AllArgsConstructor
public class AliasGenerator {

    private final AccountRepository accountRepository;
    private final Random random = new Random();
    private static final List<String> ANIMALS = List.of(
            "perro", "gato", "caballo", "vaca", "cerdo", "oveja", "pollo", "pato", "rata", "conejo",
            "tigre", "leopardo", "elefante", "jirafa", "mono", "zorro", "lobo", "serpiente",
            "oso", "ardilla", "burro", "camello", "ciervo", "cocodrilo", "foca", "gallina", "ganso",
            "golondrina", "gorila", "iguana", "lagarto", "langosta", "orca", "panda",
            "rinoceronte", "saltamontes", "tortuga"
    );

    public String generateUniqueAlias(String fullName, String fullLastName) {
        String firstName = extractFirstName(fullName);
        String lastName = extractFirstLastName(fullLastName);
        String alias;

        do {
            String animal = getRandomAnimal();
            alias = String.format("%s.%s.%s", firstName.toLowerCase(), lastName.toLowerCase(), animal.toLowerCase());
        } while (accountRepository.existsByAlias(alias));

        return alias;
    }

    private String extractFirstName(String fullName) {
        String[] names = fullName.split("\\s+");
        return names[0];
    }

    private String extractFirstLastName(String fullLastName) {
        String[] lastNames = fullLastName.split("\\s+");
        return lastNames[0];
    }

    private String getRandomAnimal() {
        return ANIMALS.get(random.nextInt(ANIMALS.size()));
    }
}
