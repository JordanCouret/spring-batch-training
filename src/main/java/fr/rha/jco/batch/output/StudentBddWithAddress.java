package fr.rha.jco.batch.output;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import fr.rha.jco.batch.input.StudentCsv;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class StudentBddWithAddress extends StudentBdd{
	private Long addressId;

	public StudentBddWithAddress(StudentCsv csv, Long addressId) {
		this.id = csv.getId();
		this.firstName = csv.getFirstName();
		this.lastName = csv.getLastName();
		this.address = csv.getAddress();
		this.birth = LocalDate.parse(csv.getBirth(), DateTimeFormatter.ISO_LOCAL_DATE);
		this.addressId = addressId;
	}
}
