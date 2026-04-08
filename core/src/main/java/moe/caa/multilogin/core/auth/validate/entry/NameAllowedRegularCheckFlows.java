package moe.caa.multilogin.core.auth.validate.entry;

import moe.caa.multilogin.api.internal.util.Pair;
import moe.caa.multilogin.api.internal.util.ValueUtil;
import moe.caa.multilogin.core.auth.validate.ValidateContext;
import moe.caa.multilogin.core.main.MultiCore;
import moe.caa.multilogin.flows.workflows.BaseFlows;
import moe.caa.multilogin.flows.workflows.Signal;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class NameAllowedRegularCheckFlows extends BaseFlows<ValidateContext> {
    private final MultiCore core;
    private volatile Pattern cachedPattern;
    private volatile String cachedPatternStr;

    public NameAllowedRegularCheckFlows(MultiCore core) {
        this.core = core;
    }

    @Override
    public Signal run(ValidateContext validateContext) {
        String nameAllowedRegular = core.getPluginConfig().getNameAllowedRegular();
        if (ValueUtil.isEmpty(nameAllowedRegular)) {
            return Signal.PASSED;
        }
        Pattern pattern = getOrCreatePattern(nameAllowedRegular);
        if (pattern == null) {
            return Signal.PASSED;
        }
        if (!pattern.matcher(validateContext.getBaseServiceAuthenticationResult().getResponse().getName()).matches()) {
            validateContext.setDisallowMessage(core.getLanguageHandler().getMessage("auth_validate_failed_username_mismatch",
                    new Pair<>("name", validateContext.getBaseServiceAuthenticationResult().getResponse().getName()),
                    new Pair<>("regular", nameAllowedRegular)
            ));
            return Signal.TERMINATED;
        }
        return Signal.PASSED;
    }

    private Pattern getOrCreatePattern(String patternStr) {
        if (patternStr.equals(cachedPatternStr) && cachedPattern != null) {
            return cachedPattern;
        }
        try {
            Pattern pattern = Pattern.compile(patternStr);
            cachedPattern = pattern;
            cachedPatternStr = patternStr;
            return pattern;
        } catch (PatternSyntaxException e) {
            core.getPlugin().getRunServer().getConsoleSender().sendMessagePL(
                    "Invalid nameAllowedRegular pattern: " + patternStr + ", error: " + e.getMessage());
            return null;
        }
    }
}
