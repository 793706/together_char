package com.manxuan.version3.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

public class yamlUtil {

  static Path path = FileSystems.getDefault().getPath("src/main/resources/application.yml");

  static Map<String, Map<String,String>> yamlMap = new HashMap<String, Map<String,String>>();

  private Map parseYaml() {

    Yaml yaml = new Yaml();
    try {
      return yaml.load(new FileInputStream((path.toFile())));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }

  public yamlUtil() {
    yamlMap = parseYaml();
  }
}
