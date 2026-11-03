# Chibi Player: Architecture Analysis

**Status:** architecture recommendation adopted for the Fabric 26.3 client-only MVP; compatibility still depends on tested mod versions.
**Research snapshot:** 1 November 2026
**Scope:** client-side Chibi player rendering for the Fabric 26.3 project indicated by the supplied previous artifact. Confirm the target before implementation.

## Executive summary

Keep Minecraft's existing player renderer, player render state, skin selection, and feature/layer pipeline. Adapt the vanilla player model's proportions and attachment pivots while preserving its model-part identities and animated pose. Do not register a replacement player renderer or render a second player model.

This is the strongest compatibility starting point because animation and cosmetic mods commonly alter, decorate, or attach to the renderer and model Minecraft already uses. Replacing either silently opts out of those integrations. The approach still needs a narrow, version-specific integration point in the vanilla player render/model path: Fabric's entity layer registration API adds layers but does not provide a general API for changing the built-in player model's geometry or proportions. Keep the injection limited to the player renderer/model, gated by the local per-player render policy, and avoid altering entities, hitboxes, packets, or server behavior.

Treat compatibility as something to demonstrate with the named mods and resource packs, not something that can be guaranteed merely by choosing an API. In particular, animation systems that replace the model or use their own renderer remain a risk and may need small, optional adapters.

## What I understood from the project material

The first release is a cosmetic, client-only Chibi transformation of the player's active skin. It must leave gameplay and vanilla hitboxes alone, work on vanilla servers without protocol changes, and let the local user choose whether to render themselves and other players in Chibi form. The requested compatibility surface includes animation, cosmetic, emote, cape, skin-provider, and resource-pack integrations. Skin acquisition should be inherited from the active vanilla/player texture pipeline rather than reimplemented per provider.

The project's rules consistently prioritize interoperability over perfect visual fidelity, avoiding replacement of vanilla systems, comparing architectures before implementation, and keeping V1 focused. The future morph framework is a vision, not a reason to add V1 abstraction or custom-morph infrastructure now.

The compatibility goals that must be treated as architectural requirements are:

- Preserve the active skin and its UV layout, including slim/classic player model variants.
- Keep items attached to the animated hands and capes attached to the animated torso.
- Keep eyes/facial layers aligned with the head, including Just Expressions and Fresh Animations Player Extension.
- Keep pivots connected when proportions change; preserve animation-produced rotations and poses.
- Keep the vanilla entity and hitbox dimensions, client-only behavior, and multiplayer server compatibility.
- Provide local display modes: Everyone Normal, Everyone Chibi, Only Me, and a custom per-player list.

These display modes only control what the local client renders. Without synchronization, one player's choice cannot make another player's client render Chibi. The UI wording should make this local nature clear.

## What is unclear or missing

1. **Target platform:** The previous JAR says Fabric and Minecraft `~26.3`, but the new project has no stated loader/version/build files in the supplied material. Confirm whether V1 targets Fabric 26.3 only, and whether other loaders or versions are in scope.
2. **Meaning of “Everyone Chibi”:** The requirements also say client-side only and no multiplayer synchronization. This analysis assumes the mode means “render every player as Chibi on my client.”
3. **Third-person self rendering:** “View themselves” could mean only third-person camera views, or also mirrors, inventory previews, and other render contexts. The renderer policy should be keyed to render state/context and not accidentally change GUI previews.
4. **Chibi proportions:** No target measurements, reference image, head/body ratio, or treatment of sleeves/outer skin layers is specified. Decide the visual target before tuning model pivots.
5. **Cosmetic compatibility breadth:** “Compatible” could mean no crashes, or precise attachment/animation for every cosmetic item. The latter requires a named test matrix and may require upstream mod cooperation.
6. **Skin provider mechanism:** The goal is automatic support for providers that expose their skin through Minecraft's active player texture/render state. A provider that bypasses that pipeline may need an adapter; the supplied notes do not say which providers are in scope or versions.
7. **Project maintenance target:** No update policy or supported Minecraft version range is recorded. Since renderer internals and mixin targets change, the narrow integration needs an explicit support matrix.

Suggested default decisions for the next phase: Fabric 26.3 only; every setting is local to the rendering client; self-render means world rendering in third person; use a single agreed Chibi proportion reference; and define compatibility as “works without configuration when the target mod uses the vanilla player renderer/model,” with adapters for verified exceptions.

## Previous mod review: PartScale 1.0.14+26.3

The supplied `old-mod/partscale-1.0.14+26.3.jar` is a valid Fabric client mod, not source code and not a Chibi implementation. Its metadata targets Fabric Loader 0.19.5+, Minecraft 26.3, Java 25, and Fabric API. I inspected its class inventory and bytecode because source was not included.

### Worth keeping as lessons

- **Preserve the vanilla player renderer.** PartScale modifies vanilla model parts rather than registering a new player renderer. That is directionally right for active skins, vanilla animation poses, and mod-added renderer layers.
- **Model-part transforms can keep UVs and skin layers.** Scaling the existing head/body/limbs retains their original texture mapping, which is a good baseline for expressions and outer-layer textures.
- **Treat attachments as first-class.** The old mod recognizes that hands and capes need explicit handling, and that Wavey Capes and EMF may require integration-specific code.
- **Resolve the player from render-state/entity context.** The newer renderer uses an `AvatarRenderState`; the old mod carries entity IDs through a thread-local stack to connect state-only layer calls back to the live player.
- **Keep the transform cosmetic.** The artifact is a client-only mod and does not alter entity dimensions or networking, matching the project's V1 scope.

These are patterns to evaluate, not code to copy.

### Redesign before reuse

- **Avoid mutating shared model state without a complete lifecycle.** The bytecode changes model part scales/pivots before model submission and restores them at a return injection. This is fragile if a target path changes, exits early, throws, or another mixin mutates the same fields. A render-state-oriented pipeline and narrowly scoped transform hook should make ordering and cleanup explicit.
- **Do not multiply prior scale blindly.** PartScale saves the current part scale and multiplies by its configured factor each time it applies. If a competing mod leaves a transform behind, repeats a transform, or changes injection order, the result can compound or be overwritten. The new design must preserve upstream animation pose and have deterministic baseline/restore behavior.
- **Do not assume vanilla arm/leg pivots describe every modded model.** PartScale applies hand-tuned pivot offsets. Those constants are model-specific and may disconnect parts or misalign held items on alternate models and animation rigs. Derive and validate attachment transforms from the actual model hierarchy.
- **Avoid broad interception of generic humanoid models.** Its model hook is on `HumanoidModel`, a shared type used beyond the player. Player-only behavior should be gated to the avatar renderer/state and not affect other humanoids.
- **Avoid fragile global render context where direct state is available.** The thread-local stack and ID-to-entity lookup add lifecycle assumptions. In the state-based render path, carry only the minimum stable identifier/policy result needed; do not reach back into a live entity from a render thread unless the target API requires it.
- **Keep compatibility adapters optional and isolated.** The artifact contains hard references to vanilla cape classes and Wavey Capes, plus reflective EMF lookup. Exact mixin targets can fail as mods evolve. Isolate optional integrations and never make an unrelated optional mod a hard startup dependency.
- **Do not build configuration/per-player part scaling into V1 without need.** The JAR includes per-part settings, preset/command/config support, GUI sliders, cape/item tuning, and override files. Those features are outside the requested fixed Chibi transformation and create more states to validate.

### Risks and mistakes not to repeat

The old mod's key risk is broad, low-level mutation of renderer/model fields combined with several independent injections around rendering. Cape, hand, Wavey Cape, and EMF paths each need bespoke synchronization. This is evidence that “the vanilla model was scaled” does not automatically make attachments compatible. Its `required: true` mixin configuration and default `require: 1` also turn target drift into a startup failure, which is a sharp maintenance cost for an optional cosmetic feature. Avoid copying literal offsets or private method targets without verifying each Minecraft build and the actual third-party call path.

The archive contains no source, test matrix, or compatibility report. Bytecode review cannot establish how it behaves in game with the named mods. No conclusion here should be treated as a confirmed runtime bug report.

## Implementation approaches compared

| Approach | Advantages | Disadvantages and compatibility risks | Assessment |
|---|---|---|---|
| **1. Keep the vanilla player renderer/model; adapt its model parts and pivots (recommended)** | Retains normal skin lookup, player render state, vanilla animation setup, item/cape/armor layers and add-on layers. Existing parts preserve skin UVs and are the parts other mods expect. No duplicate entity or hitbox. | Minecraft has no general public “change player proportions” API. A small version-specific hook is likely needed. Model parts are mutable/shared during rendering; injection order with animation mods matters. Cape/cosmetic layers may use assumptions about vanilla proportions. | Best compatibility baseline. Scope hooks tightly, preserve upstream pose/parts, validate layer order and cleanup. |
| **2. Add a Fabric player render layer/feature that draws Chibi geometry** | Uses a supported extension point for additive rendering; keeps base renderer and other layers. Useful for effects/cosmetics that add geometry. | A second Chibi model either overlays the vanilla player (z-fighting/double skin) or requires hiding the base model, which affects other render features. Third-party items, capes, cosmetics, and emotes still attach to the original model unless separately adapted. | Good for additive decoration, poor as the primary morph renderer. |
| **3. Register/replace the player renderer with a custom renderer** | Full control of model, proportions, layers and render flow. Clean ownership if every dependent integration targets the replacement. | Replaces the shared extension point. Mods that inject into, look up, cast, or subclass the vanilla renderer can be skipped or fail. Must reproduce skin variants, model layers, held items, cape, armor, elytra, cosmetics, emotes, visibility and render state behavior. High conflict surface. | Reject for V1 compatibility-first work. |
| **4. Render a custom Chibi model and suppress/hide the vanilla base model** | Full freedom for silhouette and pivots, including non-humanoid future morphs. A dedicated hierarchy can make attachment points explicit. | Functionally replaces the model while pretending to retain the renderer. Animation mods continue to animate the hidden vanilla model; the custom model needs animation translation/adapters. Layer attachments stay on vanilla parts, and suppression can hide other mods' geometry. Skin UVs and outer layers must be mapped deliberately. | Future option for morphs that cannot fit a humanoid rig; not the initial Chibi plan. |
| **5. Scale the whole entity/render pose** | Very small implementation; model, items and attached layers may scale together if applied at the right outer pose. | Makes the entire player uniformly smaller, including the head/body proportions, armor, nameplate/shadow and potentially camera/visibility behavior depending on hook point. Does not create a true Chibi silhouette with a large head and short limbs. Other pose hooks may overwrite or compound it. | Reject: conflicts with “not just a smaller player” and risks render-only effects beyond intended geometry. |
| **6. Replace/modify skin textures or use a resource pack/model JSON** | Could be useful for static texture details; no per-frame model mutation for texture-only changes. | A skin texture cannot change geometry or pivots. A resource pack cannot target arbitrary player proportions while preserving all dynamic animation and cosmetic layers. Provider skins and Just Expressions must continue to load. | Insufficient for the core transformation. |
| **7. Raw render-event/custom vertex rendering outside the player renderer** | Maximum low-level control and possible renderer independence. | Bypasses or duplicates renderer-owned skin, animation, light, visibility, item, cape and cosmetic behavior; greater work and conflict risk. Raw graphics paths also age poorly as rendering backends evolve. | Reject for a player morph. |

## Recommended long-term architecture

### V1: preserve the vanilla avatar pipeline

1. **Selection policy:** Resolve Everyone Normal / Everyone Chibi / Only Me / custom list using stable player identity, with the policy local to this client. Cache lookup data outside per-frame rendering. Do not transmit it.
2. **Keep the vanilla avatar renderer and model instances.** Do not replace renderer registration or create a second player entity. Keep the existing player render state and active skin/texture path, including slim/classic arms.
3. **Transform the existing model hierarchy.** Preserve the head, body, arm, and leg part objects and their animation-produced rotations. Apply the agreed Chibi proportions and corrected pivots at one narrow, player-only integration point. Keep inner and outer skin layers aligned. Preserve body-to-limb attachment relationships.
4. **Treat attachments as transforms of the same rig.** Held-item rendering should consume the final animated hand transform. Cape and cosmetic layers should continue using the vanilla render-layer chain and torso anchor. Head-attached features should consume the transformed head anchor. Avoid drawing replacement hands/capes/items as a second pass.
5. **Use state-safe changes.** Prefer decisions and immutable transform parameters associated with a player render state over reading mutable world entities during rendering. If model fields must be temporarily changed, establish exact entry/exit points and restore them on every path; do not allow state to leak to another entity/frame. Recheck assumptions against Minecraft's render-state/submit pipeline for the chosen version.
6. **Use optional adapters only for verified exceptions.** Start with zero hard dependencies on the named animation/cosmetic/cape/skin mods. Add isolated compatibility code only when a reproducible test shows a mod does not follow the vanilla player model/layer contract. If an API exists upstream, prefer that API to a private mixin target.
7. **Keep gameplay untouched.** Do not change entity dimensions, collision, camera eye height, server state, packets, or player model data. Avoid changing nameplate/shadow size unless explicitly chosen as a rendering requirement.
8. **Version and mapping discipline.** Pin the supported Minecraft/Fabric API range. Revalidate all mixin targets per supported release. A target mismatch must fail visibly in development and be covered by a startup/integration check, while optional mod adapters must not crash installations where the target mod is absent.

### Why this is the best starting point

The Fabric API offers a public callback for adding render layers, but its documented/current implementation is a layer-registration hook. It does not expose an equivalent public API for replacing the built-in player's body proportions. Fabric's 26.3 implementation wires layer registration around renderer creation and specifically includes avatar renderers. That makes it useful for cooperating additions, not a complete Chibi implementation ([Fabric 26.3 layer-registration implementation](https://github.com/FabricMC/fabric/blob/26.3/fabric-rendering-v1/src/client/java/net/fabricmc/fabric/mixin/client/rendering/EntityRenderersMixin.java)).

Modern Minecraft also uses extracted render state and submitted render commands; current Fabric rendering documentation warns that rendering work has moved toward render states and command submission, and the 26.3 Fabric source identifies the dedicated `AvatarRenderer` and `AvatarRenderState` path. This is another reason not to design around an old immediate-mode render callback or uncontrolled live-entity lookup ([Fabric rendering concepts](https://docs.fabricmc.net/develop/rendering/basic-concepts), [Fabric 26.3 entity-renderer integration](https://github.com/FabricMC/fabric/blob/26.3/fabric-rendering-v1/src/client/java/net/fabricmc/fabric/mixin/client/rendering/EntityRenderersMixin.java)).

The recommendation is an architecture direction, not a claim that one universal hook automatically works with every named mod. The exact safest injection point must be proven against the selected 26.3 mappings/source and the named mods' actual renderer/model behavior before implementation.

## Compatibility risk map

| Area | Likely preserved by vanilla renderer/model reuse | Remaining risk / validation needed |
|---|---|---|
| Mojang, SkinRestorer, Ely.by, offline/custom skins | Normal active player texture selection stays in the game's renderer/state path. | Provider may replace the renderer/texture path or expect vanilla model dimensions. Test actual providers and skin variants. |
| Punchy, Punchy Moves/Studio, SmoothPlayerAnimations, Touch'n Motion | Existing animated part objects and renderer remain available. | Model replacement, direct field mutation, injection order, pose updates after Chibi transforms, or assumptions about default pivots can conflict. Test each mod/version and animation states. |
| Fresh Animations Player Extension | The normal resource/animation pipeline and skin UVs remain. | Custom geometry/part names and pack-specific pivots may not match; verify with/without its required pack setup. |
| Just Expressions | The active skin and UV mapping remain, and face features attached to the vanilla head can follow its transform. | Expressions may be texture UV features or geometry anchored to assumed head proportions. Test all expressions, both skin models, and outer head layer. |
| Emotecraft, Essential, Cosmetica | Existing renderer feature/cosmetic path remains present. | Emote rigs may assume vanilla pivots; cosmetics may attach to head/body/limb by fixed offsets or use their own renderer. Test representative head/body/hand/back items and emotes. |
| Better Capes, Wavey Capes, Vanilla Capes | Existing cape layer and cape physics input remain in renderer flow. | Cape mods can submit their own geometry and bypass vanilla layer anchors. Test each provider with movement, crouch, flight, and emotes. |
| Held items | Existing hand layer and animated arm parts remain. | Correctness depends on the final hand transform matching shortened/scaled arm geometry, including bow/crossbow/map/shield/spyglass poses. Test all vanilla use poses. |
| Multiplayer | No gameplay state, dimensions, packets, or server mod required. | Per-client choices are intentionally not synchronized. Keep the local-only semantics clear. |

No project can responsibly promise perfect compatibility with every version of every third-party mod in advance. Record exact tested game/mod versions and describe support in those terms.

## Supplied modpack inspection and MVP status

The supplied CurseForge export targets Minecraft 26.3 with Fabric Loader 0.19.5. Its active resource pack list includes Just Expressions, Fresh Animations, FA+Player, and multiple Punchy packs. Its mods include Punchy, SmoothPlayerAnimations, Not Enough Animations, EMF/ETF, Skin Restorer, and Better Capes. The SmoothPlayerAnimations configuration enables its body lean and CEM animation, while Not Enough Animations enables in-world item/map and animation smoothing features.

The export does not contain installed mod JARs, only CurseForge project/file IDs and overrides. The named pack does not appear to include Punchy Moves, Punchy Studio, Touch'n Motion, Wavey Capes, Essential, Cosmetica, or Emotecraft; those remain requested compatibility targets, but have not been exercised against this pack.

The first runnable MVP retains the built-in avatar renderer, applies a render-only outer pose scale, and enlarges the humanoid head while a model is submitted for an avatar state. This keeps vanilla skin lookup, player hitboxes, and server state unchanged. The current prototype applies to every rendered player and has no settings or per-player selection yet. Its proportions and attachment alignment still need visual checks in the supplied modpack.

## Research basis

- Fabric's 26.3 source shows player/avatar renderers are created through a distinct avatar path and the public layer-registration callback is applied to that renderer. [EntityRenderersMixin, Fabric API 26.3](https://github.com/FabricMC/fabric/blob/26.3/fabric-rendering-v1/src/client/java/net/fabricmc/fabric/mixin/client/rendering/EntityRenderersMixin.java)
- Fabric's 26.1 migration notes document the rendering callback rename to `LivingEntityRenderLayerRegistrationCallback`, indicating that APIs should be checked against the selected game branch rather than copied from older examples. [Fabric API porting notes](https://github.com/FabricMC/fabric-docs/blob/main/versions/26.1.2/develop/porting/fabric-api.md)
- Fabric's rendering concepts explain the move toward render-state extraction and command submission, including compatibility implications for render hooks. [Basic Rendering Concepts](https://docs.fabricmc.net/develop/rendering/basic-concepts)
- The previous artifact metadata and bytecode confirm a Fabric 26.3 client-only mod that scales existing `HumanoidModel` parts and contains explicit item, cape, Wavey Cape, and EMF mixins. This is a local artifact review, not an external source claim.

## Adopted architecture and current implementation

The implementation follows the recommendation above: it retains the vanilla player renderer and model, applies client-local visual profiles, and leaves gameplay dimensions and networking unchanged. The current source includes Vanilla and Chibi profiles, local configuration, and compatibility hooks for verified renderer conflicts. The design remains version-specific and should be revalidated when Minecraft or supported mods change.

This document is the original architecture research record, not a guarantee that every mod/version combination listed in the project requirements has been tested.

