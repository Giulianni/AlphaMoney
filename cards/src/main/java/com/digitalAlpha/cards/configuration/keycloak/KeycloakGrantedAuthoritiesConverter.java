package com.digitalAlpha.cards.configuration.keycloak;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class KeycloakGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    public KeycloakGrantedAuthoritiesConverter() {
    }

    public Collection<GrantedAuthority> convert(Jwt source) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        Map<String, Object> realmAccessRoles = (Map<String, Object>) source.getClaims().get("realm_access");

        if (realmAccessRoles != null && !realmAccessRoles.isEmpty()) {
            authorities.addAll(extractRoles(realmAccessRoles));
        }

        String scopes = (String) source.getClaims().get("scope");

        if (scopes != null && !scopes.isEmpty()) {
            authorities.addAll(extractScopes(scopes));
        }

        List<String> groups = (List<String>) source.getClaims().get("groups");

        if (groups != null && !groups.isEmpty()) {
            authorities.addAll(extractGroups(groups));
        }

        return authorities;
    }


    private static Collection<GrantedAuthority> extractRoles(Map<String, Object> realmAccessRoles) {
        return ((List<String>) realmAccessRoles.get("roles"))
                .stream().map(roleName -> "ROLE_" + roleName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private static Collection<GrantedAuthority> extractScopes(String scopes) {
        return Arrays.stream(scopes.split(" ")).toList()
                .stream().map(roleName -> "SCOPE_" + roleName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private static Collection<GrantedAuthority> extractGroups(List<String> groups) {
        return groups.stream()
                .map(membership -> membership.substring(1))
                .map(groupName -> "GROUP_" + groupName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
