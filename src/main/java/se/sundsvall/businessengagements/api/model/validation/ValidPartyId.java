package se.sundsvall.businessengagements.api.model.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({
    ElementType.FIELD,
    ElementType.CONSTRUCTOR,
    ElementType.PARAMETER
})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidPartyIdConstraintValidator.class)
public @interface ValidPartyId {
    
    String message() default "partyId is not on a valid format";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
}
