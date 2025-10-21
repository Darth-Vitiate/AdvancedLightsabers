package com.fiskmods.lightsabers.client.render.hilt;

import com.fiskmods.lightsabers.client.model.lightsaber.*;
import com.fiskmods.lightsabers.common.hilt.HiltManager;

/**
 * Registers all hilt renderers with their corresponding models.
 * Updated for Forge 1.20.1 and modern HiltRenderer system.
 */
public class HiltRendererManager {

    public static void register() {

        // Standard hilts
        HiltRenderer.register(HiltManager.GRAFLEX,
                new HiltRendererBase(new ModelEmitterGraflex(), new ModelSwitchSectionGraflex(),
                        new ModelBodyGraflex(), new ModelPommelGraflex()));

        HiltRenderer.register(HiltManager.REDEEMER,
                new HiltRendererBase(new ModelEmitterRedeemer(), new ModelSwitchSectionRedeemer(),
                        new ModelBodyRedeemer(), new ModelPommelRedeemer()));

        HiltRenderer.register(HiltManager.MAULER,
                new HiltRendererBase(new ModelEmitterMauler(), new ModelSwitchSectionMauler(),
                        new ModelBodyMauler(), new ModelPommelMauler()));

        HiltRenderer.register(HiltManager.PRODIGAL_SON,
                new HiltRendererBase(new ModelEmitterProdigalSon(), new ModelSwitchSectionProdigalSon(),
                        new ModelBodyProdigalSon(), new ModelPommelProdigalSon()));

        HiltRenderer.register(HiltManager.KNIGHTED,
                new HiltRendererBase(new ModelEmitterKnighted(), new ModelSwitchSectionKnighted(),
                        new ModelBodyKnighted(), new ModelPommelKnighted()));

        // Vaid variants (Ancient & Modern)
        HiltRenderer.register(HiltManager.VAID_ANCIENT,
                new HiltRendererBase(new ModelEmitterVaid(), new ModelSwitchSectionVaid(),
                        new ModelBodyVaid(), new ModelPommelVaid()));

        HiltRenderer.register(HiltManager.VAID_MODERN,
                new HiltRendererBase(new ModelEmitterVaid(), new ModelSwitchSectionVaid(),
                        new ModelBodyVaid(), new ModelPommelVaid()));

        HiltRenderer.register(HiltManager.DROIDEKA,
                new HiltRendererBase(new ModelEmitterDroideka(), new ModelSwitchSectionDroideka(),
                        new ModelBodyDroideka(), new ModelPommelDroideka()));

        HiltRenderer.register(HiltManager.FULCRUM,
                new HiltRendererBase(new ModelEmitterFulcrum(), new ModelSwitchSectionFulcrum(),
                        new ModelBodyFulcrum(), new ModelPommelFulcrum()));

        HiltRenderer.register(HiltManager.JUGGERNAUT,
                new HiltRendererBase(new ModelEmitterJuggernaut(), new ModelSwitchSectionJuggernaut(),
                        new ModelBodyJuggernaut(), new ModelPommelJuggernaut()));

        HiltRenderer.register(HiltManager.MECHANICAL,
                new HiltRendererBase(new ModelEmitterMechanical(), new ModelSwitchSectionMechanical(),
                        new ModelBodyMechanical(), new ModelPommelMechanical()));

        HiltRenderer.register(HiltManager.MANDALORIAN,
                new HiltRendererBase(new ModelEmitterMandalorian(), new ModelSwitchSectionMandalorian(),
                        new ModelBodyMandalorian(), new ModelPommelMandalorian()));

        HiltRenderer.register(HiltManager.FURY,
                new HiltRendererBase(new ModelEmitterFury(), new ModelSwitchSectionFury(),
                        new ModelBodyFury(), new ModelPommelFury()));

        HiltRenderer.register(HiltManager.REBEL,
                new HiltRendererBase(new ModelEmitterRebel(), new ModelSwitchSectionRebel(),
                        new ModelBodyRebel(), new ModelPommelRebel()));

        HiltRenderer.register(HiltManager.IMPERIAL,
                new HiltRendererBase(new ModelEmitterImperial(), new ModelSwitchSectionImperial(),
                        new ModelBodyImperial(), new ModelPommelImperial()));

        // Special scaling variant (uses custom subclass)
        HiltRenderer.register(HiltManager.REBORN,
                new HiltRendererOneTwelve(new ModelEmitterReborn(), new ModelSwitchSectionReborn(),
                        new ModelBodyReborn(), new ModelPommelReborn()));
    }
}
