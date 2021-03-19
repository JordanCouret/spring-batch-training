package fr.rha.jco.batch.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(of = { "id", "firstName", "lastName" })
public class StudentCsv {
	private Long id;

	private String firstName;

	private String lastName;

	private String address;

	private String birth;
}
