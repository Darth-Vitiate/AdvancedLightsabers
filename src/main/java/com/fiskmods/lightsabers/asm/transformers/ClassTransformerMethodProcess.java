package com.fiskmods.lightsabers.asm.transformers;

import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;

/**
 * Modernized version of ClassTransformerMethodProcess for Forge 1.20.1+.
 * <p>
 * This serves as a base class for transformers that modify a specific method
 * within a target class.
 * <p>
 * It removes LaunchWrapper dependency and obfuscation mappings logic,
 * while remaining fully compatible with ModLauncher’s environment.
 */
public abstract class ClassTransformerMethodProcess extends ClassTransformerBase {
    private final String methodName;      // Obfuscated or runtime name
    private final String methodNameDev;   // Development name
    private final String methodDesc;      // Obfuscated or runtime descriptor
    private final String methodDescDev;   // Development descriptor

    protected String targetMethodName;    // Resolved method name
    protected String targetMethodDesc;    // Resolved method descriptor

    public static String varPlayer;

    /**
     * @param classPath     The full internal class path (e.g. net.minecraft.world.entity.player.Player)
     * @param methodName    The obfuscated method name (for backward compatibility)
     * @param methodNameDev The dev method name (for MCP/Forge dev env)
     * @param methodDesc    The obfuscated method descriptor
     * @param methodDescDev The dev descriptor (e.g. (Lnet/minecraft/world/entity/Entity;)V)
     */
    public ClassTransformerMethodProcess(String classPath, String methodName, String methodNameDev, String methodDesc, String methodDescDev) {
        super(classPath);
        this.methodName = methodName;
        this.methodNameDev = methodNameDev;
        this.methodDesc = methodDesc;
        this.methodDescDev = methodDescDev;
    }

    /**
     * Iterates through all methods and invokes {@link #processMethod(MethodNode)} on the one that matches.
     */
    @Override
    public boolean processMethods(List<MethodNode> methods) {
        for (MethodNode method : methods) {
            if (method.name.equals(targetMethodName) && method.desc.equals(targetMethodDesc)) {
                sendPatchLog(targetMethodName + targetMethodDesc);
                processMethod(method);
                return true;
            }
        }
        return false;
    }

    /**
     * Override this to define how the matching method should be modified.
     */
    public abstract void processMethod(MethodNode method);

    @Override
    public boolean processFields(List<FieldNode> fields) {
        return true;
    }

    @Override
    public void setupMappings() {
        // No obfuscation mapping needed in modern Forge;
        // simply resolve directly to dev-friendly names.
        targetMethodName = methodNameDev;
        targetMethodDesc = methodDescDev;

        // Update reference for player class
        varPlayer = "net/minecraft/world/entity/player/Player";
    }
}
