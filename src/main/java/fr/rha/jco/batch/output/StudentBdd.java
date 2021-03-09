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
	private Long id;
	private String firstName;
	private String lastName;
	private String address;
	private LocalDate birth;

	public StudentBdd(StudentCsv csv) {
		this.id = csv.getId();
		this.firstName = csv.getFirstName();
		this.lastName = csv.getLastName();
		this.address = csv.getAddress();
		this.birth = LocalDate.parse(csv.getBirth(), DateTimeFormatter.ISO_LOCAL_DATE);
	}
}
