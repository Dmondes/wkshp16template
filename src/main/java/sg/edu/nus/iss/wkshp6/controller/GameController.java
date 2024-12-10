package sg.edu.nus.iss.wkshp6.controller;

import java.io.StringReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonArray;
import jakarta.json.JsonValue;
import sg.edu.nus.iss.wkshp6.service.GameService;

@RestController
@RequestMapping("/api")
public class GameController {

    @Autowired
    GameService gameService;

    @PostMapping("/boardgame")
    public ResponseEntity<String> createGames(@RequestBody String payload) {
        try {
            // Convert the payload string into a JsonReader
            JsonReader reader = Json.createReader(new StringReader(payload));
            JsonArray games = reader.readArray(); // Read as an array

            // Iterate over each game in the array
            for (JsonValue gameValue : games) {
                JsonObject game = gameValue.asJsonObject();

                // Get the gid to use as part of the Redis key
                String gid = String.valueOf(game.getInt("gid"));
                String redisKey = "boardgame:" + gid;

                // Store in Redis: key = "boardgame:<gid>", value = {complete game JSON}
                gameService.saveGame(redisKey, game.toString());
            }

            // Create response JSON
            JsonObject response = Json.createObjectBuilder()
                    .add("insert_count", games.size())
                    .build();

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response.toString());

        } catch (Exception ex) {
            JsonObject error = Json.createObjectBuilder()
                    .add("error", ex.getMessage())
                    .build();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error.toString());
        }
    }

    @GetMapping("/boardgame/{id}")
    public ResponseEntity<String> getGame(@PathVariable String id) {
        try {
            // Construct the Redis key
            String redisKey = "boardgame:" + id;

            // Check if game exists first
            if (!gameService.gameExists(redisKey)) {
                // Game not found - return 404
                JsonObject error = Json.createObjectBuilder()
                        .add("error", "Game with id " + id + " not found")
                        .build();
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(error.toString());
            }

            // Game exists - retrieve it
            Object game = gameService.getGame(redisKey);
            return ResponseEntity.ok(game.toString());

        } catch (Exception ex) {
            JsonObject error = Json.createObjectBuilder()
                    .add("error", ex.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error.toString());
        }
    }

    @PutMapping("boardgame/{id}")
    public ResponseEntity<String> updateGame(@PathVariable String id, @RequestBody String payload,
            @RequestParam(defaultValue = "false") boolean upsert) {
        String redisKey = "boardgame:" + id;
        try {
            if (!gameService.gameExists(redisKey)) {
                if (upsert == true) {
                    gameService.saveGame(redisKey, payload);
                    return ResponseEntity.status(HttpStatus.CREATED).body(payload);
                }
                JsonObject error = Json.createObjectBuilder()
                        .add("error", "Game with id " + id + " not found")
                        .build();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error.toString());
            }
            JsonReader reader = Json.createReader(new StringReader(payload));
            JsonObject game = reader.readObject();
            gameService.saveGame(redisKey, game.toString());
            JsonObject response = Json.createObjectBuilder()
                    .add("update_count", 1)
                    .add("id", redisKey)
                    .build();
            return ResponseEntity.ok(response.toString());

        } catch (Exception ex) {
            JsonObject error = Json.createObjectBuilder()
                    .add("error", ex.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error.toString());
        }

    }

}

/*
 * return ResponseEntity.ok(data); 200 OK
 * return ResponseEntity.status(HttpStatus.CREATED).body(response); 201 CREATED
 * return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); 400 BAD
 * REQUEST
 * return
 * ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); 500
 * INTERNAL SERVER ERROR
 */