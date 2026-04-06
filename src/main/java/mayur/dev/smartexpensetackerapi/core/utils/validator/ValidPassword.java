package mayur.dev.smartexpensetackerapi.core.utils.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

//@Documented
//@Constraint(validatedBy = PasswordStrengthValidator.class) // Links to your logic
//@Target({ ElementType.FIELD, ElementType.PARAMETER })
//@Retention(RetentionPolicy.RUNTIME)
//public @interface ValidPassword {
//    String message() default "Password is too weak"; // Default error message
//    Class<?>[] groups() default {};
//    Class<? extends Payload>[] payload() default {};
//}