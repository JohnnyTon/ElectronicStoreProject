package net.electronicstore.admin;

import java.sql.SQLException;
import org.h2.tools.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;


/**
 * H2 database spring boot multi-module sharing
 * https://www.programmersought.com/article/3599934812/
 */
@Configuration
@Slf4j
public class H2DBConfig {

  @Bean(initMethod = "start", destroyMethod = "stop")
  Server inMemoryH2DatabaseaServer() throws SQLException {
    log.debug("Configure TCP for H2 Database");
    return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9090");
  }
}
