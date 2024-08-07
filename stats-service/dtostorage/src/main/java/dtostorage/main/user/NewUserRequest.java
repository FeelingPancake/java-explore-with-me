package dtostorage.main.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Jacksonized
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequest {
    @NotBlank
    @Email
    @Size(min = 6, max = 254)
    String email;
    @NotNull
    @NotBlank
    @Size(min = 2, max = 250)
    String name;
}
