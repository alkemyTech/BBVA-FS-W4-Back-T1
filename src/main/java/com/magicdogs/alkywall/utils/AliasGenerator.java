package com.magicdogs.alkywall.utils;

import com.magicdogs.alkywall.repositories.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

@Component
@AllArgsConstructor
public class AliasGenerator {

    private final AccountRepository accountRepository;
    private final Random random = new Random();
    private static final List<String> ANIMALS = List.of(
            "perro", "gato", "caballo", "vaca", "cerdo", "oveja", "pollo", "pato",
            "tigre", "leopardo", "elefante", "jirafa", "mono", "zorro", "lobo", "serpiente",
            "oso", "ardilla", "burro", "camello", "ciervo", "cocodrilo", "foca", "gallina",
            "golondrina", "gorila", "iguana", "lagarto", "langosta", "orca", "panda",
            "rinoceronte", "saltamontes", "tortuga", "cabra", "nutria", "canguro",
            "mapache", "liebre", "jaguar", "avestruz", "pulpo", "abeja", "avispa", "buey",
            "pavo", "puma", "pez", "lince", "ballena", "guepardo", "armadillo", "cebra",
            "alpaca", "marmota", "suricata", "carpincho", "ganso", "rata", "conejo"
    );

    public String generateUniqueAlias(String fullName, String fullLastName) {
        String firstName = extractFirstName(fullName);
        String lastName = extractFirstLastName(fullLastName);
        String alias;

        do {
            String animal = getRandomAnimal();
            alias = String.format("%s.%s.%s", removeDiacritics(firstName.toLowerCase()),
                    removeDiacritics(lastName.toLowerCase()), animal.toLowerCase());
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

    private String removeDiacritics(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("");
    }
}
