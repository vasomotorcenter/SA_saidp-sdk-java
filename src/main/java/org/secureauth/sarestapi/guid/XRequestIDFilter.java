package org.secureauth.sarestapi.guid;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;

public class XRequestIDFilter implements ClientRequestFilter {

    private String transactionId;

    public XRequestIDFilter(String transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public void filter(ClientRequestContext requestContext) {
        requestContext.getHeaders().putSingle("X-Request-ID", this.transactionId);
    }
}
