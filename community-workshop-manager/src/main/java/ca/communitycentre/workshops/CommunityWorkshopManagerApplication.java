package ca.communitycentre.workshops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class CommunityWorkshopManagerApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(CommunityWorkshopManagerApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(CommunityWorkshopManagerApplication.class, args);
    }
}
