package se.sundsvall.businessengagements.api.model.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import javax.validation.ConstraintValidatorContext;

import static org.assertj.core.api.Assertions.assertThat;

class ValidPartyIdConstraintValidatorTest {
    
    ValidPartyIdConstraintValidator validator;
    
    private final String validPartyId = "338721fc-c8e9-4ed3-8220-fe1d96cf33fc";
    private final String notValidPartyId = "asdf21fc-c8e9-4ed3-8220-fe1d96cf33";
    
    @Mock
    private ConstraintValidatorContext constraintValidatorContextMock;
    
    @BeforeEach
    public void setup() {
        validator = new ValidPartyIdConstraintValidator();
    }
    
    @Test
    void test() {
        //Just basic tests, assume the regex is well thought out ;)
        assertThat(validator.isValid(null, constraintValidatorContextMock)).isFalse();
        assertThat(validator.isValid(validPartyId, constraintValidatorContextMock)).isTrue();
        assertThat(validator.isValid(notValidPartyId, constraintValidatorContextMock)).isFalse();
    }
}