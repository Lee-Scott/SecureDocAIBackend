package com.familyFirstSoftware.SecureDocAIBackend;

import com.familyFirstSoftware.SecureDocAIBackend.domain.RequestContext;
import com.familyFirstSoftware.SecureDocAIBackend.entity.RoleEntity;
import com.familyFirstSoftware.SecureDocAIBackend.enumeration.Authority;
import com.familyFirstSoftware.SecureDocAIBackend.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

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
@SpringBootApplication//(exclude = SecurityAutoConfiguration.class)
@EnableJpaAuditing // for the event listener (@EntityListeners) on Auditable witch is extended by all entities
@EnableAsync
public class Application {

	public static void main(String[] args) {

		SpringApplication.run(Application.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(RoleRepository roleRepository) {
		return args -> {
			RequestContext.setUserId(0L);
			// we cannot do this more than once
			/*RequestContext.setUserId(0L);
			var userRole = new RoleEntity();
			userRole.setName(Authority.USER.name());
			userRole.setAuthorities(Authority.USER);

			var adminRole = new RoleEntity();
			adminRole.setName(Authority.ADMIN.name());
			adminRole.setAuthorities(Authority.ADMIN);

			roleRepository.save(userRole);
			RequestContext.start();*/
		};

	}

}