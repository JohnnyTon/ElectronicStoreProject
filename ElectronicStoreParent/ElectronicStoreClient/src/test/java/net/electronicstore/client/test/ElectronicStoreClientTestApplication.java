package net.electronicstore.client.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan("net.electronicstore.common.entity")
@EnableJpaRepositories("net.electronicstore.common.repository")
@ComponentScan(basePackages = {
    "net.electronicstore.common",
    "net.electronicstore.client"
})
@SpringBootApplication
public class ElectronicStoreClientTestApplication {

  public static void main(String[] args) {
    SpringApplication.run(ElectronicStoreClientTestApplication.class, args);
  }

}
