package com.github.gpor0.jaffas.security;

import com.github.gpor0.jaffas.endpoints.model.SyncPermission;
import com.github.gpor0.jaffas.endpoints.model.SyncRole;
import com.github.gpor0.jaffas.endpoints.model.SyncRolePermissions;
import org.eclipse.microprofile.config.Config;
import org.yaml.snakeyaml.Yaml;

import javax.inject.Inject;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * This class is meant to be shared among all microservices.
 * <p>
 * It contains security logic
 */
public abstract class Security {

    //wildcard scope (needed for system admin)
    public static final String SCOPE_WILDCARD = "*:*";

    private static final Logger LOG = Logger.getLogger(Security.class.getName());
    @Inject
    protected Config config;

    public abstract String getApplicationName();

    public abstract List<ApplicationRole> getRoles();

    public String getClientId() {
        return config.getValue("auth.client.clientId", String.class);
    }

    /**
     * Security gets parsed from Openapi specification in yaml format.
     * <p>
     * All roles must be defined in:
     * components:
     * securitySchemes:
     * "shemeName":
     * flows:
     * "flowName":
     * scopes:
     * system: system permissions
     * accessAdmin: User access administrator
     * ....
     * <p>
     * Each operation defines scopes and permissions required to call.
     * <p>
     * security:
     * - "shemeName":
     * - 'userRole:cre'
     * - accessAdmin
     * - system
     * <p>
     * Above example shows accessAdmin and system as roles and userRole:cre as permission. Roles MUST be defined in securitySchemes. Parsed security item is defined as permission if not defined in flow scopes.
     * <p>
     * Each operation may implement further security requirements (like user check...).
     *
     * @param yamlStream
     */
    public SyncRolePermissions readYamlSecurity(InputStream yamlStream) {
        final Yaml yaml = new Yaml();
        final Map<String, Object> obj = yaml.load(yamlStream);

        final Map<String, Set<String>> scopeMap = new HashMap<>();
        final Map<String, String> scopeDescMap = new HashMap<>();
        final Map<String, String> permissionDescMap = new HashMap<>();

        this.getRoles().stream().forEach(appRole -> {
            scopeMap.put(appRole.getName(), appRole.getPermissions());
            scopeDescMap.put(appRole.getName(), appRole.getDescription());
            appRole.getPermissions().stream().forEach(perm -> permissionDescMap.put(perm, null));
        });


        final Map<String, Map> components = (Map<String, Map>) obj.get("components");
        final Map<String, Map> securitySchemes = (Map<String, Map>) components.get("securitySchemes");
        for (Map.Entry<String, Map> def : securitySchemes.entrySet()) {
            Map<String, Map> flows = (Map) def.getValue().get("flows");

            for (Map.Entry<String, Map> flow : flows.entrySet()) {
                if (flow != null) {
                    Map<String, String> scopeNameDesc = (Map<String, String>) flow.getValue().get("scopes");
                    scopeNameDesc.entrySet().stream().forEach(e -> {
                        scopeMap.put(e.getKey(), new HashSet<>());
                        scopeDescMap.put(e.getKey(), e.getValue());
                    });
                }
            }
        }

        final Map<String, Map> map = (Map<String, Map>) obj.get("paths");
        for (Map.Entry<String, Map> path : map.entrySet()) {
            Map<String, Map> api = path.getValue();
            for (Map.Entry<String, Map> method : api.entrySet()) {
                Map methodProps = method.getValue();
                String operationId = (String) methodProps.get("operationId");
                String description = (String) methodProps.get("description");
                List<Map> securities = (List<Map>) methodProps.get("security");

                if (securities == null) {
                    continue;
                }
                for (Map<String, List<String>> auth : securities) {
                    for (Map.Entry<String, List<String>> scope : auth.entrySet()) {
                        Set<String> permissions = new HashSet<>(scope.getValue());
                        Set<String> roles = new HashSet<>(scopeMap.keySet());
                        roles.retainAll(permissions);
                        permissions.removeAll(roles);//scope.getValue() - registered scopes = permissions
                        permissions.stream().forEach(permission -> {
                            String existingDesc = permissionDescMap.get(permission);
                            if (existingDesc == null) {
                                existingDesc = operationId + ": " + description;
                            } else {
                                existingDesc = existingDesc + " | " + operationId + ": " + description;
                            }
                            permissionDescMap.put(permission, existingDesc);
                        });
                        roles.stream().forEach(role -> scopeMap.get(role).addAll(permissions));
                    }
                }
            }
        }

        final List<SyncRole> applicationRolePermissions = scopeMap.entrySet().stream()
                .filter(e -> !SCOPE_WILDCARD.equals(e.getKey()))
                .map(roleEntry -> {
                    SyncRole applicationRole = new SyncRole();
                    applicationRole.setName(roleEntry.getKey());
                    applicationRole.setDescription(scopeDescMap.get(roleEntry.getKey()));
                    roleEntry.getValue().stream().forEach(permission -> applicationRole.addPermissionsItem(permission));
                    return applicationRole;
                }).collect(Collectors.toList());

        final List<SyncPermission> applicationPermissions = permissionDescMap.entrySet().stream().map(e -> new SyncPermission().name(e.getKey()).description(e.getValue())).collect(Collectors.toList());

        final SyncRolePermissions result = new SyncRolePermissions();
        result.setPermissions(applicationPermissions);
        result.setRolePermissions(applicationRolePermissions);

        return result;
    }

    public static class ApplicationRole {
        private String name;
        private String description;
        private Set<String> permissions;

        public ApplicationRole(String name, String description) {
            this.name = name;
            this.description = description;
            this.permissions = new HashSet<>();
        }

        public ApplicationRole addPermission(String name) {
            permissions.add(name);
            return this;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public Set<String> getPermissions() {
            return permissions;
        }
    }

}
