package com.familyFirstSoftware.SecureDocAIBackend;

import com.familyFirstSoftware.SecureDocAIBackend.domain.RequestContext;
import com.familyFirstSoftware.SecureDocAIBackend.entity.RoleEntity;
import com.familyFirstSoftware.SecureDocAIBackend.enumeration.Authority;
import com.familyFirstSoftware.SecureDocAIBackend.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Collections;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 12/14/2024
 *
 * Todo: exception handling refactor into its own folder and class
 * Todo: validation on the password and reset password
 * Todo: create SUPER_ADMIN and MANAGER roles bellow or manually in the database
 */
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication//(exclude = SecurityAutoConfiguration.class)
@EnableJpaAuditing // for the event listener (@EntityListeners) on Auditable witch is extended by all entities
@EnableAsync
@ComponentScan(basePackages = {"com.familyFirstSoftware.SecureDocAIBackend"})
public class Application {

	public static void main(String[] args) {
		io.github.cdimascio.dotenv.Dotenv dotenv = io.github.cdimascio.dotenv.Dotenv.configure().directory("R:/Playspace/SecureDocAIBackend").load();
		System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
		System.setProperty("gemini.project.id", dotenv.get("gemini.project.id"));
		System.setProperty("gemini.location", dotenv.get("gemini.location"));

		SpringApplication app = new SpringApplication(Application.class);
		app.setDefaultProperties(Collections.singletonMap("spring.profiles.active", "dev"));
		app.run(args);
	}

	// Run once at startup to create default roles then never again

	/*@Bean
    CommandLineRunner commandLineRunner(RoleRepository roleRepository) {
		return args -> {
			System.out.println(">>> Running CommandLineRunner...");

			// Set user context
			RequestContext.setUserId(0L);

			// Only create roles if they don't exist
			if (roleRepository.count() == 0) {
				var userRole = new RoleEntity();
				userRole.setName("USER");
				userRole.setAuthorities(Authority.USER);
				System.out.println("Saving role: " + userRole.getName());

				var adminRole = new RoleEntity();
				adminRole.setName("ADMIN");
				adminRole.setAuthorities(Authority.ADMIN);
				System.out.println("Saving role: " + adminRole.getName());

				var superAdminRole = new RoleEntity();
				superAdminRole.setName("SUPER_ADMIN");
				superAdminRole.setAuthorities(Authority.SUPER_ADMIN);
				System.out.println("Saving role: " + superAdminRole.getName());

				var managerRole = new RoleEntity();
				managerRole.setName("MANAGER");
				managerRole.setAuthorities(Authority.MANAGER);
				System.out.println("Saving role: " + managerRole.getName());

				var aiAgentRole = new RoleEntity();
				aiAgentRole.setName("AI_AGENT");
				aiAgentRole.setAuthorities(Authority.AI_AGENT);
				System.out.println("Saving role: " + aiAgentRole.getName());

				roleRepository.save(userRole);
				roleRepository.save(adminRole);
				roleRepository.save(superAdminRole);
				roleRepository.save(managerRole);
				roleRepository.save(aiAgentRole);

				System.out.println(">>> Finished creating roles.");
			} else {
				System.out.println(">>> Roles already exist, skipping creation.");
			}

			RequestContext.start();
		};
	}*/

}