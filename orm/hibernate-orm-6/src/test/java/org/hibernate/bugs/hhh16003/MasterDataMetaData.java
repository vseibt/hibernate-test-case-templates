package org.hibernate.bugs.hhh16003;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import static java.util.Objects.requireNonNull;

/**
 * The describing information to the master data file upload.
 */
@Embeddable
public class MasterDataMetaData {

    @Column(name = "country_code", nullable = false, length = 8)
    private String country;

    @Column(name = "transport_mode", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransportMode transportMode;

    @Column(name = "product", nullable = false)
    private String product;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode;

    protected MasterDataMetaData() {
    }

    public MasterDataMetaData(
            String product,
            String country,
            TransportMode transportMode,
            String currencyCode
    ) {
        this.product = requireNonNull(product, "Product must not be null");
        this.country = requireNonNull(country, "Country must not be null");
        this.transportMode = requireNonNull(transportMode, "TransportMode must not be null");
        this.currencyCode = requireNonNull(currencyCode, "CurrencyCode must not be null");
    }
}
