package sg.edu.nus.iss.wkshp6.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import sg.edu.nus.iss.wkshp6.repo.ListRepo;
import sg.edu.nus.iss.wkshp6.repo.ValueRepo;

@Service
public class GameService {
    @Autowired
    ListRepo listRepo;

    @Autowired
    ValueRepo valueRepo;

    public void saveGame(String key, Object game) {
        valueRepo.setKey(key, game);
    }

    public String getGame(String key) {
        return valueRepo.getKey(key).toString();
    }
    public boolean gameExists(String key) {
        return valueRepo.hasKey(key);
    }
}
