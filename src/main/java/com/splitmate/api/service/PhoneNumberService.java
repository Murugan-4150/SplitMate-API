package com.splitmate.api.service;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.springframework.stereotype.Service;

@Service
public class PhoneNumberService {

    private static final PhoneNumberUtil PHONE_UTIL = PhoneNumberUtil.getInstance();

    public String normalizeToE164(String phoneNumber) {
        try {
            Phonenumber.PhoneNumber parsed = PHONE_UTIL.parse(phoneNumber, null);
            return PHONE_UTIL.format(parsed, PhoneNumberUtil.PhoneNumberFormat.E164);
        } catch (NumberParseException e) {
            throw new IllegalArgumentException("Cannot normalize phone number: " + phoneNumber, e);
        }
    }
}
