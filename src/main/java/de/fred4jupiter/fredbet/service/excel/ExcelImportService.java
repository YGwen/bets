package de.fred4jupiter.fredbet.service.excel;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.fred4jupiter.fredbet.data.DataBasePopulator;
import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.AppUserBuilder;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.repository.MatchRepository;
import de.fred4jupiter.fredbet.security.FredBetRole;
import de.fred4jupiter.fredbet.service.UserService;
import de.fred4jupiter.fredbet.util.DateUtils;

@Service
public class ExcelImportService {

	private static final Logger LOG = LoggerFactory.getLogger(ExcelImportService.class);

	@Autowired
	private MatchRepository matchRepository;

	@Autowired
	private UserService userService;
	
	@Autowired
	private DataBasePopulator dataBasePopulator;

	public List<Match> importFromExcel(File file) {
		try (InputStream inp = new FileInputStream(file)) {
			return importFromInputStream(inp);
		} catch (IOException | EncryptedDocumentException | InvalidFormatException e) {
			throw new ExcelReadingException(e.getMessage(), e);
		}
	}

	public List<Match> importFromExcel(byte[] bytes) {
		try (InputStream inp = new ByteArrayInputStream(bytes)) {
			return importFromInputStream(inp);
		} catch (IOException | EncryptedDocumentException | InvalidFormatException e) {
			throw new ExcelReadingException(e.getMessage(), e);
		}
	}

	@Transactional
	public void importMatchesFromExcelAndSave(byte[] bytes) {
		dataBasePopulator.deleteAllBetsAndMatches();
		List<Match> matches = importFromExcel(bytes);
		matchRepository.saveAll(matches);
	}

	private List<AppUser> importUsersFromExcel(byte[] bytes) {
		try (InputStream inp = new ByteArrayInputStream(bytes)) {
			return importFromUserInputStream(inp);
		} catch (IOException | EncryptedDocumentException | InvalidFormatException e) {
			throw new ExcelReadingException(e.getMessage(), e);
		}
	}


	@Transactional
	public void importUsersFrromExcelAndSave(byte[] bytes) {
		List<AppUser> users =importUsersFromExcel(bytes);
		for(AppUser user : users) {
			userService.createUser(user);
		}
	}

	private List<Match> importFromInputStream(InputStream inp) throws IOException, InvalidFormatException {
		final List<Match> matches = new ArrayList<>();

		try (Workbook wb = WorkbookFactory.create(inp)) {
			Sheet sheet = wb.getSheetAt(0);
			for (Row row : sheet) {
				if (row.getRowNum() == 0) {
					continue;
				}
				Match match = convertRowToMatch(row);
				if (match != null) {
					matches.add(match);
				}
			}

			return matches;
		}
	}

	private List<AppUser> importFromUserInputStream(InputStream inp) throws IOException, InvalidFormatException {
		final List<AppUser> users = new ArrayList<>();

		try (Workbook wb = WorkbookFactory.create(inp)) {
			Sheet sheet = wb.getSheetAt(0);
			for (Row row : sheet) {
				if (row.getRowNum() == 0) {
					continue;
				}
				AppUser user = convertRowToUser(row);
				if (user != null) {
					users.add(user);
				}
			}

			return users;
		}
	}


	private AppUser convertRowToUser(Row row) {
		if (row == null || row.getCell(0) == null) {
			return null;
		}

		String username = row.getCell(0).getStringCellValue();
		String password = row.getCell(1).getStringCellValue();
		LOG.info("{}, {}-",username, password);
		AppUser user = AppUserBuilder.create().withUsernameAndPassword(username, password)
				.withRole(FredBetRole.ROLE_USER).build();

		return user;
	}

	private Match convertRowToMatch(Row row) {
		if (row == null || row.getCell(0) == null) {
			return null;
		}

		String country1 = row.getCell(0).getStringCellValue();
		String country2 = row.getCell(1).getStringCellValue();
		String group = row.getCell(2).getStringCellValue();
		Date kickOffDate = HSSFDateUtil.getJavaDate(row.getCell(3).getNumericCellValue());
		String stadium = row.getCell(4).getStringCellValue();

		Match match = new Match();

		if (Country.fromName(country1) != null) {
			match.setCountryOne(Country.fromName(country1));
		} else {
			match.setTeamNameOne(country1);
		}

		if (Country.fromName(country2) != null) {
			match.setCountryTwo(Country.fromName(country2));
		} else {
			match.setTeamNameTwo(country2);
		}

		match.setGroup(Group.valueOf(group));
		match.setKickOffDate(DateUtils.toLocalDateTime(kickOffDate));
		match.setStadium(stadium);
		return match;
	}

}
