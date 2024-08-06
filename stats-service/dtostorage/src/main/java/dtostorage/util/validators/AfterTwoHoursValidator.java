package dtostorage.util.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class AfterTwoHoursValidator implements ConstraintValidator<AfterTwoHours, LocalDateTime> {

    @Override
    public void initialize(AfterTwoHours constraintAnnotation) {
    }

    @Override
    public boolean isValid(LocalDateTime eventDate, ConstraintValidatorContext context) {
        if (eventDate == null) {
            return false;
        }
        LocalDateTime nowPlusTwoHours = LocalDateTime.now().plusHours(2).plusMinutes(1);
        return eventDate.isAfter(nowPlusTwoHours);
    }
}
