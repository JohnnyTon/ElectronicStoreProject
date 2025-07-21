package net.electronicstore.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = {"net.electronicstore.common.repository",
    "net.electronicstore.admin.repository"})
@EntityScan(basePackages = "net.electronicstore.common.entity")
@ComponentScan(basePackages = {"net.electronicstore.common", "net.electronicstore.admin"})
@EnableJpaAuditing
@SpringBootApplication
public class ElectronicStoreAdminApplication {

  public static void main(String[] args) {
    SpringApplication.run(ElectronicStoreAdminApplication.class, args);
  }

}
