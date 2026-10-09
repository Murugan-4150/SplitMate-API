package com.splitmate.api.validation;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {

    private static final PhoneNumberUtil PHONE_UTIL = PhoneNumberUtil.getInstance();

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }

        try {
            Phonenumber.PhoneNumber phoneNumber = PHONE_UTIL.parse(value, null);

            if (!PHONE_UTIL.isValidNumber(phoneNumber)) {
                return false;
            }

            PhoneNumberUtil.PhoneNumberType type = PHONE_UTIL.getNumberType(phoneNumber);
            return type == PhoneNumberUtil.PhoneNumberType.MOBILE
                    || type == PhoneNumberUtil.PhoneNumberType.FIXED_LINE_OR_MOBILE;

        } catch (NumberParseException e) {
            return false;
        }
    }
}
