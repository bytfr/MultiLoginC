package moe.caa.multilogin.core.auth.validate.entry;

import lombok.SneakyThrows;
import moe.caa.multilogin.api.internal.logger.LoggerProvider;
import moe.caa.multilogin.api.internal.plugin.IPlayer;
import moe.caa.multilogin.api.internal.util.Pair;
import moe.caa.multilogin.api.internal.util.ValueUtil;
import moe.caa.multilogin.core.auth.validate.ValidateContext;
import moe.caa.multilogin.core.main.MultiCore;
import moe.caa.multilogin.flows.workflows.BaseFlows;
import moe.caa.multilogin.flows.workflows.Signal;

import java.sql.SQLException;
import java.util.UUID;

public class AssignInGameFlows extends BaseFlows<ValidateContext> {
    private static final int MINECRAFT_MAX_USERNAME_LENGTH = 16;
    private final MultiCore core;

    public AssignInGameFlows(MultiCore core) {
        this.core = core;
    }

    @SneakyThrows
    @Override
    public Signal run(ValidateContext validateContext) {

        UUID inGameUUID = core.getSqlManager().getUserDataTable().getInGameUUID(
                validateContext.getBaseServiceAuthenticationResult().getResponse().getId(),
                validateContext.getBaseServiceAuthenticationResult().getServiceConfig().getId()
        );

        String loginName = validateContext.getBaseServiceAuthenticationResult().getResponse().getName();
        if (inGameUUID == null) {

            inGameUUID = validateContext.getBaseServiceAuthenticationResult().getServiceConfig().getInitUUID()
                    .generateUUID(validateContext.getBaseServiceAuthenticationResult().getResponse().getId(), loginName);

            synchronized (AssignInGameFlows.class) {
                while (core.getSqlManager().getInGameProfileTable().dataExists(inGameUUID)) {
                    LoggerProvider.getLogger().warn(String.format("UUID %s has been used and will take a random value.", inGameUUID.toString()));
                    inGameUUID = UUID.randomUUID();
                }
                core.getSqlManager().getUserDataTable().setInGameUUID(
                        validateContext.getBaseServiceAuthenticationResult().getResponse().getId(),
                        validateContext.getBaseServiceAuthenticationResult().getServiceConfig().getId(),
                        inGameUUID);
            }
        }

        boolean exist = core.getSqlManager().getInGameProfileTable().dataExists(inGameUUID);
        if (exist) {
            String username = core.getSqlManager().getInGameProfileTable().getUsername(inGameUUID);
            if (!ValueUtil.isEmpty(username)) {
                if (core.getPluginConfig().isAutoNameChange() && validateContext.isOnlineNameUpdated()) {
                    String fixName = validateContext.getBaseServiceAuthenticationResult().getServiceConfig().generateName(loginName);
                    if (fixName.isEmpty()) fixName = "1";

                    String initFixName = fixName;
                    if (core.getPluginConfig().isNameCorrect()) {
                        fixName = resolveNameConflict(fixName, inGameUUID);
                    }
                    fixName = truncateName(fixName);

                    try {
                        core.getSqlManager().getInGameProfileTable().updateUsername(inGameUUID, fixName);
                        notifyNameChange(inGameUUID, initFixName, fixName);
                        validateContext.getInGameProfile().setId(inGameUUID);
                        validateContext.getInGameProfile().setName(fixName);
                        return Signal.PASSED;
                    } catch (SQLException e) {
                        handleNameConflict(validateContext);
                        return Signal.TERMINATED;
                    }
                }
                validateContext.getInGameProfile().setId(inGameUUID);
                validateContext.getInGameProfile().setName(username);
                return Signal.PASSED;
            }
        }

        String fixName = validateContext.getBaseServiceAuthenticationResult().getServiceConfig().generateName(loginName);
        if (fixName.isEmpty()) fixName = "1";

        String initFixName = fixName;
        if (core.getPluginConfig().isNameCorrect()) {
            fixName = resolveNameConflict(fixName, inGameUUID);
        }
        fixName = truncateName(fixName);

        if (exist) {
            try {
                core.getSqlManager().getInGameProfileTable().updateUsername(inGameUUID, fixName);
                notifyNameChange(inGameUUID, initFixName, fixName);
                validateContext.getInGameProfile().setId(inGameUUID);
                validateContext.getInGameProfile().setName(fixName);
                return Signal.PASSED;
            } catch (SQLException e) {
                handleNameConflict(validateContext);
                return Signal.TERMINATED;
            }
        } else {
            try {
                core.getSqlManager().getInGameProfileTable().insertNewData(inGameUUID, fixName);
                notifyNameChange(inGameUUID, initFixName, fixName);
                validateContext.getInGameProfile().setId(inGameUUID);
                validateContext.getInGameProfile().setName(fixName);
                return Signal.PASSED;
            } catch (SQLException e) {
                handleNameConflict(validateContext);
                return Signal.TERMINATED;
            }
        }
    }

    private String resolveNameConflict(String fixName, UUID inGameUUID) throws SQLException {
        UUID ownerUUID;
        while ((ownerUUID = core.getSqlManager().getInGameProfileTable().getInGameUUIDIgnoreCase(fixName)) != null) {
            if (ownerUUID.equals(inGameUUID)) break;
            fixName = incrementString(fixName);
        }
        return fixName;
    }

    private String truncateName(String name) {
        if (name.length() > MINECRAFT_MAX_USERNAME_LENGTH) {
            return name.substring(0, MINECRAFT_MAX_USERNAME_LENGTH);
        }
        return name;
    }

    private void notifyNameChange(UUID inGameUUID, String oldName, String newName) {
        if (!oldName.equals(newName)) {
            LoggerProvider.getLogger().warn(String.format("The name %s is occupied, change it to %s.", oldName, newName));
            core.getPlugin().getRunServer().getScheduler().runTaskAsync(() -> {
                IPlayer player = core.getPlugin().getRunServer().getPlayerManager().getPlayer(inGameUUID);
                if (player != null) {
                    player.sendMessagePL(core.getLanguageHandler().getMessage("name_correct_info",
                            new Pair<>("old_name", oldName),
                            new Pair<>("new_name", newName)
                    ));
                }
            }, 2000);
        }
    }

    private void handleNameConflict(ValidateContext validateContext) {
        validateContext.setDisallowMessage(core.getLanguageHandler().getMessage("auth_validate_failed_username_repeated",
                new Pair<>("name", validateContext.getInGameProfile().getName())
        ));
    }

    private String incrementString(String source) {
        if (source.isEmpty()) return "1";

        char c = source.charAt(source.length() - 1);
        if (Character.isDigit(c)) {
            int i = Character.getNumericValue(c);
            if (i == 9) {
                return incrementString(source.substring(0, source.length() - 1)) + "0";
            } else {
                return source.substring(0, source.length() - 1) + (i + 1);
            }
        }

        return source + "1";
    }
}
