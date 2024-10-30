/*

package dtos.validators;

import dtos.validators.annotation.RoleType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RoleValidator implements ConstraintValidator<RoleType, String>{

    private int ageLimit;

    @Override
    public void initialize(AgeLimit constraintAnnotation) {
        this.ageLimit = constraintAnnotation.limit();
    }

    @Override
    public boolean isValid(Integer inputAge, ConstraintValidatorContext constraintValidatorContext) {
        return inputAge > ageLimit;
    }

}
*/