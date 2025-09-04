package org.lucas.model;

import java.time.OffsetDateTime;

public record MoneyAudit(
            UUID transactionId,
            BankService targetService,
            String description,
            OffsetDateTime createdAt
        )
{

}
