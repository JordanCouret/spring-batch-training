package fr.rha.jco.batch.input;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import org.springframework.lang.Nullable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = {"id", "firstName", "lastName"})
public class StudentCsv {
	@NotNull
	private Long id;
	@Pattern(regexp = "^[a-zA-Z\\s]+")
	private String firstName;
	@Pattern(regexp = "^[a-zA-Z\\s]+")
	private String lastName;
	@Nullable
	private String address;
	// Soft solution, see https://www.baeldung.com/java-date-regular-expressions for complex validation of LocalDate
	@Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$")
	private String birth;
}
