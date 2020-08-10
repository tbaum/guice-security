package com.google.inject.extensions.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClock;

import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * @author tbaum
 * @since 05.08.18.
 */
public class SecurityTokenServiceImpl implements SecurityTokenService {
    private final Key secret;
    private final long expiration;
    private final RoleConverter roleConverter;
    private final Clock clock;

    public SecurityTokenServiceImpl(Key secret, long expiration, RoleConverter roleConverter) {
        this(secret, expiration, roleConverter, DefaultClock.INSTANCE);
    }

    public SecurityTokenServiceImpl(Key secret, long expiration, RoleConverter roleConverter, Clock clock) {
        this.secret = secret;
        this.expiration = expiration;
        this.roleConverter = roleConverter;
        this.clock = clock;
    }

    public RoleConverter getRoleConverter() {
        return roleConverter;
    }

    @Override
    public Date now() {
        return clock.now();
    }

    @Override
    public ParsedToken validateToken(String token) {


        Claims claims = Jwts.parserBuilder().setClock(clock).setSigningKey(secret).build()
                .parseClaimsJws(token)
                .getBody();
        return new ParsedToken(this, token, claims);
    }

    @Override
    public String createToken(SecurityUser user) {
        Date createdDate = now();
        Collection<Class<? extends SecurityRole>> authorities = user.getRoles();
        Map<String, Object> a = new HashMap<>();
        if (authorities != null) {
            a.put("a", authorities.stream().map(roleConverter::toString).collect(toList()));
        }
        return Jwts.builder()
                .setClaims(a)
                .setSubject(user.getUsername())
                .setIssuedAt(createdDate)
                .setExpiration(new Date(createdDate.getTime() + expiration * 1000))
                .signWith(secret)
                .compact();
    }
}
