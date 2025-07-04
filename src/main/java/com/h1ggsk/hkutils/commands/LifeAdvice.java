package com.h1ggsk.hkutils.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

import java.util.List;
import java.util.Random;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class LifeAdvice extends Command {
    private static final List<String> ADVICE_LIST = List.of(
        "don't spam or your gay",
        "don't make it weird or your not sigma",
        "no one wants to know about your nfsw so shut it",
        "no diddling children",
        "have general knowledge",
        "don't be a dumbass",
        "follow za rules",
        "don't give h1ggsk a headache",
        "give me a break",
        "be a good boi",
        "don't ping me for stupid reasons - important",
        "don't make me regret making this feature - very very important",
        "don't be cruel to animals",
        "if you say 'I’m not reading all that' congrats youre illiterate",
        "if you say 'who asked?,' the FBI did (enjoy your audit)",
        "if you say ‘touch grass,’ i’ll touch you",
        "don't ask dumb questions unless you want dumb answers",
        "don't be a clown or the circus will claim you",
        "please shut the fuck up or im cutting off your cable"
    );

    private static final Random RANDOM = new Random();

    public LifeAdvice() {
        super("lifeadvice", "Gives you life advice");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            String randomAdvice = ADVICE_LIST.get(RANDOM.nextInt(ADVICE_LIST.size()));
            info(randomAdvice);
            return SINGLE_SUCCESS;
        });
    }
}
