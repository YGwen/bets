package de.fred4jupiter.fredbet.repository;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.fred4jupiter.fredbet.AbstractTransactionalIntegrationTest;
import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.AppUserBuilder;
import de.fred4jupiter.fredbet.security.FredBetRole;

public class AppUserRepositoryIT extends AbstractTransactionalIntegrationTest {

	@Autowired
	private AppUserRepository appUserRepository;

	@Test
	public void saveAppUser() {
		AppUser appUser = AppUserBuilder.create().withDemoData().build();
		appUser = appUserRepository.save(appUser);
		assertNotNull(appUser.getId());

		AppUser foundAppUser = appUserRepository.getOne(appUser.getId());
		assertNotNull(foundAppUser);
		assertEquals(appUser.getUsername(), foundAppUser.getUsername());
	}

	@Test
	public void appUserCanHaveMultipleRoles() {
		AppUser appUser = AppUserBuilder.create().withDemoData().withRole(FredBetRole.ROLE_USER).withRole(FredBetRole.ROLE_ADMIN).build();
		appUser = appUserRepository.save(appUser);
		appUserRepository.flush();
		assertNotNull(appUser.getId());

		AppUser foundAppUser = appUserRepository.getOne(appUser.getId());
		assertNotNull(foundAppUser);
		assertEquals(appUser.getUsername(), foundAppUser.getUsername());
		assertThat(appUser.getRoles().size(), equalTo(2));
	}

}
