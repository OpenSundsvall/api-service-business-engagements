package se.sundsvall.businessengagements.api.model.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.UUID;

public class ValidPartyIdConstraintValidator implements ConstraintValidator<ValidPartyId, Object> {
    
    @Override
    public boolean isValid(final Object partyId, final ConstraintValidatorContext constraintValidatorContext) {
        try {
            //We only check that the parsing is ok, not the return value.
            UUID.fromString(String.valueOf(partyId));
        } catch (Exception e) {
            return false;
        }
        
        return true;
    }
}
