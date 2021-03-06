package fr.rha.jco.batch.output;

import fr.rha.jco.batch.input.StudentCsv;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor
@Data
@ToString(of = {"id", "firstName", "lastName"})
public class StudentBdd {
	protected Long id;
	protected String firstName;
	protected String lastName;
	protected String address;
	protected LocalDate birth;

	public StudentBdd(StudentCsv csv) {
		this.id = csv.getId();
		this.firstName = csv.getFirstName();
		this.lastName = csv.getLastName();
		this.address = csv.getAddress();
		this.birth = LocalDate.parse(csv.getBirth(), DateTimeFormatter.ISO_LOCAL_DATE);
	}
}
