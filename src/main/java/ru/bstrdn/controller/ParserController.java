package ru.bstrdn.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.bstrdn.service.ParserService;

@RestController
@RequestMapping("/parser")
@RequiredArgsConstructor
public class ParserController {

  private final ParserService parserService;

  @PostMapping("/users")
  public ResponseEntity<String> parseUser(@RequestParam("file") MultipartFile file) {
    try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
      List<String> list = br.lines().toList();
      parserService.parseUser(list);
      return ResponseEntity.ok(String.format("File %s successfully parse", file.getName()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @PostMapping("/post")
  public ResponseEntity<String> parsePostForRandomUsers(@RequestParam("file") MultipartFile file) {
    try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
      List<String> list = br.lines().toList();
      parserService.parsePostForRandomUsers(list);
      return ResponseEntity.ok(String.format("File %s successfully parse", file.getName()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
