/*

package dtos.validators.annotation;

import dtos.validators.RoleValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {RoleValidator.class})
public @interface RoleType {

    int limit() default 120;

    String message() default "Age  does not match the required adult limit";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
*/