package com.test.feulmgmt.stripe;

import androidx.annotation.NonNull;

import com.stripe.android.PaymentSessionConfig;
import com.stripe.android.PaymentSessionConfig.ShippingInformationValidator;
import com.stripe.android.model.Address;
import com.stripe.android.model.ShippingInformation;

import java.util.Locale;

public class AppShippingInformationValidator implements ShippingInformationValidator {

    @Override
    public boolean isValid(
            @NonNull ShippingInformation shippingInformation
    ) {
        final Address address = shippingInformation.getAddress();
        return address != null && Locale.US.getCountry() == address.getCountry();
    }

    @NonNull
    public String getErrorMessage(
            @NonNull ShippingInformation shippingInformation
    ) {
        return "A US address is required";
    }
}
