package dtostorage.util.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = AfterTwoHoursValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterTwoHours {
    String message() default "Дата и время должны быть как минимум на 2 часа позже текущего времени";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
