package com.google.inject.extensions.security;

import io.jsonwebtoken.Claims;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface SecurityTokenService {

    ParsedToken validateToken(String token);

    String createToken(SecurityUser user);

    public RoleConverter getRoleConverter();

    Date now();

    class ParsedToken implements SecurityUser {
        //   private final String token;
        private final Claims claims;
        private final SecurityTokenService ts;
        private final RoleConverter roleConverter;

        protected ParsedToken(SecurityTokenService ts, String token, Claims claims) {
            //  this.token = token;
            this.claims = claims;
            this.ts = ts;
            this.roleConverter = ts.getRoleConverter();
        }

        @Override
        public String getUsername() {
            return claims.getSubject();
        }

        boolean isValid(SecurityUser userDetails) {
            String username = userDetails.getUsername();
            // Date lastPasswordResetDate =  ((JwtUser) userDetails).getLastPasswordResetDate();
            Date now = ts.now();
            return username.equals(claims.getSubject()) && now.before(claims.getExpiration());
            // (lastPasswordResetDate == null || lastPasswordResetDate.before(claims.getIssuedAt()));
        }

        @Override
        public Set<Class<? extends SecurityRole>> getRoles() {
            return ((List<?>) claims.get("a", List.class))
                    .stream()
                    .map(a -> (String) a)
                    .map(roleConverter::toRole)
                    .collect(Collectors.toSet());
        }
    }
}
