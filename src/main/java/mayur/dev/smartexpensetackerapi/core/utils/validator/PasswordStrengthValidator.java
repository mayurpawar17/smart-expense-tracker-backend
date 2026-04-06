package mayur.dev.smartexpensetackerapi.core.utils.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import me.gosimple.nbvcxz.Nbvcxz;
import me.gosimple.nbvcxz.scoring.Result;


// Example of a custom validator using nbvcxz
//public class PasswordStrengthValidator implements ConstraintValidator<ValidPassword, String> {
//    @Override
//    public boolean isValid(String password, ConstraintValidatorContext context) {
//        Result result = (Result) new Nbvcxz().estimate(password);
//        return result.getBasicScore() >= 3; // 3 = "Strong"
//    }
//}