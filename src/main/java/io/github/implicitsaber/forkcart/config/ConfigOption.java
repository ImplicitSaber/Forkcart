package io.github.implicitsaber.forkcart.config;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.implicitsaber.forkcart.Forkcart;
import net.minecraft.command.CommandSource;

import java.io.IOException;
import java.util.Properties;

public abstract class ConfigOption<T> {
    protected final Config owner;

    public final String key;
    protected T value;

    protected ConfigOption(String key, T initialValue, Config owner) {
        this.owner = owner;

        this.key = key;
        this.value = initialValue;
    }

    protected abstract void read(Properties properties);

    protected abstract void write(Properties properties);

    protected abstract ArgumentType<T> commandArgType();

    public final <S extends CommandSource> RequiredArgumentBuilder<S, ?> commandArg(String name) {
        return RequiredArgumentBuilder.argument(name, this.commandArgType());
    }

    public T get() {
        return this.value;
    }

    public void set(T value) {
        this.value = value;
    }

    public void setAndSave(T value) {
        this.set(value);
        try {
            this.owner.save();
        } catch (IOException e) {
            Forkcart.LOGGER.error("Error saving config '{}' while setting value '{}={}'",
                    this.owner.id, this.key, this.value, e);
        }
    }

    public abstract <S extends CommandSource> void setFromCommandAndSave(CommandContext<S> ctx, String argName);

    public static class BooleanOption extends ConfigOption<Boolean> {
        public BooleanOption(String key, Boolean initialValue, Config owner) {
            super(key, initialValue, owner);
        }

        @Override
        protected void read(Properties properties) {
            if (properties.containsKey(this.key)) {
                this.value = "true".equals(properties.getProperty(this.key));
            }
        }

        @Override
        protected void write(Properties properties) {
            properties.setProperty(this.key, this.value ? "true" : "false");
        }

        @Override
        public ArgumentType<Boolean> commandArgType() {
            return BoolArgumentType.bool();
        }

        @Override
        public <S extends CommandSource> void setFromCommandAndSave(CommandContext<S> ctx, String argName) {
            this.setAndSave(BoolArgumentType.getBool(ctx, argName));
        }
    }

    public static class IntOption extends ConfigOption<Integer> {
        private final int[] bounds;
        public IntOption(String key, Integer initialValue, int[] bounds, Config owner) {
            super(key, initialValue, owner);
            this.bounds = bounds;
        }

        @Override
        protected void read(Properties properties) {
            if (properties.containsKey(this.key)) {
                this.value = Integer.parseInt(properties.getProperty(this.key));
            }
        }

        @Override
        protected void write(Properties properties) {
            properties.setProperty(this.key, Integer.toString(this.value));
        }

        @Override
        public ArgumentType<Integer> commandArgType() {
            if (this.bounds.length == 1) {
                return IntegerArgumentType.integer(this.bounds[0]);
            }
            if (this.bounds.length == 2) {
                return IntegerArgumentType.integer(this.bounds[0], this.bounds[1]);
            }
            return IntegerArgumentType.integer();
        }

        @Override
        public <S extends CommandSource> void setFromCommandAndSave(CommandContext<S> ctx, String argName) {
            this.setAndSave(IntegerArgumentType.getInteger(ctx, argName));
        }
    }
}
