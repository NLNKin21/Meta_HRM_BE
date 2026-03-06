package com.metahrms.employee_management;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
@Slf4j
public class EmployeeManagementApplication {

    public static void main(String[] args) throws UnknownHostException {
        SpringApplication app = new SpringApplication(EmployeeManagementApplication.class);
        Environment env = app.run(args).getEnvironment();
        
        String protocol = "http";
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        String serverPort = env.getProperty("server.port", "8080");
        String contextPath = env.getProperty("server.servlet.context-path", "");
        
        log.info("""
            
            ----------------------------------------------------------
            \tApplication '{}' is running! Access URLs:
            \tLocal: \t\t{}://localhost:{}{}
            \tExternal: \t{}://{}:{}{}
            \tSwagger UI: \t{}://localhost:{}{}/swagger-ui.html
            \tProfile(s): \t{}
            ----------------------------------------------------------
            """,
            env.getProperty("spring.application.name"),
            protocol, serverPort, contextPath,
            protocol, hostAddress, serverPort, contextPath,
            protocol, serverPort, contextPath,
            env.getActiveProfiles().length == 0 ? "default" : String.join(", ", env.getActiveProfiles())
        );
    }
}