package net.electronicstore.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = {
    "net.electronicstore.common.repository",
    "net.electronicstore.client.repository"
})
@EntityScan(basePackages = "net.electronicstore.common.entity")
@ComponentScan(basePackages = {
    "net.electronicstore.common",
    "net.electronicstore.client"
})
@EnableJpaAuditing
@SpringBootApplication
public class ElectronicStoreClientApplication {

  public static void main(String[] args) {
    SpringApplication.run(ElectronicStoreClientApplication.class, args);
  }

}
