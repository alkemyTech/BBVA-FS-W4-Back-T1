package com.magicdogs.alkywall.servicies;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashSet;
import java.util.Set;

@Service
public class AliasService {

    @Value("${wordnik.api.key}")
    private String apiKey;

    private static final String WORDNIK_API_URL = "https://api.wordnik.com/v4/words.json/randomWord";
    private final RestTemplate restTemplate;

    public AliasService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String generateAlias() {
        Set<String> words = new HashSet<>();

        while (words.size() < 3) {
            String word = getRandomWord();
            words.add(word);
        }

        return String.join(".", words);
    }

    private String getRandomWord() {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(WORDNIK_API_URL)
                .queryParam("hasDictionaryDef", true)
                .queryParam("minLength", 6)
                .queryParam("maxLength", 6)
                .queryParam("api_key", apiKey);

        WordnikResponse response = restTemplate.getForObject(uriBuilder.toUriString(), WordnikResponse.class);
        return response.getWord();
    }

    private static class WordnikResponse {
        private String word;

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }
    }
}
